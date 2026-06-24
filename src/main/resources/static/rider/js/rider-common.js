(function () {
    const RIDER_USER_KEY = 'takeout_rider_user_v1';

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

    function saveRider(user) {
        localStorage.setItem(RIDER_USER_KEY, JSON.stringify(user || {}));
    }

    function getLocalRider() {
        try {
            return JSON.parse(localStorage.getItem(RIDER_USER_KEY) || '{}');
        } catch (e) {
            return {};
        }
    }

    function clearRider() {
        localStorage.removeItem(RIDER_USER_KEY);
    }

    function normalizeResult(json) {
        if (!json || typeof json !== 'object') return json;

        if ('success' in json && !json.success) {
            throw new Error(json.message || '请求失败');
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
        let el = $('#riderToast');

        if (!el) {
            el = document.createElement('div');
            el.id = 'riderToast';
            el.className = 'rider-toast hidden';
            document.body.appendChild(el);
        }

        el.textContent = message || '';
        el.dataset.type = type || 'info';
        el.classList.remove('hidden');

        clearTimeout(window.__riderToastTimer);
        window.__riderToastTimer = setTimeout(() => {
            el.classList.add('hidden');
        }, 2200);
    }

    function requireRiderLogin() {
        return request('/rider/current')
            .then(user => {
                saveRider(user);
                return user;
            })
            .catch(() => {
                clearRider();
                location.href = '/rider/login.html?redirect=' + encodeURIComponent(location.pathname + location.search);
            });
    }

    function formatMoney(value) {
        return `¥${Number(value || 0).toFixed(2)}`;
    }

    window.RiderApp = {
        $,
        $all,
        request,
        toast,
        escapeHtml,
        getField,
        saveRider,
        getLocalRider,
        clearRider,
        requireRiderLogin,
        formatMoney
    };
})();