(function () {
  document.addEventListener('DOMContentLoaded', init);

  function init() {
    const loginForm = App.$('#loginForm');
    const registerForm = App.$('#registerForm');
    bindPasswordToggle();
    preserveRedirectLinks();
    if (loginForm) loginForm.addEventListener('submit', handleLogin);
    if (registerForm) registerForm.addEventListener('submit', handleRegister);

    if (App.isLoggedIn()) {
      const tip = App.$('#authTip');
      if (tip) tip.textContent = '当前已登录，可直接返回首页或继续切换账号。';
    }
  }

  function preserveRedirectLinks() {
    const redirect = new URLSearchParams(location.search).get('redirect');
    if (!redirect) return;
    App.$all('a[href="/login.html"], a[href="/register.html"]').forEach(link => {
      const href = link.getAttribute('href');
      link.setAttribute('href', `${href}?redirect=${encodeURIComponent(redirect)}`);
    });
  }

  function bindPasswordToggle() {
    App.$all('[data-toggle-password]').forEach(btn => {
      btn.addEventListener('click', () => {
        const input = App.$(`#${btn.dataset.togglePassword}`);
        if (!input) return;
        const isPassword = input.type === 'password';
        input.type = isPassword ? 'text' : 'password';
        btn.textContent = isPassword ? '隐藏' : '显示';
      });
    });
  }

  function redirectTarget() {
    const value = new URLSearchParams(location.search).get('redirect') || '/user/index.html';
    if (!value.startsWith('/') || value.startsWith('//') || value.includes('://')) return '/user/index.html';
    return value;
  }

  function setMessage(message, type) {
    const el = App.$('#authMessage');
    if (!el) return;
    el.textContent = message || '';
    el.dataset.type = type || 'info';
    el.classList.toggle('hidden', !message);
  }

  function saveAuthFromResult(result) {
    const token = App.getField(result || {}, ['token', 'accessToken', 'access_token', 'Authorization'], '');
    const user = App.getField(result || {}, ['user', 'currentUser', 'userInfo'], result && result.username ? result : {});
    if (!token) throw new Error('登录接口未返回 token');
    App.saveAuth(token, user || {});
  }

  async function handleLogin(event) {
    event.preventDefault();
    const username = App.$('#usernameInput').value.trim();
    const password = App.$('#passwordInput').value.trim();
    if (!username || !password) return setMessage('请填写账号和密码', 'error');

    const btn = App.$('#loginSubmitBtn');
    try {
      btn.disabled = true;
      setMessage('正在登录...', 'info');
      const result = await App.request('/api/auth/login', {
        method: 'POST',
        body: { username, password }
      });
      saveAuthFromResult(result);
      setMessage('登录成功，正在进入页面...', 'success');
      setTimeout(() => { location.href = redirectTarget(); }, 450);
    } catch (e) {
      setMessage(e.message || '登录失败，请检查账号和密码', 'error');
    } finally {
      btn.disabled = false;
    }
  }

  async function handleRegister(event) {
    event.preventDefault();
    const realName = App.$('#realNameInput').value.trim();
    const phone = App.$('#phoneInput').value.trim();
    const username = App.$('#usernameInput').value.trim();
    const password = App.$('#passwordInput').value.trim();
    const confirmPassword = App.$('#confirmPasswordInput').value.trim();

    if (!realName || !username || !password || !confirmPassword) return setMessage('请完整填写必填信息', 'error');
    if (username.length < 3) return setMessage('账号至少需要 3 个字符', 'error');
    if (password.length < 6) return setMessage('密码至少需要 6 位', 'error');
    if (password !== confirmPassword) return setMessage('两次输入的密码不一致', 'error');
    if (phone && !/^1\d{10}$/.test(phone)) return setMessage('手机号格式不正确', 'error');

    const btn = App.$('#registerSubmitBtn');
    try {
      btn.disabled = true;
      setMessage('正在注册...', 'info');
      const result = await App.request('/api/auth/register', {
        method: 'POST',
        body: { username, password, realName, phone, roleType: 1 }
      });
      saveAuthFromResult(result);
      setMessage('注册成功，正在进入页面...', 'success');
      setTimeout(() => { location.href = redirectTarget(); }, 450);
    } catch (e) {
      setMessage(e.message || '注册失败，请稍后再试', 'error');
    } finally {
      btn.disabled = false;
    }
  }
})();
