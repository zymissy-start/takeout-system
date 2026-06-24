(function () {
    const state = {
        rider: null,
        type: 'available',
        orders: []
    };

    document.addEventListener('DOMContentLoaded', init);

    async function init() {
        bindEvents();

        state.rider = await RiderApp.requireRiderLogin();

        await loadOrders();
    }

    function bindEvents() {
        RiderApp.$('#logoutBtn').addEventListener('click', logout);
        RiderApp.$('#refreshBtn').addEventListener('click', loadOrders);
        RiderApp.$('#closeDetailBtn').addEventListener('click', closeDetail);

        RiderApp.$all('#orderTabs button').forEach(btn => {
            btn.addEventListener('click', () => {
                RiderApp.$all('#orderTabs button').forEach(item => item.classList.remove('active'));
                btn.classList.add('active');
                state.type = btn.dataset.type;
                loadOrders();
            });
        });
    }

    async function loadOrders() {
        const box = RiderApp.$('#orderList');
        box.innerHTML = '<div class="rider-empty">正在加载订单...</div>';

        let url = '/rider/orders/available';

        if (state.type === 'delivering') {
            url = '/rider/orders/my?status=3';
        }

        if (state.type === 'finished') {
            url = '/rider/orders/my?status=4';
        }

        try {
            state.orders = await RiderApp.request(url);
            renderOrders();
        } catch (e) {
            box.innerHTML = `<div class="rider-empty">${RiderApp.escapeHtml(e.message || '订单加载失败')}</div>`;
        }
    }

    function statusText(status) {
        const map = {
            '2': '待骑手接单',
            '3': '配送中',
            '4': '已完成'
        };

        return map[String(status)] || '未知状态';
    }

    function renderOrders() {
        const box = RiderApp.$('#orderList');

        if (!state.orders.length) {
            box.innerHTML = '<div class="rider-empty">暂无订单</div>';
            return;
        }

        box.innerHTML = state.orders.map(order => {
            const id = RiderApp.getField(order, ['orderId', 'order_id'], '');
            const merchant = RiderApp.getField(order, ['merchantName', 'merchant_name'], '商家');
            const status = RiderApp.getField(order, ['status'], 2);
            const total = RiderApp.getField(order, ['totalPrice', 'total_price'], 0);
            const address = RiderApp.getField(order, ['address'], '');
            const summary = RiderApp.getField(order, ['summary'], '暂无商品明细');
            const wait = RiderApp.getField(order, ['waitMinutes', 'wait_minutes'], 0);
            const urged = Number(RiderApp.getField(order, ['isUrged', 'is_urged'], 0)) === 1;

            return `
        <article class="rider-order-card">
          <div class="rider-order-head">
            <b>订单 #${RiderApp.escapeHtml(id)} · ${RiderApp.escapeHtml(merchant)}</b>
            <span class="rider-pill ${urged ? 'urgent' : Number(status) === 4 ? 'done' : ''}">
              ${urged && Number(status) === 2 ? '用户已催单' : statusText(status)}
            </span>
          </div>

          <div class="rider-order-items">
            ${RiderApp.escapeHtml(summary)}<br>
            地址：${RiderApp.escapeHtml(address)}<br>
            等待时长：${wait} 分钟
          </div>

          <div class="rider-order-foot">
            <span class="muted small">预计收入：¥5.00</span>
            <strong>${RiderApp.formatMoney(total)}</strong>
          </div>

          <div class="rider-actions">
            <button data-action="detail" data-id="${RiderApp.escapeHtml(id)}">详情</button>
            ${Number(status) === 2 ? `<button class="main" data-action="accept" data-id="${RiderApp.escapeHtml(id)}">立即接单</button>` : ''}
            ${Number(status) === 3 ? `<button class="main" data-action="finish" data-id="${RiderApp.escapeHtml(id)}">完成配送</button>` : ''}
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
            postAction('/rider/orders/accept', id, '接单成功，开始配送');
            return;
        }

        if (action === 'finish') {
            postAction('/rider/orders/finish', id, '配送完成');
        }
    }

    async function openDetail(orderId) {
        const mask = RiderApp.$('#detailMask');
        const box = RiderApp.$('#detailContent');

        box.innerHTML = '正在加载订单详情...';
        mask.classList.remove('hidden');

        try {
            const detail = await RiderApp.request(`/rider/orders/detail?orderId=${encodeURIComponent(orderId)}`);
            renderDetail(detail);
        } catch (e) {
            box.innerHTML = `<div class="rider-empty">${RiderApp.escapeHtml(e.message || '订单详情加载失败')}</div>`;
        }
    }

    function closeDetail() {
        RiderApp.$('#detailMask').classList.add('hidden');
    }

    function renderDetail(order) {
        const box = RiderApp.$('#detailContent');
        const status = RiderApp.getField(order, ['status'], 2);
        const items = RiderApp.getField(order, ['items'], []);

        const itemRows = Array.isArray(items) && items.length
            ? items.map(item => {
                const name = RiderApp.getField(item, ['productName', 'product_name'], '商品');
                const quantity = RiderApp.getField(item, ['quantity'], 1);
                const price = RiderApp.getField(item, ['price'], 0);

                return `
            <div class="detail-row">
              <span>${RiderApp.escapeHtml(name)} × ${quantity}</span>
              <b>${RiderApp.formatMoney(Number(price) * Number(quantity))}</b>
            </div>
          `;
            }).join('')
            : '<p class="muted">暂无商品明细</p>';

        box.innerHTML = `
      <div class="detail-row">
        <span>订单编号</span>
        <b>#${RiderApp.escapeHtml(RiderApp.getField(order, ['orderId', 'order_id'], ''))}</b>
      </div>

      <div class="detail-row">
        <span>订单状态</span>
        <b>${statusText(status)}</b>
      </div>

      <div class="detail-row">
        <span>商家</span>
        <b>${RiderApp.escapeHtml(RiderApp.getField(order, ['merchantName', 'merchant_name'], ''))}</b>
      </div>

      <div class="detail-row">
        <span>用户</span>
        <b>${RiderApp.escapeHtml(RiderApp.getField(order, ['userName', 'user_name'], ''))}</b>
      </div>

      <div class="detail-title">商品明细</div>
      ${itemRows}

      <div class="detail-title">配送信息</div>
      <p class="muted">地址：${RiderApp.escapeHtml(RiderApp.getField(order, ['address'], ''))}</p>
      <p class="muted">备注：${RiderApp.escapeHtml(RiderApp.getField(order, ['remark'], '无'))}</p>
      <p class="muted">预计送达：${RiderApp.escapeHtml(RiderApp.getField(order, ['estimatedArrivalTime', 'estimated_arrival_time'], '接单后自动生成'))}</p>
      <p class="muted">完成时间：${RiderApp.escapeHtml(RiderApp.getField(order, ['finishTime', 'finish_time'], '未完成'))}</p>
    `;
    }

    async function postAction(url, orderId, successMessage) {
        try {
            await RiderApp.request(url, {
                method: 'POST',
                body: { orderId }
            });

            RiderApp.toast(successMessage, 'success');
            await loadOrders();
        } catch (e) {
            RiderApp.toast(e.message || '操作失败', 'error');
        }
    }

    async function logout() {
        try {
            await RiderApp.request('/rider/logout', { method: 'POST' });
        } catch (e) {
            // 忽略退出异常。
        }

        RiderApp.clearRider();
        location.href = '/rider/login.html';
    }
})();