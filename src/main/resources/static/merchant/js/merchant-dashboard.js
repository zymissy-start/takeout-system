(function () {
    const state = {
        merchant: null,
        statistics: {
            waitAcceptCount: 0,
            cookingCount: 0,
            waitRiderCount: 0,
            finishedCount: 0
        },
        orders: [],
        products: []
    };

    document.addEventListener('DOMContentLoaded', init);

    async function init() {
        bindEvents();

        const merchant = await MerchantApp.requireMerchantLogin();
        state.merchant = merchant;

        renderMerchantInfo();
        await loadDashboard();
    }

    function bindEvents() {
        const logoutBtn = MerchantApp.$('#logoutBtn');
        const refreshBtn = MerchantApp.$('#refreshBtn');

        if (logoutBtn) {
            logoutBtn.addEventListener('click', logout);
        }

        if (refreshBtn) {
            refreshBtn.addEventListener('click', loadDashboard);
        }

        MerchantApp.$all('.action-card[data-action]').forEach(btn => {
            btn.addEventListener('click', handleQuickAction);
        });
    }

    function renderMerchantInfo() {
        const name = MerchantApp.getField(state.merchant, ['realName', 'real_name', 'username'], '商家');
        const username = MerchantApp.getField(state.merchant, ['username'], '');
        const phone = MerchantApp.getField(state.merchant, ['phone'], '未绑定手机号');

        MerchantApp.$('#merchantName').textContent = name;
        MerchantApp.$('#merchantAvatar').textContent = String(name).slice(0, 1);
        MerchantApp.$('#merchantSub').textContent = `账号：${username} · 电话：${phone}`;
    }

    async function loadDashboard() {
        await Promise.all([
            loadStatistics(),
            loadOrderPreview(),
            loadProductPreview()
        ]);
    }

    async function loadStatistics() {
        try {
            const data = await MerchantApp.request('/merchant/dashboard/statistics');
<<<<<<< HEAD
            state.statistics = Object.assign(state.statistics, data || {});
        } catch (e) {
=======

            state.statistics = Object.assign(state.statistics, data || {});
        } catch (e) {
            // 后台统计接口还没写时，前端先展示 0，不阻塞页面使用。
>>>>>>> origin/feature-user-rider-merchant
            state.statistics = {
                waitAcceptCount: 0,
                cookingCount: 0,
                waitRiderCount: 0,
                finishedCount: 0
            };
        }

        MerchantApp.$('#waitAcceptCount').textContent = state.statistics.waitAcceptCount || 0;
        MerchantApp.$('#cookingCount').textContent = state.statistics.cookingCount || 0;
        MerchantApp.$('#waitRiderCount').textContent = state.statistics.waitRiderCount || 0;
        MerchantApp.$('#finishedCount').textContent = state.statistics.finishedCount || 0;
    }

    async function loadOrderPreview() {
        const box = MerchantApp.$('#orderPreviewList');
        box.innerHTML = '<div class="empty-state">正在加载订单...</div>';

        try {
            const data = await MerchantApp.request('/merchant/orders?size=5');
            state.orders = Array.isArray(data) ? data : (data.records || data.list || data.rows || []);
        } catch (e) {
            state.orders = mockOrders();
        }

        renderOrders();
    }

    async function loadProductPreview() {
        const box = MerchantApp.$('#productPreviewList');
        box.innerHTML = '<div class="empty-state">正在加载商品...</div>';

        try {
            const data = await MerchantApp.request('/merchant/foods?size=4');
            state.products = Array.isArray(data) ? data : (data.records || data.list || data.rows || []);
        } catch (e) {
            state.products = mockProducts();
        }

        renderProducts();
    }

    function statusText(status) {
        const map = {
            '-1': '已取消',
            '0': '待商家接单',
            '1': '制作中',
            '2': '待骑手接单',
<<<<<<< HEAD
            '3': '配送中',
            '4': '已完成',
            '5': '已完成'
=======
            '3': '骑手配送中',
            '4': '已完成'
>>>>>>> origin/feature-user-rider-merchant
        };

        return map[String(status)] || '未知状态';
    }

    function pillClass(status) {
<<<<<<< HEAD
        if (String(status) === '4' || String(status) === '5') return 'done';
        if (String(status) === '-1') return 'cancel';
        if (String(status) === '3') return 'info';
=======
        if (String(status) === '4') return 'done';
        if (String(status) === '-1') return 'cancel';
        if (String(status) === '3') return 'info';
        if (String(status) === '2') return 'warning';
        return '';
    }

    function isUrgentOrder(order) {
        const status = Number(MerchantApp.getField(order, ['status'], 0));
        if (status === 4 || status === -1) return false;
        return Number(MerchantApp.getField(order, ['reminderCount', 'reminder_count'], 0)) > 0
            || Number(MerchantApp.getField(order, ['riderUrgeCount', 'rider_urge_count'], 0)) > 0;
    }

    function urgentText(order) {
        const reminderCount = Number(MerchantApp.getField(order, ['reminderCount', 'reminder_count'], 0));
        const riderUrgeCount = Number(MerchantApp.getField(order, ['riderUrgeCount', 'rider_urge_count'], 0));
        const latest = MerchantApp.getField(order, ['latestReminderTime', 'latest_reminder_time', 'riderUrgeTime', 'rider_urge_time'], '刚刚');
        if (riderUrgeCount > 0) return `骑手已催出餐 ${riderUrgeCount} 次 · ${latest}`;
        if (reminderCount > 0) return `用户催单 ${reminderCount} 次 · ${latest}`;
>>>>>>> origin/feature-user-rider-merchant
        return '';
    }

    function renderOrders() {
        const box = MerchantApp.$('#orderPreviewList');

        if (!state.orders.length) {
            box.innerHTML = '<div class="empty-state">暂无待处理订单</div>';
            return;
        }

<<<<<<< HEAD
        box.innerHTML = state.orders.map(order => {
=======
        const urgentCount = state.orders.filter(isUrgentOrder).length;
        const urgentBanner = urgentCount > 0
            ? `<div class="merchant-urge-banner">⚠️ 有 ${urgentCount} 个订单正在催单，请优先处理红色标记订单。</div>`
            : '';

        box.innerHTML = urgentBanner + state.orders.map(order => {
>>>>>>> origin/feature-user-rider-merchant
            const id = MerchantApp.getField(order, ['orderId', 'order_id', 'id'], '');
            const userName = MerchantApp.getField(order, ['userName', 'user_name', 'receiverName'], '用户');
            const status = MerchantApp.getField(order, ['status'], 0);
            const total = MerchantApp.getField(order, ['payAmount', 'pay_amount', 'totalPrice', 'total_price'], 0);
            const time = MerchantApp.getField(order, ['orderTime', 'order_time', 'createTime', 'create_time'], '');
            const summary = MerchantApp.getField(order, ['summary', 'itemsText'], '点击查看订单商品明细');
<<<<<<< HEAD

            return `
        <article class="order-card" data-id="${MerchantApp.escapeHtml(id)}">
          <div class="order-card-head">
            <b>${MerchantApp.escapeHtml(userName)}</b>
            <span class="status-pill ${pillClass(status)}">${statusText(status)}</span>
          </div>

=======
            const requiredTitle = MerchantApp.getField(order, ['requiredRiderTitle', 'required_rider_title'], '普通');
            const urgent = isUrgentOrder(order);
            const urgentLine = urgent ? `<div class="order-urge-alert">🔔 ${MerchantApp.escapeHtml(urgentText(order))}</div>` : '';

            return `
        <article class="order-card ${urgent ? 'urgent-order' : ''}" data-id="${MerchantApp.escapeHtml(id)}">
          <div class="order-card-head">
            <b>${MerchantApp.escapeHtml(userName)}</b>
            <span class="status-pill ${urgent ? 'urgent' : pillClass(status)}">${urgent ? '催单中' : statusText(status)}</span>
          </div>

          ${urgentLine}

>>>>>>> origin/feature-user-rider-merchant
          <div class="order-items">
            ${MerchantApp.escapeHtml(summary)}
          </div>

          <div class="order-card-foot">
<<<<<<< HEAD
            <span class="muted small">${MerchantApp.escapeHtml(time || '刚刚')}</span>
=======
            <span class="muted small">${MerchantApp.escapeHtml(time || '刚刚')} · ${MerchantApp.escapeHtml(requiredTitle)}订单</span>
>>>>>>> origin/feature-user-rider-merchant
            <strong class="price">${MerchantApp.formatMoney(total)}</strong>
          </div>

          <div class="order-actions">
            <button data-action="detail" data-id="${MerchantApp.escapeHtml(id)}">详情</button>
            ${Number(status) === 0 ? `<button class="main" data-action="accept" data-id="${MerchantApp.escapeHtml(id)}">确认接单</button>` : ''}
<<<<<<< HEAD
            ${Number(status) === 1 || Number(status) === 2 ? `<button class="main" data-action="finish" data-id="${MerchantApp.escapeHtml(id)}">出餐完成</button>` : ''}
            ${Number(status) === 2 || Number(status) === 3 ? `<button class="main" data-action="rider" data-id="${MerchantApp.escapeHtml(id)}">召唤骑手</button>` : ''}
=======
            ${Number(status) === 1 ? `<button class="main" data-action="finish" data-id="${MerchantApp.escapeHtml(id)}">出餐完成</button>` : ''}
            ${Number(status) === 2 ? `<button class="ghost" type="button" disabled>已进入骑手接单池</button>` : ''}
>>>>>>> origin/feature-user-rider-merchant
          </div>
        </article>
      `;
        }).join('');

        box.querySelectorAll('button[data-action]').forEach(btn => {
            btn.addEventListener('click', handleOrderAction);
        });
    }

    function renderProducts() {
        const box = MerchantApp.$('#productPreviewList');

        if (!state.products.length) {
            box.innerHTML = '<div class="empty-state">暂无商品数据</div>';
            return;
        }

        box.innerHTML = state.products.map(product => {
            const name = MerchantApp.getField(product, ['name', 'productName', 'product_name'], '未命名商品');
            const price = MerchantApp.getField(product, ['price'], 0);
            const sales = MerchantApp.getField(product, ['monthlySales', 'monthly_sales', 'saleCount', 'sale_count'], 0);
            const status = Number(MerchantApp.getField(product, ['status'], 1)) === 1 ? '上架中' : '已下架';

            return `
        <article class="product-card">
          <div class="product-row">
            <span class="product-img">🍽️</span>
            <div class="product-info">
              <b>${MerchantApp.escapeHtml(name)}</b>
              <span>月售 ${sales} · ${status}</span>
            </div>
            <strong class="price">${MerchantApp.formatMoney(price)}</strong>
          </div>
        </article>
      `;
        }).join('');
    }

    async function handleOrderAction(event) {
        const action = event.currentTarget.dataset.action;
        const id = event.currentTarget.dataset.id;

        if (action === 'detail') {
<<<<<<< HEAD
            location.href = '/merchant/orders.html';
=======
            MerchantApp.toast(`订单详情功能预留：${id}`);
>>>>>>> origin/feature-user-rider-merchant
            return;
        }

        if (action === 'accept') {
            return postOrderAction('/merchant/order/accept', id, '已确认接单');
        }

        if (action === 'finish') {
            return postOrderAction('/merchant/order/finish-cooking', id, '已标记出餐完成');
        }
<<<<<<< HEAD

        if (action === 'rider') {
            return postOrderAction('/merchant/order/call-rider', id, '已召唤骑手');
        }
=======
>>>>>>> origin/feature-user-rider-merchant
    }

    async function postOrderAction(url, orderId, successMessage) {
        try {
            await MerchantApp.request(url, {
                method: 'POST',
                body: { orderId }
            });

            MerchantApp.toast(successMessage, 'success');
            await loadDashboard();
        } catch (e) {
            MerchantApp.toast(e.message || '操作失败，后端接口可能还未完成', 'error');
        }
    }

    function handleQuickAction(event) {
        const action = event.currentTarget.dataset.action;

        if (action === 'foods') {
            location.href = '/merchant/foods.html';
            return;
        }

        if (action === 'orders') {
            location.href = '/merchant/orders.html';
            return;
        }

<<<<<<< HEAD
        if (action === 'shop') {
            location.href = '/merchant/shop.html';
            return;
        }

        if (action === 'print') {
            location.href = '/merchant/order-print.html';
            return;
        }

        MerchantApp.toast('功能开发中');
=======
        const map = {
            shop: '店铺信息页面下一步开发：/merchant/shop.html',
            print: '打印订单功能将在订单详情中使用 window.print() 实现'
        };

        MerchantApp.toast(map[action] || '功能开发中');
>>>>>>> origin/feature-user-rider-merchant
    }

    async function logout() {
        try {
            await MerchantApp.request('/merchant/logout', {
                method: 'POST'
            });
        } catch (e) {
            // 即使后端退出失败，也清理本地缓存并回登录页。
        }

        MerchantApp.clearMerchant();
        location.href = '/merchant/login.html';
    }

    function mockOrders() {
        return [
            {
                orderId: 9001,
                userName: '张三',
                status: 0,
                totalPrice: 34,
                orderTime: '刚刚',
                summary: '经典双层芝士汉堡 × 1，冰镇可乐 × 1'
            },
            {
                orderId: 9002,
                userName: '李四',
                status: 1,
                totalPrice: 18.5,
                orderTime: '10 分钟前',
                summary: '香辣鸡腿堡 × 1'
            },
            {
                orderId: 9003,
                userName: '王五',
                status: 2,
                totalPrice: 25,
                orderTime: '18 分钟前',
                summary: '跑腿代购服务 × 1'
            }
        ];
    }

    function mockProducts() {
        return [
            { productId: 101, productName: '经典双层芝士汉堡', price: 25, monthlySales: 120, status: 1 },
            { productId: 102, productName: '香辣鸡腿堡', price: 18.5, monthlySales: 85, status: 1 },
            { productId: 103, productName: '冰镇可乐', price: 6, monthlySales: 200, status: 1 }
        ];
    }
})();