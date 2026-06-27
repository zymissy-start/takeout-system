(function () {
  const API_BASE = ''; // 同域部署：SpringBoot 静态资源和接口在同一服务下，无需改动。
  const LOGIN_URL = '/login.html'; // 登录由其他同学实现，必要时改成他们的登录页地址。

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

  function saveAuth(token, user) {
    if (token) localStorage.setItem('token', token);
    if (user) saveLocalUser(user);
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

  async function request(url, options) {
    const token = getToken();
    const opt = Object.assign({ method: 'GET' }, options || {});
    opt.headers = Object.assign({ 'Content-Type': 'application/json' }, opt.headers || {});
    if (token) opt.headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
    if (opt.body && typeof opt.body !== 'string') opt.body = JSON.stringify(opt.body);

    const resp = await fetch(API_BASE + url, opt);
    if (resp.status === 401 || resp.status === 403) {
      toast('登录已过期，请重新登录');
      setTimeout(() => { window.location.href = LOGIN_URL; }, 650);
      throw new Error('未登录或无权限');
    }
    const text = await resp.text();
    let json = null;
    try { json = text ? JSON.parse(text) : {}; } catch (e) { json = text; }
    if (!resp.ok) throw new Error((json && (json.message || json.msg)) || `请求失败：${resp.status}`);
    return normalizeResult(json);
  }

  async function loadCurrentUser() {
    const local = getLocalUser();
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

  window.App = { $, $all, request, toast, getToken, isLoggedIn, getLocalUser, saveLocalUser, saveAuth, loadCurrentUser, formatMoney, getField, escapeHtml };
})();
