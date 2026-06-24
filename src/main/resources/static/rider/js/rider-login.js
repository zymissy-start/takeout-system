(function () {
    document.addEventListener('DOMContentLoaded', init);

    function init() {
        const form = RiderApp.$('#riderLoginForm');
        const toggleBtn = RiderApp.$('#togglePasswordBtn');

        if (form) {
            form.addEventListener('submit', handleLogin);
        }

        if (toggleBtn) {
            toggleBtn.addEventListener('click', togglePassword);
        }

        checkCurrentRider();
    }

    async function checkCurrentRider() {
        try {
            const rider = await RiderApp.request('/rider/current');

            if (rider && rider.userId) {
                setMessage('当前已登录，可继续切换账号或直接进入骑手工作台。', 'success');
            }
        } catch (e) {
            // 未登录是正常状态，不提示。
        }
    }

    function togglePassword() {
        const input = RiderApp.$('#passwordInput');
        const btn = RiderApp.$('#togglePasswordBtn');

        const isPassword = input.type === 'password';
        input.type = isPassword ? 'text' : 'password';
        btn.textContent = isPassword ? '隐藏' : '显示';
    }

    function setMessage(message, type) {
        const el = RiderApp.$('#authMessage');

        if (!el) return;

        el.textContent = message || '';
        el.dataset.type = type || 'info';
    }

    function redirectTarget() {
        const value = new URLSearchParams(location.search).get('redirect') || '/rider/dashboard.html';

        if (!value.startsWith('/') || value.startsWith('//') || value.includes('://')) {
            return '/rider/dashboard.html';
        }

        return value;
    }

    async function handleLogin(event) {
        event.preventDefault();

        const username = RiderApp.$('#usernameInput').value.trim();
        const password = RiderApp.$('#passwordInput').value.trim();
        const btn = RiderApp.$('#loginSubmitBtn');

        if (!username || !password) {
            setMessage('请填写骑手账号和密码', 'error');
            return;
        }

        try {
            btn.disabled = true;
            setMessage('正在登录...', 'info');

            const rider = await RiderApp.request('/rider/login', {
                method: 'POST',
                body: { username, password }
            });

            RiderApp.saveRider(rider);
            setMessage('登录成功，正在进入骑手工作台...', 'success');

            setTimeout(() => {
                location.href = redirectTarget();
            }, 500);
        } catch (e) {
            setMessage(e.message || '登录失败，请检查账号和密码', 'error');
        } finally {
            btn.disabled = false;
        }
    }
})();