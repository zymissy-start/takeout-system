(function () {
    const MERCHANT_USER_KEY = 'takeout_merchant_user_v1';

    function $(selector) {
        return document.querySelector(selector);
    }

    function $all(selector) {
        return Array.from(document.querySelectorAll(selector));
    }

    function escapeHtml(value) {
        return String(value ?? '')
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function getField(obj, keys, defaultValue) {
        if (!obj) return defaultValue;
        for (const key of keys) {
            if (obj[key] !== undefined && obj[key] !== null && obj[key] !== '') {
                return obj[key];
            }
        }
        return defaultValue;
    }

    function saveMerchant(user) {
        localStorage.setItem(MERCHANT_USER_KEY, JSON.stringify(user || {}));
    }

    function getLocalMerchant() {
        try {
            return JSON.parse(localStorage.getItem(MERCHANT_USER_KEY) || '{}');
        } catch (e) {
            return {};
        }
    }

    function clearMerchant() {
        localStorage.removeItem(MERCHANT_USER_KEY);
    }

    function normalizeResult(json) {
        if (!json || typeof json !== 'object') return json;

        if ('success' in json && !json.success) {
            throw new Error(json.message || json.msg || '请求失败');
        }

        if ('code' in json) {
            const ok = json.code === 0 || json.code === 200 || json.code === '0' || json.code === '200';
            if (!ok) throw new Error(json.message || json.msg || '请求失败');
            return 'data' in json ? json.data : json;
        }

        if ('success' in json && json.success) {
            return 'data' in json ? json.data : json;
        }

        return json;
    }

    async function request(url, options) {
        const opt = Object.assign({
            method: 'GET',
            body: null,
            headers: {}
        }, options || {});

        const fetchOptions = {
            method: opt.method,
            credentials: 'same-origin',
            headers: Object.assign({}, opt.headers)
        };

        if (opt.body) {
            fetchOptions.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
            const form = new URLSearchParams();
            Object.keys(opt.body).forEach(key => {
                if (opt.body[key] !== undefined && opt.body[key] !== null) {
                    form.append(key, opt.body[key]);
                }
            });
            fetchOptions.body = form;
        }

        const response = await fetch(url, fetchOptions);

        if (!response.ok) {
            throw new Error(`请求失败：${response.status}`);
        }

        const text = await response.text();
        const json = text ? JSON.parse(text) : {};
        return normalizeResult(json);
    }

    function toast(message, type) {
        let el = $('#merchantToast');

        if (!el) {
            el = document.createElement('div');
            el.id = 'merchantToast';
            el.className = 'merchant-toast hidden';
            document.body.appendChild(el);
        }

        el.textContent = message || '';
        el.dataset.type = type || 'info';
        el.classList.remove('hidden');

        clearTimeout(window.__merchantToastTimer);
        window.__merchantToastTimer = setTimeout(() => {
            el.classList.add('hidden');
        }, 2200);
    }

    function requireMerchantLogin() {
        return request('/merchant/current')
            .then(user => {
                saveMerchant(user);
                return user;
            })
            .catch(() => {
                clearMerchant();
                location.href = '/merchant/login.html?redirect=' + encodeURIComponent(location.pathname + location.search);
            });
    }

    function formatMoney(value) {
        return `¥${Number(value || 0).toFixed(2)}`;
    }

    window.MerchantApp = {
        $,
        $all,
        request,
        toast,
        escapeHtml,
        getField,
        saveMerchant,
        getLocalMerchant,
        clearMerchant,
        requireMerchantLogin,
        formatMoney
    };
})();