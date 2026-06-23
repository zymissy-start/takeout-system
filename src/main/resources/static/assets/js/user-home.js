(function () {
  const state = {
    page: 1,
    size: 60,
    keyword: '',
    categoryId: '',
    sort: 'recommend',
    hasMore: false,
    products: [],
    merchants: [],
    selectedMerchantId: null,
    addresses: [],
    deliveryFee: 3.00
  };

  const foodEmoji = ['🍔', '🍗', '🍜', '🥤', '🍱', '🍕', '🥟', '🧋', '🌙', '🥣'];

  document.addEventListener('DOMContentLoaded', init);
  window.addEventListener('cart:change', renderCart);

  async function init() {
    bindEvents();
    await initUser();
    await Promise.all([loadCategories(), loadAddresses()]);
    await loadProducts(true);
    renderCart();
  }

  function bindEvents() {
    App.$('#searchBtn').addEventListener('click', () => {
      state.keyword = App.$('#keywordInput').value.trim();
      state.selectedMerchantId = null;
      loadProducts(true);
    });
    App.$('#keywordInput').addEventListener('keydown', e => {
      if (e.key === 'Enter') App.$('#searchBtn').click();
    });
    App.$('#sortSelect').addEventListener('change', e => {
      state.sort = e.target.value;
      state.selectedMerchantId = null;
      loadProducts(true);
    });
    App.$('#loadMoreBtn').addEventListener('click', () => loadProducts(false));
    App.$('#clearMerchantBtn').addEventListener('click', () => {
      state.selectedMerchantId = '';
      renderMerchants();
      renderProducts();
    });
    App.$('#clearCartBtn').addEventListener('click', () => {
      Cart.clear();
      App.toast('购物车已清空');
    });
    App.$('#checkoutBtn').addEventListener('click', openCheckout);
    App.$('#closeCheckoutBtn').addEventListener('click', closeCheckout);
    App.$('#submitOrderBtn').addEventListener('click', submitOrder);
    App.$all('.quick-item[data-category-name]').forEach(btn => {
      btn.addEventListener('click', () => selectCategoryByName(btn.dataset.categoryName));
    });
  }

  async function initUser() {
    if (!App.isLoggedIn()) {
      App.$('#userAvatar').textContent = '登';
      const avatarLink = document.querySelector('.avatar-link');
      if (avatarLink) avatarLink.title = '登录后进入个人中心';
      return;
    }
    const user = await App.loadCurrentUser();
    const name = App.getField(user, ['realName', 'real_name', 'username'], '我');
    App.$('#userAvatar').textContent = String(name).slice(0, 1);
  }

  async function loadCategories() {
    try {
      const list = await App.request('/api/categories');
      renderCategories(Array.isArray(list) ? list : []);
    } catch (e) {
      renderCategories(App.mock && App.mock.categories ? App.mock.categories : []);
      App.toast(e.message || '分类加载失败，已使用本地演示数据');
    }
  }

  function renderCategories(categories) {
    const box = App.$('#categoryTabs');
    const all = [{ categoryId: '', categoryName: '全部' }].concat(categories.map(c => ({
      categoryId: App.getField(c, ['categoryId', 'category_id'], ''),
      categoryName: App.getField(c, ['categoryName', 'category_name', 'name'], '分类')
    })));
    box.innerHTML = all.map(c => {
      const isActive = String(state.categoryId || '') === String(c.categoryId || '');
      return `<button type="button" data-id="${c.categoryId}" class="${isActive ? 'active' : ''}" aria-pressed="${isActive}">${App.escapeHtml(c.categoryName)}</button>`;
    }).join('');
    box.querySelectorAll('button').forEach(btn => {
      btn.addEventListener('click', () => {
        state.categoryId = btn.dataset.id || '';
        state.selectedMerchantId = null;
        updateCategoryActive();
        loadProducts(true);
      });
    });
    updateCategoryActive();
  }

  function updateCategoryActive() {
    App.$all('#categoryTabs button').forEach(btn => {
      const isActive = String(state.categoryId || '') === String(btn.dataset.id || '');
      btn.classList.toggle('active', isActive);
      btn.setAttribute('aria-pressed', String(isActive));
    });
  }

  function selectCategoryByName(name) {
    const tab = Array.from(App.$('#categoryTabs').querySelectorAll('button')).find(btn => btn.textContent.includes(name));
    if (tab) {
      state.categoryId = tab.dataset.id || '';
      state.selectedMerchantId = null;
      updateCategoryActive();
      loadProducts(true);
    } else {
      state.keyword = name;
      state.selectedMerchantId = null;
      App.$('#keywordInput').value = name;
      loadProducts(true);
    }
  }

  async function loadProducts(reset) {
    if (reset) {
      state.page = 1;
      state.products = [];
      App.$('#merchantList').innerHTML = `<div class="empty-state">正在加载商家...</div>`;
      App.$('#productList').innerHTML = `<div class="empty-state">正在加载商品...</div>`;
    }
    const params = new URLSearchParams({ page: state.page, size: state.size, sort: state.sort });
    if (state.keyword) params.set('keyword', state.keyword);
    if (state.categoryId) params.set('categoryId', state.categoryId);
    try {
      const data = await App.request(`/api/user/products?${params.toString()}`);
      const rows = Array.isArray(data) ? data : (data.records || data.list || data.rows || []);
      state.hasMore = Array.isArray(data) ? rows.length >= state.size : Boolean(data.hasMore || (data.total && state.page * state.size < data.total));
      state.products = reset ? rows : state.products.concat(rows);
      state.page += 1;
      renderMerchants();
      renderProducts();
      App.$('#loadMoreBtn').classList.toggle('hidden', !state.hasMore);
    } catch (e) {
      App.$('#merchantList').innerHTML = `<div class="empty-state">${App.escapeHtml(e.message || '商家加载失败，请检查后端接口')}</div>`;
      App.$('#productList').innerHTML = '';
      App.$('#loadMoreBtn').classList.add('hidden');
    }
  }

  function buildMerchants(products) {
    const mockMap = new Map(((App.mock && App.mock.merchants) || []).map(m => [String(App.getField(m, ['merchantId', 'merchant_id', 'id'], '')), m]));
    const groups = new Map();

    products.forEach(product => {
      const merchantId = String(App.getField(product, ['merchantId', 'merchant_id'], ''));
      if (!merchantId) return;
      const base = mockMap.get(merchantId) || {};
      if (!groups.has(merchantId)) {
        groups.set(merchantId, {
          merchantId,
          merchantName: App.getField(base, ['merchantName', 'merchant_name', 'storeName'], App.getField(product, ['merchantName', 'merchant_name', 'storeName'], '附近商家')),
          merchantDesc: App.getField(base, ['merchantDesc', 'desc'], '热卖商品 · 即时配送'),
          rating: App.getField(base, ['rating'], '4.8'),
          distance: App.getField(base, ['distance'], '1km'),
          deliveryTime: App.getField(base, ['deliveryTime', 'delivery_time'], '约30分钟'),
          minOrder: App.getField(base, ['minOrder', 'min_order'], 0),
          deliveryFee: App.getField(base, ['deliveryFee', 'delivery_fee'], state.deliveryFee),
          logo: App.getField(base, ['logo'], '🍽️'),
          products: [],
          totalSales: 0,
          minPrice: Infinity
        });
      }
      const m = groups.get(merchantId);
      m.products.push(product);
      m.totalSales += Number(App.getField(product, ['monthlySales', 'monthly_sales', 'orderCount', 'order_count'], 0));
      m.minPrice = Math.min(m.minPrice, Number(App.getField(product, ['price'], 0)));
    });

    let merchants = Array.from(groups.values());
    if (state.sort === 'sales' || state.sort === 'recommend') merchants.sort((a, b) => b.totalSales - a.totalSales);
    if (state.sort === 'priceAsc') merchants.sort((a, b) => a.minPrice - b.minPrice);
    if (state.sort === 'priceDesc') merchants.sort((a, b) => b.minPrice - a.minPrice);
    return merchants;
  }

  function renderMerchants() {
    const box = App.$('#merchantList');
    state.merchants = buildMerchants(state.products);

    if (!state.merchants.length) {
      state.selectedMerchantId = '';
      box.innerHTML = `<div class="empty-state">没有找到商家，换个关键词试试</div>`;
      return;
    }

    const exists = state.selectedMerchantId !== null && state.selectedMerchantId !== '' && state.merchants.some(m => String(m.merchantId) === String(state.selectedMerchantId));
    if (state.selectedMerchantId === null) state.selectedMerchantId = String(state.merchants[0].merchantId);
    if (state.selectedMerchantId !== '' && state.selectedMerchantId !== null && !exists) state.selectedMerchantId = String(state.merchants[0].merchantId);

    box.innerHTML = state.merchants.map(m => `
      <article class="merchant-card ${String(m.merchantId) === String(state.selectedMerchantId) ? 'active' : ''}" data-id="${App.escapeHtml(m.merchantId)}">
        <div class="merchant-logo">${App.escapeHtml(m.logo || '🍽️')}</div>
        <div class="merchant-main">
          <div class="merchant-title-row">
            <h3>${App.escapeHtml(m.merchantName)}</h3>
            <span>${m.deliveryTime}</span>
          </div>
          <p>${App.escapeHtml(m.merchantDesc)}</p>
          <div class="merchant-meta">
            <span>⭐ ${App.escapeHtml(m.rating)}</span>
            <span>月售 ${m.totalSales}</span>
            <span>${App.escapeHtml(m.distance)}</span>
            <span>${m.deliveryFee ? `配送费 ${App.formatMoney(m.deliveryFee)}` : '免配送费'}</span>
          </div>
          <div class="merchant-products-preview">${m.products.slice(0, 3).map(p => `<em>${App.escapeHtml(App.getField(p, ['name', 'productName'], '商品'))}</em>`).join('')}</div>
        </div>
      </article>`).join('');

    box.querySelectorAll('.merchant-card').forEach(card => {
      card.addEventListener('click', () => {
        state.selectedMerchantId = card.dataset.id;
        renderMerchants();
        renderProducts();
      });
    });
  }

  function renderProducts() {
    const box = App.$('#productList');
    const merchant = state.merchants.find(m => String(m.merchantId) === String(state.selectedMerchantId));
    const products = merchant ? state.products.filter(p => String(App.getField(p, ['merchantId', 'merchant_id'], '')) === String(merchant.merchantId)) : state.products;

    App.$('#merchantProductTitle').textContent = merchant ? `${merchant.merchantName} · 店内商品` : '全部商品';
    App.$('#merchantProductDesc').textContent = merchant ? `${merchant.merchantDesc}，共 ${products.length} 个商品` : '所有商家的可选商品';
    App.$('#clearMerchantBtn').classList.toggle('hidden', !merchant);

    if (!products.length) {
      box.innerHTML = `<div class="empty-state">当前商家暂无商品</div>`;
      return;
    }

    box.innerHTML = products.map((p, index) => {
      const productId = App.getField(p, ['productId', 'product_id', 'id'], '');
      const name = App.getField(p, ['name', 'productName'], '未命名商品');
      const desc = App.getField(p, ['description', 'desc'], '商家暂未填写介绍');
      const price = App.getField(p, ['price'], 0);
      const image = App.getField(p, ['imageUrl', 'image_url'], '');
      const merchantName = App.getField(p, ['merchantName', 'merchant_name', 'storeName'], '附近商家');
      const sales = App.getField(p, ['monthlySales', 'monthly_sales', 'orderCount', 'order_count'], 0);
      return `
        <article class="product-card" data-id="${productId}">
          ${image ? `<img class="product-img" src="${App.escapeHtml(image)}" alt="${App.escapeHtml(name)}" onerror="this.outerHTML='<div class=&quot;product-placeholder&quot;>${foodEmoji[index % foodEmoji.length]}</div>'" />` : `<div class="product-placeholder">${foodEmoji[index % foodEmoji.length]}</div>`}
          <div class="product-info">
            <h3>${App.escapeHtml(name)}</h3>
            <p>${App.escapeHtml(desc)}</p>
            <div class="product-meta"><span>${App.escapeHtml(merchantName)}</span><span>月售 ${sales}</span></div>
            <div class="price-line"><span class="price">${App.formatMoney(price)}</span><button class="add-btn" data-id="${productId}">+</button></div>
          </div>
        </article>`;
    }).join('');

    box.querySelectorAll('.add-btn').forEach(btn => {
      btn.addEventListener('click', e => {
        if (!App.requireLogin('请先登录，登录后才能加入购物车和下单。', { redirect: true, auto: false, closable: true })) return;
        const product = state.products.find(item => String(App.getField(item, ['productId', 'product_id', 'id'], '')) === String(e.currentTarget.dataset.id));
        try {
          Cart.add(product, 1);
          App.toast('已加入购物车');
        } catch (err) {
          App.toast(err.message);
        }
      });
    });
  }

  async function loadAddresses() {
    const addressBtn = App.$('#addressBtn');
    if (!App.isLoggedIn()) {
      state.addresses = [];
      addressBtn.textContent = '选择收货地址';
      addressBtn.onclick = () => App.requireLogin('请先登录，登录后才能管理收货地址。', { redirect: true, auto: false, closable: true });
      return;
    }
    try {
      const list = await App.request('/api/user/addresses');
      state.addresses = Array.isArray(list) ? list : [];
      const defaultAddr = state.addresses.find(a => Number(App.getField(a, ['isDefault', 'is_default'], 0)) === 1) || state.addresses[0];
      addressBtn.textContent = defaultAddr ? App.getField(defaultAddr, ['addressDetail', 'address_detail', 'address'], '选择地址') : '新增收货地址';
    } catch (e) {
      state.addresses = [];
      addressBtn.textContent = '新增收货地址';
    }
    addressBtn.onclick = () => { window.location.href = '/user/profile.html'; };
  }

  function renderCart() {
    const items = Cart.readCart();
    const box = App.$('#cartItems');
    if (!items.length) {
      box.classList.add('empty');
      box.innerHTML = '购物车还是空的';
    } else {
      box.classList.remove('empty');
      box.innerHTML = items.map(item => `
        <div class="cart-row">
          <div><b>${App.escapeHtml(item.name)}</b><span>${App.formatMoney(item.price)} × ${item.quantity}</span></div>
          <div class="qty-box">
            <button class="qty-btn" data-id="${item.productId}" data-delta="-1">−</button>
            <strong>${item.quantity}</strong>
            <button class="qty-btn" data-id="${item.productId}" data-delta="1">+</button>
          </div>
        </div>`).join('');
      box.querySelectorAll('.qty-btn').forEach(btn => btn.addEventListener('click', () => Cart.change(btn.dataset.id, Number(btn.dataset.delta))));
    }
    App.$('#cartTotal').textContent = App.formatMoney(Cart.goodsAmount());
  }

  function openCheckout() {
    if (!App.requireLogin('请先登录，登录后才能结算和提交订单。', { redirect: true, auto: false, closable: true })) return;
    const items = Cart.readCart();
    if (!items.length) return App.toast('请先选择商品');
    if (!state.addresses.length) return App.toast('请先到个人中心新增收货地址');
    const select = App.$('#addressSelect');
    select.innerHTML = state.addresses.map(addr => {
      const id = App.getField(addr, ['addressId', 'address_id', 'id'], '');
      const name = App.getField(addr, ['receiverName', 'receiver_name'], '收货人');
      const phone = App.getField(addr, ['receiverPhone', 'receiver_phone'], '');
      const address = App.getField(addr, ['addressDetail', 'address_detail', 'address'], '');
      const selected = Number(App.getField(addr, ['isDefault', 'is_default'], 0)) === 1 ? 'selected' : '';
      return `<option value="${id}" ${selected}>${App.escapeHtml(`${name} ${phone} ${address}`)}</option>`;
    }).join('');
    const goods = Cart.goodsAmount();
    App.$('#modalGoodsAmount').textContent = App.formatMoney(goods);
    App.$('#modalDeliveryFee').textContent = App.formatMoney(state.deliveryFee);
    App.$('#modalPayAmount').textContent = App.formatMoney(goods + state.deliveryFee);
    App.$('#checkoutMask').classList.remove('hidden');
  }

  function closeCheckout() { App.$('#checkoutMask').classList.add('hidden'); }

  async function submitOrder() {
    if (!App.requireLogin('请先登录，登录后才能提交订单。', { redirect: true, auto: false, closable: true })) return;
    const items = Cart.readCart();
    if (!items.length) return App.toast('购物车为空');
    const addressId = App.$('#addressSelect').value;
    const address = state.addresses.find(a => String(App.getField(a, ['addressId', 'address_id', 'id'], '')) === String(addressId));
    const payload = {
      addressId: addressId ? Number(addressId) : null,
      address: address ? App.getField(address, ['addressDetail', 'address_detail', 'address'], '') : '',
      remark: App.$('#remarkInput').value.trim(),
      merchantId: items[0].merchantId,
      deliveryFee: state.deliveryFee,
      items: items.map(item => ({ productId: item.productId, quantity: item.quantity }))
    };
    try {
      App.$('#submitOrderBtn').disabled = true;
      const order = await App.request('/api/user/orders', { method: 'POST', body: payload });
      Cart.clear();
      closeCheckout();
      App.toast('下单成功');
      const orderId = App.getField(order || {}, ['orderId', 'order_id'], '');
      setTimeout(() => { window.location.href = `/user/order.html${orderId ? `?orderId=${orderId}` : ''}`; }, 700);
    } catch (e) {
      App.toast(e.message || '下单失败');
    } finally {
      App.$('#submitOrderBtn').disabled = false;
    }
  }
})();
