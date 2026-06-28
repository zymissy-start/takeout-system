(function () {
    var state = {
        activeOrderId: null,
        pendingOrderPayload: null
    };

    document.addEventListener('DOMContentLoaded', init);

    function init() {
        bindEvents();
        addMessage('agent', '正在读取你的点单记录和推荐菜品...');
        loadSummary();
    }

    function bindEvents() {
        var sendBtn = byId('sendBtn');
        var chatInput = byId('chatInput');

        if (sendBtn) {
            sendBtn.addEventListener('click', sendCurrentMessage);
        }

        if (chatInput) {
            chatInput.addEventListener('keydown', function (e) {
                if (e.key === 'Enter') {
                    sendCurrentMessage();
                }
            });
        }

        document.querySelectorAll('.prompt-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                ask(btn.dataset.prompt || btn.textContent.trim());
            });
        });

        var confirmBtn = byId('confirmOrderBtn');
        if (confirmBtn) {
            confirmBtn.addEventListener('click', confirmAiOrder);
        }
    }

    async function loadSummary() {
        try {
            var summary = await request('/api/user/ai-agent/summary');

            var status = byId('agentStatus');
            if (status) {
                status.textContent = summary.deepSeekEnabled ? 'DeepSeek 已接入' : '未配置 Key：本地规则推荐';
            }

            renderProducts(summary.recommendations || []);

            addMessage('agent', summary.welcomeText || '我可以帮你推荐菜品、根据你的需求生成下单方案，并在你确认后完成下单。');
        } catch (e) {
            var status = byId('agentStatus');
            if (status) {
                status.textContent = '加载失败';
            }

            renderProducts([]);
            addMessage('agent', e.message || '加载失败，请确认已登录普通用户账号。');
        }
    }

    function sendCurrentMessage() {
        var input = byId('chatInput');
        if (!input) {
            return;
        }

        var text = input.value.trim();
        if (!text) {
            return;
        }

        input.value = '';
        ask(text);
    }

    async function ask(text) {
        addMessage('user', text);

        try {
            var resp = await request('/api/user/ai-agent/chat', {
                method: 'POST',
                body: {
                    message: text,
                    orderId: state.activeOrderId,
                    items: readCartItems()
                }
            });

            handleAgentResponse(resp);
        } catch (e) {
            addMessage('agent', e.message || 'AI 助手暂时不可用。');
        }
    }

    function handleAgentResponse(resp) {
        addMessage('agent', resp.reply || '暂时没有推荐结果。');

        if (resp.recommendations && resp.recommendations.length) {
            renderProducts(resp.recommendations);
        }

        if (resp.activeOrder) {
            state.activeOrderId = field(resp.activeOrder, ['orderId', 'order_id'], state.activeOrderId);
        }

        if (resp.needConfirm && resp.actionPayload) {
            state.pendingOrderPayload = resp.actionPayload;
            showConfirmOrderButton();
        }

        if (!resp.needConfirm && resp.action === 'create_order') {
            state.pendingOrderPayload = null;
            hideConfirmOrderButton();
        }
    }

    function showConfirmOrderButton() {
        var btn = byId('confirmOrderBtn');
        if (!btn) {
            return;
        }
        btn.style.display = 'inline-block';
    }

    function hideConfirmOrderButton() {
        var btn = byId('confirmOrderBtn');
        if (!btn) {
            return;
        }
        btn.style.display = 'none';
    }

    async function confirmAiOrder() {
        if (!state.pendingOrderPayload) {
            addMessage('agent', '当前没有待确认的下单方案。');
            return;
        }

        var username = state.pendingOrderPayload.username;

        if (!username) {
            username = prompt('请输入普通用户的用户名，系统会自动使用该用户默认收货地址。');
            if (!username) {
                addMessage('agent', '还没有用户名，暂时不能下单。');
                return;
            }
        }

        try {
            var resp = await request('/api/user/ai-agent/chat', {
                method: 'POST',
                body: {
                    message: '确认下单',
                    intent: 'create_order',
                    confirmOrder: true,
                    username: username,
                    addressId: state.pendingOrderPayload.addressId,
                    userCouponId: state.pendingOrderPayload.userCouponId,
                    remark: state.pendingOrderPayload.remark,
                    items: normalizePayloadItems(state.pendingOrderPayload.items)
                }
            });

            handleAgentResponse(resp);
            state.pendingOrderPayload = null;
            hideConfirmOrderButton();
        } catch (e) {
            addMessage('agent', e.message || '下单失败，请检查用户名、收货地址、商品库存或优惠券。');
        }
    }

    function normalizePayloadItems(items) {
        if (!Array.isArray(items)) {
            return [];
        }

        return items.map(function (item) {
            return {
                productId: Number(item.productId || item.product_id),
                quantity: Number(item.quantity || 1)
            };
        }).filter(function (item) {
            return item.productId;
        });
    }

    function renderProducts(products) {
        var box = byId('recommendList');
        if (!box) {
            return;
        }

        if (!products || !products.length) {
            box.innerHTML = '<div class="empty">暂无推荐菜品。</div>';
            return;
        }

        box.innerHTML = products.map(function (p) {
            var rawImg = field(p, ['imageUrl', 'image_url'], '');
            var img = normalizeImageUrl(rawImg);
            var id = field(p, ['productId', 'product_id'], '');
            var emoji = productEmoji(p);

            var imageHtml =
                '<div class="product-img-wrap">' +
                (img ? '<img class="product-img" src="' + escapeHtml(img) + '" alt="菜品图片">' : '') +
                '<span class="product-img-fallback" style="' + (img ? 'display:none;' : 'display:flex;') + '">' + emoji + '</span>' +
                '</div>';

            return '<article class="product-card">' +
                imageHtml +
                '<div class="product-info">' +
                '<div class="product-name">' + escapeHtml(p.name || '未命名菜品') + '</div>' +
                '<div class="product-meta">' +
                escapeHtml(p.merchantName || '未知商家') +
                ' · ' + escapeHtml(p.categoryName || '美食') +
                ' · 月售 ' + (p.monthlySales || 0) +
                '</div>' +
                '<div class="product-meta">' + escapeHtml(p.description || '暂无描述') + '</div>' +
                '<div class="product-price">' + money(p.price) + '</div>' +
                '<button class="agent-btn ghost" data-product-id="' + escapeHtml(id) + '">加入购物车</button>' +
                '</div>' +
                '</article>';
        }).join('');

        box.querySelectorAll('.product-img').forEach(function (img) {
            img.addEventListener('error', function () {
                img.style.display = 'none';
                var fallback = img.nextElementSibling;
                if (fallback) {
                    fallback.style.display = 'flex';
                }
            });
        });

        box.querySelectorAll('[data-product-id]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var product = products.find(function (p) {
                    return String(field(p, ['productId', 'product_id'], '')) === String(btn.dataset.productId);
                });
                addToCart(product);
            });
        });
    }

    function readCartItems() {
        try {
            if (window.Cart && typeof Cart.readCart === 'function') {
                return Cart.readCart().map(toCartItem).filter(Boolean);
            }
        } catch (e) {
        }

        var keys = ['takeout_cart', 'cart', 'shoppingCart', 'user_cart'];

        for (var i = 0; i < keys.length; i++) {
            try {
                var raw = localStorage.getItem(keys[i]);
                if (!raw) {
                    continue;
                }

                var list = JSON.parse(raw);
                if (Array.isArray(list)) {
                    return list.map(toCartItem).filter(Boolean);
                }
            } catch (e) {
            }
        }

        return [];
    }

    function toCartItem(item) {
        var productId = Number(field(item, ['productId', 'product_id', 'id'], 0));

        if (!productId) {
            return null;
        }

        return {
            productId: productId,
            quantity: Number(item.quantity || 1)
        };
    }

    function addToCart(product) {
        if (!product) {
            return toast('菜品不存在');
        }

        try {
            if (window.Cart && typeof Cart.add === 'function') {
                Cart.add(product, 1);
                toast('已加入购物车');
                return;
            }
        } catch (e) {
            toast(e.message || '加入失败');
            return;
        }

        toast('请返回首页在菜品卡片中加入购物车');
    }

    async function request(url, options) {
        if (window.App && typeof App.request === 'function') {
            return App.request(url, options);
        }

        var opt = Object.assign({ method: 'GET' }, options || {});
        opt.headers = Object.assign({ 'Content-Type': 'application/json' }, opt.headers || {});

        var token = localStorage.getItem('token') || '';
        if (token) {
            opt.headers.Authorization = token.indexOf('Bearer ') === 0 ? token : 'Bearer ' + token;
        }

        if (opt.body && typeof opt.body !== 'string') {
            opt.body = JSON.stringify(opt.body);
        }

        var resp = await fetch(url, opt);
        var json = await resp.json();

        if (!resp.ok || (json.code && Number(json.code) !== 200 && Number(json.code) !== 0)) {
            throw new Error(json.message || '请求失败');
        }

        return json.data || json;
    }

    function toast(message) {
        if (window.App && App.toast) {
            return App.toast(message);
        }

        alert(message);
    }

    function normalizeImageUrl(url) {
        if (!url) {
            return '';
        }

        var value = String(url).trim();

        if (!value || value === 'null' || value === 'undefined') {
            return '';
        }

        if (value.indexOf('http://') === 0 || value.indexOf('https://') === 0 || value.indexOf('/') === 0) {
            return value;
        }

        return '/' + value;
    }

    function productEmoji(product) {
        var text = ((product && product.name) || '') + ' ' + ((product && product.categoryName) || '');

        if (text.indexOf('饮') >= 0 || text.indexOf('奶茶') >= 0 || text.indexOf('可乐') >= 0 || text.indexOf('茶') >= 0) {
            return '🥤';
        }

        if (text.indexOf('汉堡') >= 0 || text.indexOf('堡') >= 0) {
            return '🍔';
        }

        if (text.indexOf('饭') >= 0 || text.indexOf('排骨') >= 0) {
            return '🍱';
        }

        if (text.indexOf('面') >= 0 || text.indexOf('粉') >= 0) {
            return '🍜';
        }

        return '🍽️';
    }

    function addMessage(role, text) {
        var panel = byId('chatPanel');
        if (!panel) {
            return;
        }

        var div = document.createElement('div');
        div.className = 'msg ' + role;
        div.textContent = text || '';

        panel.appendChild(div);
        panel.scrollTop = panel.scrollHeight;
    }

    function byId(id) {
        return document.getElementById(id);
    }

    function money(v) {
        return '￥' + Number(v || 0).toFixed(2);
    }

    function field(obj, names, fallback) {
        for (var i = 0; i < names.length; i++) {
            if (obj && obj[names[i]] !== undefined && obj[names[i]] !== null) {
                return obj[names[i]];
            }
        }
        return fallback;
    }

    function escapeHtml(value) {
        return String(value == null ? '' : value).replace(/[&<>'"]/g, function (s) {
            return ({
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                "'": '&#39;',
                '"': '&quot;'
            })[s];
        });
    }
})();