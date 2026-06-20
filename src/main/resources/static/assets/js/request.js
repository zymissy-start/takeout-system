(function () {
  const API_BASE = ''; // 同域部署：SpringBoot 静态资源和接口在同一服务下，无需改动。
  const LOGIN_URL = '/login.html'; // 登录页路径。
  const USE_MOCK_WHEN_BACKEND_UNAVAILABLE = true; // 后端未完成时用于前端演示。后端完成后可改为 false。

  const mockCategories = [
    { categoryId: 1, categoryName: '美食' },
    { categoryId: 2, categoryName: '饮品' },
    { categoryId: 3, categoryName: '跑腿代购' },
    { categoryId: 4, categoryName: '夜宵' },
    { categoryId: 5, categoryName: '早餐' }
  ];

  const mockMerchants = [
    { merchantId: 3, merchantName: '汉堡王·东门店', merchantDesc: '汉堡炸鸡 · 快餐', rating: 4.8, distance: '0.6km', deliveryTime: '约25分钟', minOrder: 0, deliveryFee: 3, logo: '🍔' },
    { merchantId: 5, merchantName: '西苑快餐', merchantDesc: '盖饭套餐 · 家常菜', rating: 4.7, distance: '0.8km', deliveryTime: '约28分钟', minOrder: 12, deliveryFee: 2, logo: '🍱' },
    { merchantId: 6, merchantName: '茶语小站', merchantDesc: '奶茶果茶 · 下午茶', rating: 4.9, distance: '0.4km', deliveryTime: '约18分钟', minOrder: 8, deliveryFee: 2, logo: '🧋' },
    { merchantId: 7, merchantName: '帮帮跑腿', merchantDesc: '代取快递 · 生活服务', rating: 4.6, distance: '1.0km', deliveryTime: '约30分钟', minOrder: 5, deliveryFee: 0, logo: '🛍️' },
    { merchantId: 8, merchantName: '东门夜宵', merchantDesc: '夜宵小吃 · 炸物', rating: 4.7, distance: '1.2km', deliveryTime: '约32分钟', minOrder: 10, deliveryFee: 3, logo: '🌙' },
    { merchantId: 9, merchantName: '晨光早餐铺', merchantDesc: '早餐豆浆 · 包子油条', rating: 4.8, distance: '0.5km', deliveryTime: '约16分钟', minOrder: 6, deliveryFee: 1, logo: '🥣' }
  ];

  const mockProducts = [
    { productId: 101, merchantId: 3, categoryId: 1, name: '经典双层芝士汉堡', description: '芝士浓郁，牛肉多汁，适合午餐快速补能。', price: 25.00, imageUrl: '', merchantName: '汉堡王·东门店', monthlySales: 120 },
    { productId: 102, merchantId: 3, categoryId: 1, name: '香辣鸡腿堡', description: '外酥里嫩，微辣可口，搭配饮品更划算。', price: 18.50, imageUrl: '', merchantName: '汉堡王·东门店', monthlySales: 85 },
    { productId: 103, merchantId: 3, categoryId: 2, name: '冰镇可乐', description: '冰爽解腻，建议搭配汉堡套餐。', price: 6.00, imageUrl: '', merchantName: '汉堡王·东门店', monthlySales: 200 },
    { productId: 104, merchantId: 5, categoryId: 1, name: '招牌鸡排饭', description: '大块鸡排、时蔬和米饭，宿舍党晚餐选择。', price: 16.80, imageUrl: '', merchantName: '西苑快餐', monthlySales: 236 },
    { productId: 105, merchantId: 9, categoryId: 5, name: '豆浆油条套餐', description: '热豆浆搭配现炸油条，早八不空腹。', price: 8.90, imageUrl: '', merchantName: '晨光早餐铺', monthlySales: 168 },
    { productId: 106, merchantId: 6, categoryId: 2, name: '珍珠奶茶', description: '默认三分糖，支持备注冰量和糖度。', price: 12.00, imageUrl: '', merchantName: '茶语小站', monthlySales: 310 },
    { productId: 107, merchantId: 6, categoryId: 2, name: '柠檬冰茶', description: '清爽酸甜，适合下午自习提神。', price: 10.00, imageUrl: '', merchantName: '茶语小站', monthlySales: 154 },
    { productId: 108, merchantId: 7, categoryId: 3, name: '跑腿代取快递', description: '附近快递点代取，备注取件码和宿舍楼。', price: 5.00, imageUrl: '', merchantName: '帮帮跑腿', monthlySales: 98 },
    { productId: 109, merchantId: 8, categoryId: 4, name: '夜宵烤冷面', description: '酸甜口味，夜间学习补给。', price: 9.90, imageUrl: '', merchantName: '东门夜宵', monthlySales: 126 },
    { productId: 110, merchantId: 8, categoryId: 4, name: '炸鸡小食拼盘', description: '鸡米花、薯条、洋葱圈组合。', price: 19.90, imageUrl: '', merchantName: '东门夜宵', monthlySales: 141 }
  ];

  const mockAddresses = [
    { addressId: 1, receiverName: '张三', receiverPhone: '13800000000', addressDetail: '西苑校区 6 号宿舍楼 502', isDefault: 1 },
    { addressId: 2, receiverName: '张三', receiverPhone: '13800000000', addressDetail: '软件学院实验室楼下', isDefault: 0 }
  ];

  const mockOrders = [
    {
      orderId: 9001,
      merchantId: 3,
      merchantName: '汉堡王·东门店',
      status: 3,
      payAmount: 34.00,
      totalPrice: 34.00,
      orderTime: '2026-06-20 12:18:30',
      merchantConfirmTime: '2026-06-20 12:19:10',
      kitchenFinishTime: '2026-06-20 12:28:00',
      estimatedArrivalTime: '预计 12:45 送达',
      address: '西苑校区 6 号宿舍楼 502',
      remark: '少冰，电话联系',
      items: [
        { productId: 101, productName: '经典双层芝士汉堡', quantity: 1, price: 25.00 },
        { productId: 103, productName: '冰镇可乐', quantity: 1, price: 6.00 }
      ]
    },
    {
      orderId: 9002,
      merchantId: 6,
      merchantName: '茶语小站',
      status: 4,
      payAmount: 15.00,
      totalPrice: 15.00,
      orderTime: '2026-06-19 16:08:12',
      merchantConfirmTime: '2026-06-19 16:09:00',
      kitchenFinishTime: '2026-06-19 16:15:00',
      estimatedArrivalTime: '2026-06-19 16:30:00',
      finishTime: '2026-06-19 16:26:18',
      address: '软件学院实验室楼下',
      remark: '三分糖',
      items: [
        { productId: 106, productName: '珍珠奶茶', quantity: 1, price: 12.00 }
      ]
    }
  ];

  const MOCK_USERS_KEY = 'takeout_mock_users_v1';

  function defaultMockUsers() {
    return [
      { userId: 2, username: 'zhangsan', password: '123456', realName: '张三', phone: '13800000000', roleType: 1, creditScore: 10 },
      { userId: 10, username: 'lisi', password: '123456', realName: '李四', phone: '13900000000', roleType: 1, creditScore: 8 }
    ];
  }

  function readMockUsers() {
    try {
      const saved = JSON.parse(localStorage.getItem(MOCK_USERS_KEY) || '[]');
      if (Array.isArray(saved) && saved.length) return saved;
    } catch (e) {}
    const users = defaultMockUsers();
    localStorage.setItem(MOCK_USERS_KEY, JSON.stringify(users));
    return users;
  }

  function writeMockUsers(users) {
    localStorage.setItem(MOCK_USERS_KEY, JSON.stringify(users || []));
  }

  function publicUser(user) {
    if (!user) return {};
    const clone = Object.assign({}, user);
    delete clone.password;
    return clone;
  }

  function createToken(user) {
    return `mock-token-${user.userId || Date.now()}-${Date.now()}`;
  }

  function saveAuth(token, user) {
    if (token) localStorage.setItem('token', token);
    if (user) saveLocalUser(user);
  }

  function currentUserCartKey() {
    const user = getLocalUser();
    const uid = getField(user, ['userId', 'user_id', 'id', 'username'], '');
    return uid ? `takeout_user_cart_v1_${uid}` : '';
  }

  function clearCartCache() {
    const key = currentUserCartKey();
    if (key) localStorage.removeItem(key);
    localStorage.removeItem('takeout_user_cart_v1');
    window.dispatchEvent(new Event('cart:change'));
  }

  function clearAuthCache() {
    clearCartCache();
    localStorage.removeItem('token');
    localStorage.removeItem('Authorization');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userInfo');
  }

  function $(selector) { return document.querySelector(selector); }
  function $all(selector) { return Array.from(document.querySelectorAll(selector)); }

  function toast(message, type) {
    const el = $('#toast');
    if (!el) return alert(message);
    el.textContent = message || '';
    el.classList.remove('hidden');
    el.dataset.type = type || 'info';
    clearTimeout(window.__toastTimer);
    window.__toastTimer = setTimeout(() => el.classList.add('hidden'), 2200);
  }

  function getToken() {
    return localStorage.getItem('token') || localStorage.getItem('Authorization') || '';
  }

  function isLoggedIn() {
    return Boolean(getToken());
  }

  function getLocalUser() {
    try {
      return JSON.parse(localStorage.getItem('currentUser') || localStorage.getItem('userInfo') || '{}');
    } catch (e) {
      return {};
    }
  }

  function saveLocalUser(user) {
    localStorage.setItem('currentUser', JSON.stringify(user || {}));
  }

  function normalizeResult(json) {
    if (!json || typeof json !== 'object') return json;
    if ('code' in json) {
      const ok = json.code === 0 || json.code === 200 || json.code === '0' || json.code === '200';
      if (!ok) throw new Error(json.message || json.msg || '请求失败');
      return 'data' in json ? json.data : json;
    }
    if ('success' in json && !json.success) throw new Error(json.message || json.msg || '请求失败');
    return 'data' in json && Object.keys(json).length <= 3 ? json.data : json;
  }

  function isLoginPage() {
    const path = location.pathname.toLowerCase();
    return path.endsWith('/login.html') || path.endsWith('/login') || path.includes('/login/') || path.endsWith('/register.html');
  }

  function isProtectedPagePath(pathname) {
    const path = (pathname || location.pathname).toLowerCase();
    return path.endsWith('/user/order.html') || path.endsWith('/user/profile.html') || path.endsWith('/user/order') || path.endsWith('/user/profile');
  }

  function loginRedirectUrl() {
    const from = `${location.pathname}${location.search || ''}${location.hash || ''}`;
    return `${LOGIN_URL}?redirect=${encodeURIComponent(from)}`;
  }

  function ensureLoginPromptStyle() {
    if ($('#loginPromptStyle')) return;
    const style = document.createElement('style');
    style.id = 'loginPromptStyle';
    style.textContent = `
      #loginPromptMask {
        position: fixed;
        inset: 0;
        z-index: 9999;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px;
        background: rgba(15, 23, 42, .48);
        backdrop-filter: blur(10px);
      }
      .login-prompt-card {
        width: min(370px, 100%);
        padding: 26px 22px 22px;
        border-radius: 24px;
        background: #fff;
        box-shadow: 0 22px 70px rgba(15, 23, 42, .26);
        text-align: center;
        color: #1f2329;
        animation: loginPromptIn .18s ease-out;
      }
      .login-prompt-close {
        position: absolute;
        right: 14px;
        top: 12px;
        width: 34px;
        height: 34px;
        border: 0;
        border-radius: 999px;
        background: #f3f4f7;
        color: #606874;
        font-size: 22px;
        line-height: 1;
        cursor: pointer;
      }
      .login-prompt-close:hover { background: #e9edf3; color: #1f2329; }
      .login-prompt-card { position: relative; }
      .login-prompt-icon {
        width: 58px;
        height: 58px;
        margin: 0 auto 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 20px;
        background: linear-gradient(135deg, #fff4ad, #ffd100);
        font-size: 28px;
      }
      .login-prompt-card h3 { margin: 0 0 8px; font-size: 20px; }
      .login-prompt-card p { margin: 0; color: #70757f; font-size: 14px; line-height: 1.7; }
      .login-prompt-actions { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-top: 18px; }
      .login-prompt-actions button { border: 0; cursor: pointer; padding: 12px 14px; border-radius: 999px; font-weight: 800; }
      #loginPromptStayBtn { background: #f3f4f7; color: #1f2329; }
      #loginPromptNowBtn { background: #151515; color: #fff; }
      .login-prompt-actions.single { grid-template-columns: 1fr; }
      @keyframes loginPromptIn { from { opacity: 0; transform: translateY(8px) scale(.98); } to { opacity: 1; transform: translateY(0) scale(1); } }
    `;
    document.head.appendChild(style);
  }

  function showLoginPrompt(message, options) {
    if (isLoginPage()) return;
    const opt = Object.assign({ auto: false, delay: 1200, redirect: true, closable: true }, options || {});
    ensureLoginPromptStyle();

    const old = $('#loginPromptMask');
    if (old) old.remove();

    const mask = document.createElement('div');
    mask.id = 'loginPromptMask';
    mask.innerHTML = `
      <div class="login-prompt-card" role="dialog" aria-modal="true" aria-labelledby="loginPromptTitle">
        ${opt.closable ? '<button type="button" class="login-prompt-close" id="loginPromptCloseBtn" aria-label="关闭">×</button>' : ''}
        <div class="login-prompt-icon">🔐</div>
        <h3 id="loginPromptTitle">请先登录</h3>
        <p>${escapeHtml(message || '登录后才能继续操作。')}</p>
        <div class="login-prompt-actions ${opt.closable ? '' : 'single'}">
          ${opt.closable ? '<button type="button" id="loginPromptStayBtn">暂不登录</button>' : ''}
          <button type="button" id="loginPromptNowBtn">去登录</button>
        </div>
      </div>`;

    document.body.appendChild(mask);
    const stayBtn = $('#loginPromptStayBtn');
    if (stayBtn) stayBtn.addEventListener('click', () => mask.remove());
    const closeBtn = $('#loginPromptCloseBtn');
    if (closeBtn) closeBtn.addEventListener('click', () => mask.remove());
    mask.addEventListener('click', event => { if (event.target === mask && opt.closable) mask.remove(); });
    $('#loginPromptNowBtn').addEventListener('click', () => {
      location.href = loginRedirectUrl();
    });

    clearTimeout(window.__loginRedirectTimer);
    if (opt.auto && opt.redirect) {
      window.__loginRedirectTimer = setTimeout(() => {
        location.href = loginRedirectUrl();
      }, opt.delay);
    }
  }

  function requireLogin(message, options) {
    if (isLoggedIn()) return true;
    showLoginPrompt(message || '登录后才能继续操作。', options);
    return false;
  }

  function isPublicApi(url) {
    const path = String(url || '').split('?')[0];
    return path === '/api/categories' || path === '/api/user/products' || path === '/api/products' || path === '/api/user/merchants' || path === '/api/auth/login' || path === '/api/auth/register' || path === '/api/login' || path === '/api/register';
  }

  function isAuthRequiredApi(url) {
    const path = String(url || '').split('?')[0];
    if (isPublicApi(path)) return false;
    return path === '/api/user/me' || path === '/api/user/stats' || path.startsWith('/api/user/addresses') || path.startsWith('/api/user/orders') || path.startsWith('/api/user/favorites') || path.startsWith('/api/user/coupons');
  }

  function mockResponse(url, options) {
    if (!USE_MOCK_WHEN_BACKEND_UNAVAILABLE) return undefined;

    const raw = String(url || '');
    const normalized = raw.startsWith('http') ? raw : `http://mock.local${raw}`;
    const u = new URL(normalized);
    const path = u.pathname;
    const method = String((options && options.method) || 'GET').toUpperCase();

    if (method === 'POST' && (path === '/api/auth/login' || path === '/api/login')) {
      const body = parseBody(options);
      const username = String(body.username || body.account || '').trim();
      const password = String(body.password || '').trim();
      const user = readMockUsers().find(item => String(item.username) === username && String(item.password) === password);
      if (!user) throw new Error('账号或密码错误');
      const safeUser = publicUser(user);
      const token = createToken(user);
      return { token, user: safeUser };
    }

    if (method === 'POST' && (path === '/api/auth/register' || path === '/api/register')) {
      const body = parseBody(options);
      const username = String(body.username || '').trim();
      const password = String(body.password || '').trim();
      const realName = String(body.realName || body.real_name || body.name || username || '').trim();
      const phone = String(body.phone || '').trim();
      if (!username || !password) throw new Error('请填写账号和密码');
      const users = readMockUsers();
      if (users.some(item => String(item.username) === username)) throw new Error('账号已存在');
      const user = {
        userId: Date.now(),
        username,
        password,
        realName: realName || username,
        phone,
        roleType: 1,
        creditScore: 10
      };
      users.push(user);
      writeMockUsers(users);
      const safeUser = publicUser(user);
      const token = createToken(user);
      return { token, user: safeUser };
    }

    if (method === 'GET' && path === '/api/categories') return mockCategories.slice();

    if (method === 'GET' && path === '/api/user/merchants') return mockMerchants.slice();

    if (method === 'GET' && (path === '/api/user/products' || path === '/api/products')) {
      const keyword = (u.searchParams.get('keyword') || '').trim().toLowerCase();
      const categoryId = u.searchParams.get('categoryId') || '';
      const sort = u.searchParams.get('sort') || 'recommend';
      const page = Number(u.searchParams.get('page') || 1);
      const size = Number(u.searchParams.get('size') || 12);

      let rows = mockProducts.slice();
      if (categoryId) rows = rows.filter(item => String(item.categoryId) === String(categoryId));
      if (keyword) {
        rows = rows.filter(item => [item.name, item.description, item.merchantName].join(' ').toLowerCase().includes(keyword));
      }
      if (sort === 'sales') rows.sort((a, b) => Number(b.monthlySales || 0) - Number(a.monthlySales || 0));
      if (sort === 'priceAsc') rows.sort((a, b) => Number(a.price || 0) - Number(b.price || 0));
      if (sort === 'priceDesc') rows.sort((a, b) => Number(b.price || 0) - Number(a.price || 0));

      const start = Math.max(0, (page - 1) * size);
      const records = rows.slice(start, start + size);
      return { records, total: rows.length, hasMore: start + size < rows.length };
    }

    if (isAuthRequiredApi(path) && !isLoggedIn()) return undefined;

    if (method === 'GET' && path === '/api/user/me') {
      const local = getLocalUser();
      return Object.keys(local || {}).length ? local : publicUser(readMockUsers()[0]);
    }

    if (method === 'GET' && path === '/api/user/stats') return { couponCount: 2, orderCount: mockOrders.length };

    if (path === '/api/user/addresses') {
      if (method === 'GET') return mockAddresses.slice();
      if (method === 'POST') return Object.assign({ addressId: Date.now() }, parseBody(options));
    }

    if (path.startsWith('/api/user/addresses/')) {
      if (['PUT', 'DELETE'].includes(method)) return { success: true };
    }

    if (path === '/api/user/orders') {
      if (method === 'GET') {
        const status = u.searchParams.get('status');
        if (!status || status === 'all') return mockOrders.slice();
        const allowed = status.split(',').map(x => x.trim());
        return mockOrders.filter(order => allowed.includes(String(order.status)));
      }
      if (method === 'POST') {
        return { orderId: Date.now(), status: 0, orderTime: new Date().toLocaleString() };
      }
    }

    const orderMatch = path.match(/^\/api\/user\/orders\/(\d+)(?:\/(cancel|urge|comments))?$/);
    if (orderMatch) {
      const id = Number(orderMatch[1]);
      const action = orderMatch[2];
      if (!action && method === 'GET') return mockOrders.find(order => Number(order.orderId) === id) || mockOrders[0];
      if (action === 'cancel' && method === 'PUT') return { success: true };
      if (action === 'urge' && method === 'PUT') return { success: true };
      if (action === 'comments' && method === 'POST') return { success: true };
    }

    return undefined;
  }

  function parseBody(options) {
    if (!options || !options.body) return {};
    if (typeof options.body === 'string') {
      try { return JSON.parse(options.body); } catch (e) { return {}; }
    }
    return options.body || {};
  }

  async function request(url, options) {
    const token = getToken();

    if (!token && isAuthRequiredApi(url)) {
      requireLogin('登录后才能继续使用该功能。', { redirect: true, auto: false, closable: true });
      throw new Error('请先登录');
    }

    const opt = Object.assign({ method: 'GET' }, options || {});
    opt.headers = Object.assign({ 'Content-Type': 'application/json' }, opt.headers || {});
    if (token) opt.headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
    if (opt.body && typeof opt.body !== 'string') opt.body = JSON.stringify(opt.body);

    let resp;
    try {
      resp = await fetch(API_BASE + url, opt);
    } catch (err) {
      const mock = mockResponse(url, opt);
      if (mock !== undefined) return mock;
      throw err;
    }

    if (resp.status === 401 || resp.status === 403) {
      clearAuthCache();
      requireLogin('登录已过期或没有权限，请重新登录。', { redirect: true, auto: false, closable: true });
      throw new Error('未登录或无权限');
    }

    const text = await resp.text();
    let json;
    try { json = text ? JSON.parse(text) : {}; } catch (e) { json = text; }

    if (!resp.ok) {
      const mock = mockResponse(url, opt);
      if (mock !== undefined) return mock;
      throw new Error((json && (json.message || json.msg)) || `请求失败：${resp.status}`);
    }
    return normalizeResult(json);
  }

  async function loadCurrentUser() {
    const local = getLocalUser();
    if (!isLoggedIn()) return local || {};
    try {
      const user = await request('/api/user/me');
      saveLocalUser(user);
      return user;
    } catch (e) {
      if (local && (local.userId || local.user_id || local.username)) return local;
      return {};
    }
  }

  function formatMoney(value) {
    const n = Number(value || 0);
    return `￥${n.toFixed(2)}`;
  }

  function getField(obj, names, fallback) {
    for (const name of names) {
      if (obj && obj[name] !== undefined && obj[name] !== null) return obj[name];
    }
    return fallback;
  }

  function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>'"]/g, s => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'
    }[s]));
  }

  function guardProtectedLinks() {
    document.addEventListener('click', event => {
      const link = event.target.closest && event.target.closest('a[href]');
      if (!link || isLoggedIn()) return;
      const href = link.getAttribute('href') || '';
      if (href.includes('/user/order.html') || href.includes('/user/profile.html')) {
        event.preventDefault();
        requireLogin('订单和个人中心需要登录后查看。', { redirect: true, auto: false, closable: true });
      }
    });
  }

  document.addEventListener('DOMContentLoaded', () => {
    guardProtectedLinks();
    if (!isLoginPage() && isProtectedPagePath(location.pathname) && !isLoggedIn()) {
      requireLogin('订单和个人中心需要登录后查看。', { redirect: true, auto: false, closable: true });
    }
  });

  window.App = {
    $, $all, request, toast, getToken, isLoggedIn, requireLogin, loginRedirectUrl, saveAuth, clearAuthCache, clearCartCache,
    getLocalUser, saveLocalUser, loadCurrentUser, formatMoney, getField, escapeHtml,
    mock: { categories: mockCategories, merchants: mockMerchants, products: mockProducts, addresses: mockAddresses, orders: mockOrders }
  };
})();
