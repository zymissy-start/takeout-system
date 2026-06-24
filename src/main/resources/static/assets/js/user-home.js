(function () {
  const state = {
    page: 1,
    size: 12,
    keyword: '',
    categoryId: '',
    sort: 'recommend',
    hasMore: false,
    products: [],
    addresses: [],
    deliveryFee: 3.00,
    level: null
  };

  const foodEmoji = ['🍔', '🍗', '🍜', '🥤', '🍱', '🍕', '🥟', '🧋'];

  document.addEventListener('DOMContentLoaded', init);
  window.addEventListener('cart:change', renderCart);

  async function init() {
    bindEvents();
    await initUser();
    await Promise.all([loadLevel(), loadCategories(), loadAddresses()]);
    await loadProducts(true);
    renderCart();
  }

  function bindEvents() {
    App.$('#searchBtn').addEventListener('click', () => {
      state.keyword = App.$('#keywordInput').value.trim();
      loadProducts(true);
    });
    App.$('#keywordInput').addEventListener('keydown', e => { if (e.key === 'Enter') App.$('#searchBtn').click(); });
    App.$('#sortSelect').addEventListener('change', e => { state.sort = e.target.value; loadProducts(true); });
    App.$('#loadMoreBtn').addEventListener('click', () => loadProducts(false));
    App.$('#clearCartBtn').addEventListener('click', () => { Cart.clear(); App.toast('购物车已清空'); });
    App.$('#checkoutBtn').addEventListener('click', openCheckout);
    App.$('#closeCheckoutBtn').addEventListener('click', closeCheckout);
    App.$('#submitOrderBtn').addEventListener('click', submitOrder);
    App.$all('.quick-item[data-category-name]').forEach(btn => btn.addEventListener('click', () => selectCategoryByName(btn.dataset.categoryName)));
  }

  async function initUser() {
    const user = await App.loadCurrentUser();
    const name = App.getField(user, ['realName', 'real_name', 'username'], '我');
    App.$('#userAvatar').textContent = String(name).slice(0, 1);
  }

  async function loadLevel() {
    try {
      const level = await App.request('/api/user/level');
      state.level = level;
      const levelName = App.getField(level, ['levelName', 'level_name'], 'Lv1 普通用户');
      App.$('#levelBadge').textContent = levelName.replace('普通用户', '').trim();
      const rate = Number(App.getField(level, ['deliveryDiscountRate', 'delivery_discount_rate'], 1));
      const cooldown = App.getField(level, ['remindCooldownSeconds', 'remind_cooldown_seconds'], 180);
      App.$('#levelTip').textContent = rate < 1 ? `当前${levelName}：配送费${Math.round(rate * 100)}折，催单冷却${cooldown}秒。` : `当前${levelName}：完成订单和评价可提升等级。`;
    } catch (e) {
      App.$('#levelBadge').textContent = 'Lv1';
    }
  }

  async function loadCategories() {
    try {
      const list = await App.request('/api/categories');
      renderCategories(Array.isArray(list) ? list : []);
    } catch (e) {
      renderCategories([]);
      App.toast(e.message || '分类加载失败');
    }
  }

  function renderCategories(categories) {
    const box = App.$('#categoryTabs');
    const all = [{ categoryId: '', categoryName: '全部' }].concat(categories.map(c => ({
      categoryId: App.getField(c, ['categoryId', 'category_id'], ''),
      categoryName: App.getField(c, ['categoryName', 'category_name', 'name'], '分类')
    })));
    box.innerHTML = all.map(c => `<button data-id="${c.categoryId}" class="${String(state.categoryId) === String(c.categoryId) ? 'active' : ''}">${App.escapeHtml(c.categoryName)}</button>`).join('');
    box.querySelectorAll('button').forEach(btn => btn.addEventListener('click', () => { state.categoryId = btn.dataset.id; loadProducts(true); }));
  }

  function selectCategoryByName(name) {
    const tab = Array.from(App.$('#categoryTabs').querySelectorAll('button')).find(btn => btn.textContent.includes(name));
    if (tab) {
      state.categoryId = tab.dataset.id;
      loadProducts(true);
    } else {
      state.keyword = name;
      App.$('#keywordInput').value = name;
      loadProducts(true);
    }
  }

  async function loadProducts(reset) {
    if (reset) {
      state.page = 1;
      state.products = [];
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
      renderProducts();
      state.page += 1;
    } catch (e) {
      App.$('#productList').innerHTML = `<div class="empty-state">${App.escapeHtml(e.message || '商品加载失败，请检查后端接口')}</div>`;
      App.$('#loadMoreBtn').classList.add('hidden');
    }
  }

  function renderProducts() {
    const box = App.$('#productList');
    if (!state.products.length) {
      box.innerHTML = `<div class="empty-state">没有找到商品，换个关键词试试</div>`;
      App.$('#loadMoreBtn').classList.add('hidden');
      return;
    }
    box.innerHTML = state.products.map((p, index) => {
      const productId = App.getField(p, ['productId', 'product_id', 'id'], '');
      const name = App.getField(p, ['name', 'productName'], '未命名商品');
      const desc = App.getField(p, ['description', 'desc'], '商家暂未填写介绍');
      const price = App.getField(p, ['price'], 0);
      const image = App.getField(p, ['imageUrl', 'image_url'], '');
      const merchantName = App.getField(p, ['merchantName', 'merchant_name', 'storeName'], '校园商家');
      const sales = App.getField(p, ['monthlySales', 'monthly_sales', 'orderCount', 'order_count'], 0);
      const rating = App.getField(p, ['rating'], 4.8);
      const stock = App.getField(p, ['stock'], 0);
      const tag = App.getField(p, ['tag'], '');
      return `
        <article class="product-card" data-id="${productId}">
          ${image ? `<img class="product-img" src="${App.escapeHtml(image)}" alt="${App.escapeHtml(name)}" onerror="this.outerHTML='<div class=&quot;product-placeholder&quot;>${foodEmoji[index % foodEmoji.length]}</div>'" />` : `<div class="product-placeholder">${foodEmoji[index % foodEmoji.length]}</div>`}
          <div class="product-info">
            <h3>${App.escapeHtml(name)} ${tag ? `<span class="tag-pill">${App.escapeHtml(tag)}</span>` : ''}</h3>
            <p>${App.escapeHtml(desc)}</p>
            <div class="product-meta"><span>${App.escapeHtml(merchantName)}</span><span>⭐ ${rating} 月售 ${sales}</span></div>
            <div class="price-line"><span class="price">${App.formatMoney(price)}</span><button class="add-btn" data-id="${productId}" ${Number(stock) <= 0 ? 'disabled' : ''}>${Number(stock) <= 0 ? '售罄' : '+'}</button></div>
          </div>
        </article>`;
    }).join('');
    box.querySelectorAll('.add-btn').forEach(btn => btn.addEventListener('click', e => {
      const product = state.products.find(item => String(App.getField(item, ['productId', 'product_id', 'id'], '')) === String(e.currentTarget.dataset.id));
      try { Cart.add(product, 1); App.toast('已加入购物车'); } catch (err) { App.toast(err.message); }
    }));
    App.$('#loadMoreBtn').classList.toggle('hidden', !state.hasMore);
  }

  async function loadAddresses() {
    try {
      const list = await App.request('/api/user/addresses');
      state.addresses = Array.isArray(list) ? list : [];
      const defaultAddr = state.addresses.find(a => Number(App.getField(a, ['isDefault', 'is_default'], 0)) === 1) || state.addresses[0];
      const text = defaultAddr ? App.getField(defaultAddr, ['addressDetail', 'address_detail', 'address'], '选择地址') : '新增收货地址';
      App.$('#addressBtn').textContent = text.length > 18 ? text.slice(0, 18) + '...' : text;
    } catch (e) {
      state.addresses = [];
      App.$('#addressBtn').textContent = '新增收货地址';
    }
    App.$('#addressBtn').onclick = () => { window.location.href = '/user/profile.html'; };
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
    const items = Cart.readCart();
    if (!items.length) return App.toast('请先选择商品');
    if (!state.addresses.length) return App.toast('请先到个人中心新增收货地址');
    const select = App.$('#addressSelect');
    select.innerHTML = state.addresses.map(addr => {
      const id = App.getField(addr, ['addressId', 'address_id', 'id'], '');
      const name = App.getField(addr, ['receiverName', 'receiver_name'], '收货人');
      const phone = App.getField(addr, ['receiverPhone', 'receiver_phone'], '');
      const address = App.getField(addr, ['addressDetail', 'address_detail', 'address'], '');
      const lat = App.getField(addr, ['latitude'], '');
      const lng = App.getField(addr, ['longitude'], '');
      const selected = Number(App.getField(addr, ['isDefault', 'is_default'], 0)) === 1 ? 'selected' : '';
      const suffix = lat && lng ? '已定位' : '未定位';
      return `<option value="${id}" ${selected}>${App.escapeHtml(`${name} ${phone} ${address}（${suffix}）`)}</option>`;
    }).join('');
    const goods = Cart.goodsAmount();
    const first = items[0];
    state.deliveryFee = Number(first.deliveryFee || first.delivery_fee || state.deliveryFee || 3);
    const rate = state.level ? Number(App.getField(state.level, ['deliveryDiscountRate', 'delivery_discount_rate'], 1)) : 1;
    const discount = Math.max(0, state.deliveryFee - state.deliveryFee * rate);
    App.$('#modalGoodsAmount').textContent = App.formatMoney(goods);
    App.$('#modalDeliveryFee').textContent = App.formatMoney(state.deliveryFee);
    App.$('#modalDiscountAmount').textContent = '-' + App.formatMoney(discount);
    App.$('#modalPayAmount').textContent = App.formatMoney(goods + state.deliveryFee - discount);
    App.$('#checkoutLevelTip').textContent = state.level ? `当前${App.getField(state.level, ['levelName','level_name'], '等级')}享受配送费权益，最终金额以后端计算为准。` : '最终金额以后端事务计算为准。';
    App.$('#checkoutMask').classList.remove('hidden');
  }

  function closeCheckout() { App.$('#checkoutMask').classList.add('hidden'); }

  async function submitOrder() {
    const items = Cart.readCart();
    if (!items.length) return App.toast('购物车为空');
    const addressId = App.$('#addressSelect').value;
    const address = state.addresses.find(a => String(App.getField(a, ['addressId', 'address_id', 'id'], '')) === String(addressId));
    if (!address || !App.getField(address, ['latitude'], null) || !App.getField(address, ['longitude'], null)) {
      return App.toast('该地址未定位，请先到个人中心定位或地图选点');
    }
    const payload = {
      addressId: addressId ? Number(addressId) : null,
      remark: App.$('#remarkInput').value.trim(),
      items: items.map(item => ({ productId: Number(item.productId), quantity: Number(item.quantity) }))
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
