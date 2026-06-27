(function () {
  const state = {
    mode: 'merchants',
    selectedMerchant: null,
    merchantPage: 1,
    productPage: 1,
    size: 12,
    keyword: '',
    categoryId: '',
    sort: 'recommend',
    hasMore: false,
    merchants: [],
    products: [],
    addresses: [],
    deliveryFee: 3.00,
    level: null,
    favoriteMerchantIds: new Set(),
    coupons: [],
    selectedCouponId: null,
    reviews: [],
    showReviews: false
  };

  const foodEmoji = ['🍔', '🍗', '🍜', '🥤', '🍱', '🍕', '🥟', '🧋'];
  const storeEmoji = ['🏪', '🍔', '🍱', '🥤', '🍜', '🧋', '🥟', '🍕'];

  document.addEventListener('DOMContentLoaded', init);
  window.addEventListener('cart:change', renderCart);

  async function init() {
    bindEvents();
    await initUser();
    await Promise.all([loadLevel(), loadCategories(), loadAddresses(), loadFavoriteMerchantIds()]);
    await loadMerchants(true);
    openMerchantFromProfile();
    renderCart();
  }

  function openMerchantFromProfile() {
    try {
      const raw = localStorage.getItem('openMerchant');
      if (!raw) return;

      localStorage.removeItem('openMerchant');

      const merchant = JSON.parse(raw);
      const id = App.getField(merchant, ['merchantId', 'merchant_id', 'id'], null);

      if (!id) return;

      state.selectedMerchant = merchant;
      state.mode = 'products';
      loadProducts(true);
    } catch (e) {
      // 忽略非法缓存
    }
  }

  async function loadFavoriteMerchantIds() {
    try {
      const ids = await App.request('/api/user/favorites?type=merchant&idsOnly=true');
      state.favoriteMerchantIds = new Set((Array.isArray(ids) ? ids : []).map(id => String(id)));
    } catch (e) {
      state.favoriteMerchantIds = new Set();
    }
  }

  function bindEvents() {
    App.$('#searchBtn').addEventListener('click', () => {
      state.keyword = App.$('#keywordInput').value.trim();
      if (state.mode === 'products') loadProducts(true);
      else loadMerchants(true);
    });
    App.$('#keywordInput').addEventListener('keydown', e => { if (e.key === 'Enter') App.$('#searchBtn').click(); });
    App.$('#sortSelect').addEventListener('change', e => {
      state.sort = e.target.value;
      if (state.mode === 'products') loadProducts(true);
      else loadMerchants(true);
    });
    App.$('#loadMoreBtn').addEventListener('click', () => state.mode === 'products' ? loadProducts(false) : loadMerchants(false));
    App.$('#clearCartBtn').addEventListener('click', () => { Cart.clear(); App.toast('购物车已清空'); });
    App.$('#checkoutBtn').addEventListener('click', openCheckout);
    App.$('#closeCheckoutBtn').addEventListener('click', closeCheckout);
    App.$('#submitOrderBtn').addEventListener('click', submitOrder);
    App.$('#cartToggleBtn').addEventListener('click', toggleCart);
    const couponBtn = App.$('#couponPickerBtn');
    if (couponBtn) couponBtn.addEventListener('click', () => {
      const panel = App.$('#couponPanel');
      if (panel) panel.classList.toggle('hidden');
    });
    App.$all('.quick-item[data-category-name]').forEach(btn => btn.addEventListener('click', () => selectCategoryByName(btn.dataset.categoryName)));
  }

  function toggleCart() {
    const panel = App.$('#cartPanel');
    panel.classList.toggle('open');
    panel.classList.toggle('hidden', !panel.classList.contains('open'));
  }

  function closeCart() {
    const panel = App.$('#cartPanel');
    panel.classList.remove('open');
    panel.classList.add('hidden');
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
      const shortLevel = (levelName.match(/Lv\d+/i) || [levelName.split(/\s+/)[0]])[0];
      App.$('#levelBadge').textContent = shortLevel;
      const rate = Number(App.getField(level, ['deliveryDiscountRate', 'delivery_discount_rate'], 1));
      const cooldown = App.getField(level, ['remindCooldownSeconds', 'remind_cooldown_seconds'], 180);
      App.$('#levelTip').textContent = rate < 1 ? `当前${levelName}：配送费${Math.round(rate * 100)}折，高等级用户会匹配高等级骑手。` : `当前${levelName}：完成订单可提升用户权益，10单后优先匹配闪电侠骑手，15单后优先匹配单王配送骑手。`;
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
    const all = [{ categoryId: '', categoryName: '全部商店' }].concat(categories.map(c => ({
      categoryId: App.getField(c, ['categoryId', 'category_id'], ''),
      categoryName: App.getField(c, ['categoryName', 'category_name', 'name'], '分类')
    })));
    box.innerHTML = all.map(c => `<button data-id="${c.categoryId}" class="${String(state.categoryId) === String(c.categoryId) ? 'active' : ''}">${App.escapeHtml(c.categoryName)}</button>`).join('');
    box.querySelectorAll('button').forEach(btn => btn.addEventListener('click', () => {
      state.categoryId = btn.dataset.id;
      state.selectedMerchant = null;
      state.mode = 'merchants';
      loadMerchants(true);
    }));
  }

  function selectCategoryByName(name) {
    const tab = Array.from(App.$('#categoryTabs').querySelectorAll('button')).find(btn => btn.textContent.includes(name));
    state.selectedMerchant = null;
    state.mode = 'merchants';
    if (tab) {
      state.categoryId = tab.dataset.id;
      loadMerchants(true);
    } else {
      state.keyword = name;
      App.$('#keywordInput').value = name;
      loadMerchants(true);
    }
  }

  function setSectionText(title, subtitle) {
    const titleEl = App.$('#homeSectionTitle');
    const subEl = App.$('#homeSectionSub');
    if (titleEl) titleEl.textContent = title;
    if (subEl) subEl.textContent = subtitle;
  }

  async function loadMerchants(reset) {
    state.mode = 'merchants';
    if (reset) {
      state.merchantPage = 1;
      state.merchants = [];
      App.$('#productList').innerHTML = `<div class="empty-state">正在加载商店...</div>`;
    }
    setSectionText('优选商店', '先选择商店，再查看该商店商品');
    const params = new URLSearchParams({ page: state.merchantPage, size: state.size, sort: state.sort });
    if (state.keyword) params.set('keyword', state.keyword);
    if (state.categoryId) params.set('categoryId', state.categoryId);
    try {
      const data = await App.request(`/api/user/merchants?${params.toString()}`);
      const rows = Array.isArray(data) ? data : (data.records || data.list || data.rows || []);
      state.hasMore = Array.isArray(data) ? rows.length >= state.size : Boolean(data.hasMore || (data.total && state.merchantPage * state.size < data.total));
      state.merchants = reset ? rows : state.merchants.concat(rows);
      renderMerchants();
      state.merchantPage += 1;
    } catch (e) {
      App.$('#productList').innerHTML = `<div class="empty-state">${App.escapeHtml(e.message || '商店加载失败，请检查后端接口')}</div>`;
      App.$('#loadMoreBtn').classList.add('hidden');
    }
  }

  function renderMerchants() {
    const box = App.$('#productList');
    if (!state.merchants.length) {
      box.innerHTML = `<div class="empty-state">没有找到商店，换个关键词试试</div>`;
      App.$('#loadMoreBtn').classList.add('hidden');
      return;
    }
    box.innerHTML = state.merchants.map((m, index) => {
      const merchantId = App.getField(m, ['merchantId', 'merchant_id', 'id'], '');
      const name = App.getField(m, ['storeName', 'store_name', 'merchantName'], '商店');
      const logo = App.getField(m, ['storeLogo', 'store_logo'], '');
      const notice = App.getField(m, ['storeNotice', 'store_notice'], '欢迎光临本店');
      const rating = App.getField(m, ['rating'], 5);
      const sales = App.getField(m, ['monthlySales', 'monthly_sales'], 0);
      const productCount = App.getField(m, ['productCount', 'product_count'], 0);
      const minOrder = App.getField(m, ['minOrderAmount', 'min_order_amount'], 0);
      const deliveryFee = App.getField(m, ['deliveryFee', 'delivery_fee'], 3);
      const deliveryTime = App.getField(m, ['deliveryTime', 'delivery_time'], 30);
      const distance = App.getField(m, ['distanceKm', 'distance_km'], 1);
      const topProducts = App.getField(m, ['topProductNames', 'top_product_names'], '');
      const open = Number(App.getField(m, ['status'], 1)) === 1;
      return `
        <article class="product-card store-card" data-id="${MerchantSafe(merchantId)}">
          ${logo ? `<img class="product-img" src="${App.escapeHtml(logo)}" alt="${App.escapeHtml(name)}" onerror="this.outerHTML='<div class=&quot;product-placeholder&quot;>${storeEmoji[index % storeEmoji.length]}</div>'" />` : `<div class="product-placeholder">${storeEmoji[index % storeEmoji.length]}</div>`}
          <div class="product-info">
            <h3>${App.escapeHtml(name)} ${open ? '<span class="tag-pill">营业中</span>' : '<span class="tag-pill muted-pill">休息中</span>'}</h3>
            <p>${App.escapeHtml(notice)}</p>
            <div class="product-meta"><span>⭐ ${rating}</span><span>月售 ${sales}</span><span>${distance}km · ${deliveryTime}分钟</span></div>
            <div class="product-meta"><span>${productCount} 个商品</span><span>起送 ${App.formatMoney(minOrder)}</span><span>配送费 ${App.formatMoney(deliveryFee)}</span></div>
            ${topProducts ? `<div class="store-products-line">热卖：${App.escapeHtml(String(topProducts).split('、').slice(0, 3).join('、'))}</div>` : ''}
            <div class="price-line store-action-line">
                <button class="favorite-store-btn ${state.favoriteMerchantIds.has(String(merchantId)) ? 'active' : ''}" data-id="${MerchantSafe(merchantId)}" type="button">
                    ${state.favoriteMerchantIds.has(String(merchantId)) ? '★ 已收藏' : '☆ 收藏'}
                </button>
                <button class="ghost-btn enter-store-btn" data-id="${MerchantSafe(merchantId)}" ${open ? '' : 'disabled'}>进店</button>
            </div>
          </div>
        </article>`;
    }).join('');
    box.querySelectorAll('.store-card').forEach(el => el.addEventListener('click', e => {
      if (e.target.closest('button')) return;

      const id = e.currentTarget.dataset.id;
      const merchant = state.merchants.find(item =>
          String(App.getField(item, ['merchantId', 'merchant_id', 'id'], '')) === String(id)
      );

      if (merchant) enterMerchant(merchant);
    }));

    box.querySelectorAll('.enter-store-btn').forEach(el => el.addEventListener('click', e => {
      e.stopPropagation();

      const id = e.currentTarget.dataset.id;
      const merchant = state.merchants.find(item =>
          String(App.getField(item, ['merchantId', 'merchant_id', 'id'], '')) === String(id)
      );

      if (merchant) enterMerchant(merchant);
    }));

    box.querySelectorAll('.favorite-store-btn').forEach(el => {
      el.addEventListener('click', toggleFavoriteMerchant);
    });
    App.$('#loadMoreBtn').classList.toggle('hidden', !state.hasMore);
  }

  async function toggleFavoriteMerchant(event) {
    event.stopPropagation();

    const btn = event.currentTarget;
    const merchantId = btn.dataset.id;

    if (!merchantId) return;

    try {
      btn.disabled = true;

      const result = await App.request(
          `/api/user/favorites/merchants/${encodeURIComponent(merchantId)}/toggle`,
          { method: 'POST' }
      );

      const favorite = Boolean(App.getField(result, ['favorite'], false));

      if (favorite) {
        state.favoriteMerchantIds.add(String(merchantId));
      } else {
        state.favoriteMerchantIds.delete(String(merchantId));
      }

      btn.classList.toggle('active', favorite);
      btn.textContent = favorite ? '★ 已收藏' : '☆ 收藏';

      App.toast(App.getField(result, ['message'], favorite ? '收藏成功' : '已取消收藏'));
    } catch (e) {
      App.toast(e.message || '收藏操作失败');
    } finally {
      btn.disabled = false;
    }
  }

  function MerchantSafe(value) { return App.escapeHtml(value); }

  function enterMerchant(merchant) {
    state.selectedMerchant = merchant;
    state.mode = 'products';
    state.showReviews = false;
    state.reviews = [];
    state.keyword = App.$('#keywordInput').value.trim();
    loadProducts(true);
    loadMerchantReviews();
  }

  async function loadMerchantReviews() {
    if (!state.selectedMerchant) return;
    const merchantId = App.getField(state.selectedMerchant, ['merchantId', 'merchant_id', 'id'], '');
    if (!merchantId) return;
    try {
      const data = await App.request(`/api/user/merchants/${encodeURIComponent(merchantId)}/reviews`);
      state.reviews = Array.isArray(data) ? data : [];
    } catch (e) {
      state.reviews = [];
    }
    if (state.showReviews) renderReviewsSection();
  }

  function renderReviewsSection() {
    const el = App.$('#merchantReviewsSection');
    if (!el) return;
    const reviews = state.reviews;
    if (!reviews.length) {
      el.innerHTML = '<div class="reviews-header" id="reviewsToggle" style="cursor:pointer;padding:10px 0;"><b>📝 商家评价</b> <span class="muted small">(暂无评价，点击展开)</span></div>';
    } else {
      el.innerHTML = '<div class="reviews-header" id="reviewsToggle" style="cursor:pointer;padding:10px 0;"><b>📝 商家评价 (' + reviews.length + ')</b> <span class="muted small">' + (state.showReviews ? '点击收起' : '点击展开') + '</span></div>'
        + (state.showReviews ? '<div class="reviews-list">' + reviews.map(function(r) {
            var stars = '';
            for (var i = 0; i < 5; i++) stars += i < Number(r.score || 0) ? '★' : '☆';
            return '<div class="review-item"><div class="review-item-head"><b>' + App.escapeHtml(r.userName || '用户') + '</b><span class="review-stars">' + stars + '</span></div><p class="review-item-content">' + App.escapeHtml(r.content || '该用户未留下文字评价') + '</p><span class="muted small">' + App.escapeHtml(r.createTime || '') + '</span></div>';
          }).join('') + '</div>' : '');
    }
    var toggle = App.$('#reviewsToggle');
    if (toggle) toggle.addEventListener('click', function() {
      state.showReviews = !state.showReviews;
      renderReviewsSection();
    });
  }

  async function loadProducts(reset) {
    if (!state.selectedMerchant) return loadMerchants(true);
    state.mode = 'products';
    if (reset) {
      state.productPage = 1;
      state.products = [];
      App.$('#productList').innerHTML = `<div class="empty-state">正在加载商店商品...</div>`;
    }
    const merchantName = App.getField(state.selectedMerchant, ['storeName', 'store_name', 'merchantName'], '商店');
    setSectionText(merchantName, '当前只展示本商店商品，返回后可切换其他商店');
    const merchantId = App.getField(state.selectedMerchant, ['merchantId', 'merchant_id', 'id'], '');
    const params = new URLSearchParams({ page: state.productPage, size: state.size, sort: state.sort });
    if (state.keyword) params.set('keyword', state.keyword);
    if (state.categoryId) params.set('categoryId', state.categoryId);
    try {
      const data = await App.request(`/api/user/merchants/${encodeURIComponent(merchantId)}/products?${params.toString()}`);
      const rows = Array.isArray(data) ? data : (data.records || data.list || data.rows || []);
      state.hasMore = Array.isArray(data) ? rows.length >= state.size : Boolean(data.hasMore || (data.total && state.productPage * state.size < data.total));
      state.products = reset ? rows : state.products.concat(rows);
      renderProducts();
      state.productPage += 1;
    } catch (e) {
      App.$('#productList').innerHTML = `<div class="empty-state">${App.escapeHtml(e.message || '商品加载失败，请检查后端接口')}</div>`;
      App.$('#loadMoreBtn').classList.add('hidden');
    }
  }

  function renderProducts() {
    const box = App.$('#productList');
    const merchantName = state.selectedMerchant ? App.getField(state.selectedMerchant, ['storeName', 'store_name', 'merchantName'], '商店') : '商店';
    const backBar = `<div class="store-back-bar"><button id="backStoreBtn" class="ghost-btn">← 返回商店列表</button><b>${App.escapeHtml(merchantName)}</b></div>`;
    const reviewsSection = '<div id="merchantReviewsSection" style="padding:0 4px;border-bottom:1px solid var(--line);margin-bottom:10px;"></div>';
    if (!state.products.length) {
      box.innerHTML = `${backBar}${reviewsSection}<div class="empty-state">这个商店暂时没有符合条件的商品</div>`;
      App.$('#backStoreBtn').addEventListener('click', () => { state.selectedMerchant = null; loadMerchants(true); });
      App.$('#loadMoreBtn').classList.add('hidden');
      renderReviewsSection();
      return;
    }
    box.innerHTML = backBar + reviewsSection + state.products.map((p, index) => {
      const productId = App.getField(p, ['productId', 'product_id', 'id'], '');
      const name = App.getField(p, ['name', 'productName'], '未命名商品');
      const desc = App.getField(p, ['description', 'desc'], '商家暂未填写介绍');
      const price = App.getField(p, ['price'], 0);
      const image = App.getField(p, ['imageUrl', 'image_url'], '');
      const sales = App.getField(p, ['monthlySales', 'monthly_sales', 'orderCount', 'order_count'], 0);
      const rating = App.getField(p, ['rating'], 4.8);
      const stock = App.getField(p, ['stock'], 0);
      const tag = App.getField(p, ['tag'], '');
      return `
        <article class="product-card" data-id="${App.escapeHtml(productId)}">
          ${image ? `<img class="product-img" src="${App.escapeHtml(image)}" alt="${App.escapeHtml(name)}" onerror="this.outerHTML='<div class=&quot;product-placeholder&quot;>${foodEmoji[index % foodEmoji.length]}</div>'" />` : `<div class="product-placeholder">${foodEmoji[index % foodEmoji.length]}</div>`}
          <div class="product-info">
            <h3>${App.escapeHtml(name)} ${tag ? `<span class="tag-pill">${App.escapeHtml(tag)}</span>` : ''}</h3>
            <p>${App.escapeHtml(desc)}</p>
            <div class="product-meta"><span>${App.escapeHtml(merchantName)}</span><span>⭐ ${rating} 月售 ${sales}</span></div>
            <div class="price-line"><span class="price">${App.formatMoney(price)}</span><button class="add-btn" data-id="${App.escapeHtml(productId)}" ${Number(stock) <= 0 ? 'disabled' : ''}>${Number(stock) <= 0 ? '售罄' : '+'}</button></div>
          </div>
        </article>`;
    }).join('');
    App.$('#backStoreBtn').addEventListener('click', () => { state.selectedMerchant = null; loadMerchants(true); });
    box.querySelectorAll('.add-btn').forEach(btn => btn.addEventListener('click', e => {
      const product = state.products.find(item => String(App.getField(item, ['productId', 'product_id', 'id'], '')) === String(e.currentTarget.dataset.id));
      try { Cart.add(product, 1); App.toast('已加入购物车'); } catch (err) { App.toast(err.message); }
    }));
    App.$('#loadMoreBtn').classList.toggle('hidden', !state.hasMore);
    renderReviewsSection();
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
    const totalCount = items.reduce((s, i) => s + Number(i.quantity || 0), 0);
    const badge = App.$('#cartFabBadge');
    if (totalCount > 0) {
      badge.textContent = totalCount > 99 ? '99+' : totalCount;
      badge.classList.remove('hidden');
    } else {
      badge.classList.add('hidden');
      closeCart();
    }
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

  function cartMinOrderAmount(items) {
    return (items || []).reduce((max, item) => Math.max(max, Number(item.minOrderAmount || item.min_order_amount || 0)), 0);
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
    const minOrderAmount = cartMinOrderAmount(items);
    const first = items[0];
    state.deliveryFee = Number(first.deliveryFee || first.delivery_fee || state.deliveryFee || 3);
    state.selectedCouponId = null;
    loadCoupons();
    updateCheckoutTotal();
    App.$('#checkoutMask').classList.remove('hidden');
  }

  async function loadCoupons() {
    try {
      const list = await App.request('/api/user/coupons/my');
      state.coupons = (Array.isArray(list) ? list : []).filter(c => Number(App.getField(c, ['userCouponStatus', 'user_coupon_status'], -1)) === 0);
    } catch (e) { state.coupons = []; }
    renderCouponPicker();
  }

  function renderCouponPicker() {
    const panel = App.$('#couponPanel');
    const btn = App.$('#couponPickerBtn');
    const text = App.$('#couponPickerText');
    if (!panel || !btn) return;
    const goods = Cart.goodsAmount();
    const available = state.coupons.filter(c => {
      const minAmt = Number(App.getField(c, ['minAmount', 'min_amount'], 0));
      return goods >= minAmt;
    });
    if (!available.length) {
      panel.innerHTML = '<div class="coupon-option-none">暂无可用优惠券</div>';
      if (text) { text.textContent = '暂无可用'; text.parentElement.classList.remove('selected'); }
      state.selectedCouponId = null;
      updateCheckoutTotal();
      return;
    }
    let html = '<div class="coupon-option' + (!state.selectedCouponId ? ' active' : '') + '" data-cid="">';
    html += '<div class="cp-info"><b>不使用优惠券</b></div></div>';
    available.forEach(c => {
      const ucId = App.getField(c, ['userCouponId', 'user_coupon_id'], '');
      const amount = App.getField(c, ['amount'], 0);
      const title = App.getField(c, ['title'], '优惠券');
      const minAmt = App.getField(c, ['minAmount', 'min_amount'], 0);
      const active = String(state.selectedCouponId) === String(ucId) ? ' active' : '';
      html += `<div class="coupon-option${active}" data-cid="${ucId}">`;
      html += `<div class="cp-amount">¥${Number(amount).toFixed(0)}</div>`;
      html += `<div class="cp-info"><b>${App.escapeHtml(title)}</b><span>满${Number(minAmt).toFixed(0)}可用</span></div></div>`;
    });
    panel.innerHTML = html;
    panel.querySelectorAll('.coupon-option').forEach(opt => {
      opt.addEventListener('click', () => {
        const cid = opt.dataset.cid;
        state.selectedCouponId = cid ? Number(cid) : null;
        renderCouponPicker();
        updateCheckoutTotal();
      });
    });
    if (state.selectedCouponId) {
      const sel = available.find(c => String(App.getField(c, ['userCouponId', 'user_coupon_id'], '')) === String(state.selectedCouponId));
      if (sel && text) { text.textContent = `-¥${Number(App.getField(sel, ['amount'], 0)).toFixed(0)}`; text.parentElement.classList.add('selected'); }
    } else if (text) { text.textContent = available.length + '张可用'; text.parentElement.classList.remove('selected'); }
  }

  function updateCheckoutTotal() {
    const items = Cart.readCart();
    const goods = Cart.goodsAmount();
    const rate = state.level ? Number(App.getField(state.level, ['deliveryDiscountRate', 'delivery_discount_rate'], 1)) : 1;
    const discount = Math.max(0, state.deliveryFee - state.deliveryFee * rate);
    let couponDiscount = 0;
    if (state.selectedCouponId) {
      const sel = state.coupons.find(c => String(App.getField(c, ['userCouponId', 'user_coupon_id'], '')) === String(state.selectedCouponId));
      if (sel) couponDiscount = Number(App.getField(sel, ['amount'], 0));
    }
    const minOrderAmount = cartMinOrderAmount(items);
    const shortfall = Math.max(0, minOrderAmount - goods);
    App.$('#modalGoodsAmount').textContent = App.formatMoney(goods);
    App.$('#modalDeliveryFee').textContent = App.formatMoney(state.deliveryFee);
    App.$('#modalDiscountAmount').textContent = '-' + App.formatMoney(discount);
    const couponEl = App.$('#modalCouponDiscount');
    if (couponEl) couponEl.textContent = '-' + App.formatMoney(couponDiscount);
    const total = Math.max(0, goods + state.deliveryFee - discount - couponDiscount);
    App.$('#modalPayAmount').textContent = App.formatMoney(total);
    App.$('#submitOrderBtn').disabled = shortfall > 0;
    const levelTip = state.level ? `当前${App.getField(state.level, ['levelName','level_name'], '等级')}享受配送费权益，高等级用户会匹配高等级骑手。` : '最终金额以后端事务计算为准。';
    App.$('#checkoutLevelTip').textContent = shortfall > 0
      ? `未达到商家起送价 ${App.formatMoney(minOrderAmount)}，还差 ${App.formatMoney(shortfall)} 才能提交订单。`
      : `${levelTip} 已达到起送价，最终金额以后端计算为准。`;
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
    const minOrderAmount = cartMinOrderAmount(items);
    const goods = Cart.goodsAmount();
    if (goods < minOrderAmount) {
      return App.toast(`未达到商家起送价 ${App.formatMoney(minOrderAmount)}，还差 ${App.formatMoney(minOrderAmount - goods)}`);
    }
    const payload = {
      addressId: addressId ? Number(addressId) : null,
      remark: App.$('#remarkInput').value.trim(),
      items: items.map(item => ({ productId: Number(item.productId), quantity: Number(item.quantity) })),
      userCouponId: state.selectedCouponId || null
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
