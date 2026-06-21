(function () {
    document.addEventListener('DOMContentLoaded', init);

    function init() {
        const form = MerchantApp.$('#merchantLoginForm');
        const toggleBtn = MerchantApp.$('#togglePasswordBtn');

        if (form) {
            form.addEventListener('submit', handleLogin);
        }

        if (toggleBtn) {
            toggleBtn.addEventListener('click', togglePassword);
        }

        checkCurrentMerchant();
    }

    async function checkCurrentMerchant() {
        try {
            const user = await MerchantApp.request('/merchant/current');
            if (user && user.userId) {
                setMessage('当前已登录，可继续切换账号或直接进入后台。', 'success');
            }
        } catch (e) {
            // 未登录是正常状态，不提示错误。
        }
    }

    function togglePassword() {
        const input = MerchantApp.$('#passwordInput');
        const btn = MerchantApp.$('#togglePasswordBtn');

        if (!input || !btn) return;

        const isPassword = input.type === 'password';
        input.type = isPassword ? 'text' : 'password';
        btn.textContent = isPassword ? '隐藏' : '显示';
    }

    function setMessage(message, type) {
        const el = MerchantApp.$('#authMessage');
        if (!el) return;

        el.textContent = message || '';
        el.dataset.type = type || 'info';
    }

    function redirectTarget() {
        const value = new URLSearchParams(location.search).get('redirect') || '/merchant/dashboard.html';

        if (!value.startsWith('/') || value.startsWith('//') || value.includes('://')) {
            return '/merchant/dashboard.html';
        }

        return value;
    }

    async function handleLogin(event) {
        event.preventDefault();

        const username = MerchantApp.$('#usernameInput').value.trim();
        const password = MerchantApp.$('#passwordInput').value.trim();
        const btn = MerchantApp.$('#loginSubmitBtn');

        if (!username || !password) {
            setMessage('请填写商家账号和密码', 'error');
            return;
        }

        try {
            btn.disabled = true;
            setMessage('正在登录...', 'info');

            const merchant = await MerchantApp.request('/merchant/login', {
                method: 'POST',
                body: { username, password }
            });

            MerchantApp.saveMerchant(merchant);
            setMessage('登录成功，正在进入商家后台...', 'success');

            setTimeout(() => {
                location.href = redirectTarget();
            }, 450);
        } catch (e) {
            setMessage(e.message || '登录失败，请检查账号和密码', 'error');
        } finally {
            btn.disabled = false;
        }
    }
})();