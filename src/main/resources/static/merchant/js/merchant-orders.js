(function () {
    const state = {
        merchant: null,
        status: '',
        orders: []
    };

    document.addEventListener('DOMContentLoaded', init);

    async function init() {
        bindEvents();

        state.merchant = await MerchantApp.requireMerchantLogin();

        await loadOrders();
    }

    function bindEvents() {
        MerchantApp.$('#logoutBtn').addEventListener('click', logout);
        MerchantApp.$('#refreshBtn').addEventListener('click', loadOrders);
        MerchantApp.$('#closeDetailBtn').addEventListener('click', closeDetail);

        MerchantApp.$all('#orderTabs button').forEach(btn => {
            btn.addEventListener('click', () => {
                MerchantApp.$all('#orderTabs button').forEach(item => item.classList.remove('active'));
                btn.classList.add('active');
                state.status = btn.dataset.status;
                loadOrders();
            });
        });
    }

    async function loadOrders() {
        const box = MerchantApp.$('#orderList');
        box.innerHTML = '<div class="empty-state">正在加载订单...</div>';

        const params = new URLSearchParams();

        if (state.status !== '') {
            params.set('status', state.status);
        }

        try {
            state.orders = await MerchantApp.request(`/merchant/orders/list?${params.toString()}`);
            renderOrders();
        } catch (e) {
            box.innerHTML = `<div class="empty-state">${MerchantApp.escapeHtml(e.message || '订单加载失败')}</div>`;
        }
    }

    function statusText(status) {
        const map = {
            '0': '待商家接单',
            '1': '制作中',
            '2': '待骑手接单',
            '3': '配送中',
            '4': '已完成'
        };

        return map[String(status)] || '未知状态';
    }

    function pillClass(status) {
        if (String(status) === '4') return 'done';
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
        return '';
    }

    function renderOrders() {
        const box = MerchantApp.$('#orderList');

        if (!state.orders.length) {
            box.innerHTML = '<div class="empty-state">暂无订单</div>';
            return;
        }

        const urgentCount = state.orders.filter(isUrgentOrder).length;
        const urgentBanner = urgentCount > 0
            ? `<div class="merchant-urge-banner">⚠️ 当前列表有 ${urgentCount} 个催单订单，请优先处理。</div>`
            : '';

        box.innerHTML = urgentBanner + state.orders.map(order => {
            const id = MerchantApp.getField(order, ['orderId', 'order_id', 'id'], '');
            const userName = MerchantApp.getField(order, ['userName', 'user_name'], '用户');
            const status = MerchantApp.getField(order, ['status'], 0);
            const summary = MerchantApp.getField(order, ['summary'], '暂无商品明细');
            const total = MerchantApp.getField(order, ['totalPrice', 'total_price', 'payAmount'], 0);
            const time = MerchantApp.getField(order, ['orderTime', 'order_time', 'createTime'], '');
            const requiredTitle = MerchantApp.getField(order, ['requiredRiderTitle', 'required_rider_title'], '普通');
            const urgent = isUrgentOrder(order);
            const urgentLine = urgent ? `<div class="order-urge-alert">🔔 ${MerchantApp.escapeHtml(urgentText(order))}</div>` : '';

            return `
        <article class="order-card ${urgent ? 'urgent-order' : ''}" data-id="${MerchantApp.escapeHtml(id)}">
          <div class="order-card-head">
            <b>订单 #${MerchantApp.escapeHtml(id)} · ${MerchantApp.escapeHtml(userName)}</b>
            <span class="status-pill ${urgent ? 'urgent' : pillClass(status)}">${urgent ? '催单中' : statusText(status)}</span>
          </div>

          ${urgentLine}

          <div class="order-items">
            ${MerchantApp.escapeHtml(summary)}
          </div>

          <div class="order-card-foot">
            <span class="muted small">${MerchantApp.escapeHtml(time || '')} · ${MerchantApp.escapeHtml(requiredTitle)}订单</span>
            <strong class="price">${MerchantApp.formatMoney(total)}</strong>
          </div>

          <div class="order-actions">
            <button data-action="detail" data-id="${MerchantApp.escapeHtml(id)}">详情</button>
            ${Number(status) === 0 ? `<button class="main" data-action="accept" data-id="${MerchantApp.escapeHtml(id)}">确认接单</button>` : ''}
            ${Number(status) === 1 ? `<button class="main" data-action="finish" data-id="${MerchantApp.escapeHtml(id)}">出餐完成</button>` : ''}
            ${Number(status) === 2 ? `<button class="ghost" type="button" disabled>等待骑手接单</button>` : ''}
          </div>
        </article>
      `;
        }).join('');

        box.querySelectorAll('button[data-action]').forEach(btn => {
            btn.addEventListener('click', handleOrderAction);
        });
    }

    function handleOrderAction(event) {
        const action = event.currentTarget.dataset.action;
        const id = event.currentTarget.dataset.id;

        if (action === 'detail') {
            openDetail(id);
            return;
        }

        if (action === 'accept') {
            postOrderAction('/merchant/order/accept', id, '接单成功');
            return;
        }

        if (action === 'finish') {
            postOrderAction('/merchant/order/finish-cooking', id, '出餐完成');
            return;
        }
    }

    async function openDetail(orderId) {
        const mask = MerchantApp.$('#orderDetailMask');
        const box = MerchantApp.$('#orderDetailContent');

        box.innerHTML = '正在加载订单详情...';
        mask.classList.remove('hidden');

        try {
            const detail = await MerchantApp.request(`/merchant/orders/detail?orderId=${encodeURIComponent(orderId)}`);
            renderDetail(detail);
        } catch (e) {
            box.innerHTML = `<div class="empty-state">${MerchantApp.escapeHtml(e.message || '详情加载失败')}</div>`;
        }
    }

    function closeDetail() {
        MerchantApp.$('#orderDetailMask').classList.add('hidden');
    }

    function renderDetail(order) {
        const box = MerchantApp.$('#orderDetailContent');
        const status = MerchantApp.getField(order, ['status'], 0);
        const total = MerchantApp.getField(order, ['totalPrice', 'total_price'], 0);
        const items = MerchantApp.getField(order, ['items'], []);

        const rows = Array.isArray(items) && items.length
            ? items.map(item => {
                const name = MerchantApp.getField(item, ['productName', 'product_name'], '商品');
                const quantity = MerchantApp.getField(item, ['quantity'], 1);
                const price = MerchantApp.getField(item, ['price'], 0);

                return `
            <div class="detail-row">
              <span>${MerchantApp.escapeHtml(name)} × ${quantity}</span>
              <b>${MerchantApp.formatMoney(Number(price) * Number(quantity))}</b>
            </div>
          `;
            }).join('')
            : '<p class="muted">暂无商品明细</p>';

        box.innerHTML = `
      <div class="detail-row">
        <span>订单编号</span>
        <b>#${MerchantApp.escapeHtml(MerchantApp.getField(order, ['orderId', 'order_id'], ''))}</b>
      </div>

      <div class="detail-row">
        <span>订单状态</span>
        <span class="status-pill ${pillClass(status)}">${statusText(status)}</span>
      </div>

      ${Number(status) !== 4 && Number(status) !== -1 && Number(MerchantApp.getField(order, ['isUrged', 'is_urged'], 0)) === 1 ? `
      <div class="order-urge-alert detail-alert">
        🔔 催单提醒：该订单曾被用户或骑手催促，请优先处理。
      </div>` : ''}

      <div class="detail-row">
        <span>下单用户</span>
        <b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['userName', 'user_name'], '用户'))}</b>
      </div>

      <div class="detail-row">
        <span>下单时间</span>
        <b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['orderTime', 'order_time'], ''))}</b>
      </div>

      <div class="detail-section-title">商品明细</div>
      ${rows}

      <div class="detail-row total">
        <span>订单总价</span>
        <b>${MerchantApp.formatMoney(total)}</b>
      </div>

      <div class="detail-section-title">配送信息</div>

      <p class="muted">收货地址：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['address'], ''))}</p>
      <p class="muted">订单备注：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['remark'], '无'))}</p>
      <p class="muted">用户电话：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['userPhone', 'user_phone'], '暂无'))}</p>
      <p class="muted">骑手姓名：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['riderName', 'rider_name'], '暂未分配'))}</p>
      <p class="muted">骑手电话：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['riderPhone', 'rider_phone'], '暂未分配'))}</p>

      ${(MerchantApp.getField(order, ['userPhone', 'user_phone'], '') || MerchantApp.getField(order, ['riderPhone', 'rider_phone'], '')) ? `
      <div class="detail-section-title">快捷联系</div>
      <div class="order-actions" style="margin-top:8px;">
        <button class="main" onclick="location.href='/merchant/contact.html?targetId=${MerchantApp.escapeHtml(MerchantApp.getField(order, ['userId', 'user_id'], ''))}&targetRole=1&orderId=${MerchantApp.escapeHtml(MerchantApp.getField(order, ['orderId', 'order_id'], ''))}'">💬 联系用户</button>
        ${(MerchantApp.getField(order, ['riderId', 'rider_id'], '') || MerchantApp.getField(order, ['riderPhone', 'rider_phone'], '')) ? `<button class="main" onclick="location.href='/merchant/contact.html?targetId=${MerchantApp.escapeHtml(MerchantApp.getField(order, ['riderId', 'rider_id'], ''))}&targetRole=3&orderId=${MerchantApp.escapeHtml(MerchantApp.getField(order, ['orderId', 'order_id'], ''))}'">💬 联系骑手</button>` : ''}
      </div>` : ''}

      <div class="detail-section-title">流程时间</div>
      <p class="muted">商家接单：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['merchantConfirmTime', 'merchant_confirm_time'], '未接单'))}</p>
      <p class="muted">出餐完成：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['kitchenFinishTime', 'kitchen_finish_time'], '未出餐'))}</p>
      <p class="muted">预计送达：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['estimatedArrivalTime', 'estimated_arrival_time'], '暂无'))}</p>
      <p class="muted">完成时间：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['finishTime', 'finish_time'], '未完成'))}</p>
    `;
    }

    async function postOrderAction(url, orderId, successMessage) {
        try {
            await MerchantApp.request(url, {
                method: 'POST',
                body: { orderId }
            });

            MerchantApp.toast(successMessage, 'success');
            await loadOrders();
        } catch (e) {
            MerchantApp.toast(e.message || '操作失败', 'error');
        }
    }

    async function logout() {
        try {
            await MerchantApp.request('/merchant/logout', {
                method: 'POST'
            });
        } catch (e) {
            // 退出失败也清理本地状态。
        }

        MerchantApp.clearMerchant();
        location.href = '/merchant/login.html';
    }
})();