(function () {
    // 本地缓存商家信息的 key，用于页面刷新后仍能显示商家基本信息。
    const MERCHANT_USER_KEY = 'takeout_merchant_user_v1';
    /** 简化版 DOM 查询方法。 */
    function $(selector) {
        return document.querySelector(selector);
    }
    /** 查询多个 DOM 元素，并转成数组方便遍历。 */
    function $all(selector) {
        return Array.from(document.querySelectorAll(selector));
    }
    /**
     * HTML 转义方法。
     * 前端渲染用户输入或数据库内容时使用，避免特殊字符破坏页面结构。
     */
    function escapeHtml(value) {
        return String(value ?? '')
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }
    /**
     * 兼容不同字段命名方式。
     * 例如后端可能返回 userId，也可能返回 user_id。
     */
    function getField(obj, keys, defaultValue) {
        if (!obj) return defaultValue;
        for (const key of keys) {
            if (obj[key] !== undefined && obj[key] !== null && obj[key] !== '') {
                return obj[key];
            }
        }
        return defaultValue;
    }
    /** 保存商家信息到 localStorage。 */
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
    /** 清除本地商家登录缓存。 */
    function clearMerchant() {
        localStorage.removeItem(MERCHANT_USER_KEY);
    }
    /**
     * 统一处理后端返回结果。
     * 后端 Result.success 会返回 success = true 和 data 字段，
     * 此处自动取出 data，简化页面 JS 的处理逻辑。
     */

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
    /**
     * 封装 Ajax 请求。
     * 使用 fetch 发送 GET/POST 请求，并统一携带 Session Cookie。
     */

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
// POST 表单请求统一转为 x-www-form-urlencoded，方便 Spring MVC 接收参数。
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
    /**
     * 页面权限控制。
     * 如果当前 Session 中没有登录商家，则跳转到登录页。
     */

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