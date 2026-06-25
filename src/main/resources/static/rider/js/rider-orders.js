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
        const logoutBtn = RiderApp.$('#logoutBtn');
        const refreshBtn = RiderApp.$('#refreshBtn');
        const closeBtn = RiderApp.$('#closeDetailBtn');
        const mask = RiderApp.$('#detailMask');

        if (logoutBtn) logoutBtn.addEventListener('click', logout);
        if (refreshBtn) refreshBtn.addEventListener('click', loadOrders);
        if (closeBtn) closeBtn.addEventListener('click', closeDetail);
        if (mask) {
            mask.addEventListener('click', event => {
                if (event.target === mask) closeDetail();
            });
        }

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
        if (!box) return;
        box.innerHTML = '<div class="mt-empty">正在加载订单...</div>';

        let url = '/rider/orders/available';
        if (state.type === 'delivering') url = '/rider/orders/my?status=3';
        if (state.type === 'finished') url = '/rider/orders/my?status=4';

        try {
            const data = await RiderApp.request(url);
            state.orders = normalizeOrders(data);
            renderOrders();
        } catch (e) {
            box.innerHTML = `<div class="mt-empty error">${RiderApp.escapeHtml(e.message || '订单加载失败')}</div>`;
        }
    }

    function normalizeOrders(data) {
        if (Array.isArray(data)) return data;
        if (!data) return [];
        if (Array.isArray(data.records)) return data.records;
        if (Array.isArray(data.list)) return data.list;
        if (Array.isArray(data.rows)) return data.rows;
        return [];
    }

    function statusText(status) {
        const map = {
            '-1': '已取消',
            '0': '待商家接单',
            '1': '商家制作中',
            '2': '待骑手接单',
            '3': '骑手配送中',
            '4': '已完成'
        };
        return map[String(status)] || '未知状态';
    }

    function statusTagClass(status, urged) {
        if (urged) return 'hot';
        if (Number(status) === 2) return 'blue';
        if (Number(status) === 3) return 'green';
        if (Number(status) === 4) return 'done';
        return '';
    }

    function isActiveUrged(order) {
        const status = Number(RiderApp.getField(order, ['status'], 0));
        return [2, 3].includes(status) && Number(RiderApp.getField(order, ['isUrged', 'is_urged'], 0)) === 1;
    }

    function normalizeRiderTitle(title) {
        const value = String(title || '普通骑手').trim();
        if (!value || value === '普通') return '普通骑手';
        if (value === '闪电侠') return '闪电侠骑手';
        if (value === '单王' || value === '单王配送') return '单王配送骑手';
        return value.includes('骑手') ? value : `${value}骑手`;
    }

    function emptyHint() {
        if (state.type === 'available') {
            return `
        <div class="mt-empty order-hall-empty">
          <b>暂无可接订单</b>
          <p>常见原因：商家还没点“出餐完成”；订单已被其他骑手接走；或你的骑手等级低于订单要求。</p>
          <a class="mt-empty-link" href="/rider/wait-cooking.html">查看待出餐订单并催商家</a>
        </div>
      `;
        }
        if (state.type === 'delivering') {
            return '<div class="mt-empty">当前没有配送中的订单。</div>';
        }
        return '<div class="mt-empty">暂无已完成订单。</div>';
    }

    function renderOrders() {
        const box = RiderApp.$('#orderList');
        if (!box) return;

        if (!state.orders.length) {
            box.innerHTML = emptyHint();
            return;
        }

        const urgentCount = state.orders.filter(isActiveUrged).length;
        const urgentBanner = urgentCount > 0
            ? `<div class="rider-urge-banner">🔔 有 ${urgentCount} 个订单被用户催单，请优先处理。</div>`
            : '';

        box.innerHTML = urgentBanner + state.orders.map(orderCardHtml).join('');

        box.querySelectorAll('button[data-action]').forEach(btn => {
            btn.addEventListener('click', handleOrderAction);
        });
    }

    function orderCardHtml(order) {
        const id = RiderApp.getField(order, ['orderId', 'order_id'], '');
        const merchant = RiderApp.getField(order, ['merchantName', 'merchant_name'], '商家');
        const status = Number(RiderApp.getField(order, ['status'], 2));
        const total = RiderApp.getField(order, ['totalPrice', 'total_price'], 0);
        const address = RiderApp.getField(order, ['address'], '暂无地址');
        const summary = RiderApp.getField(order, ['summary'], '暂无商品明细');
        const wait = RiderApp.getField(order, ['waitMinutes', 'wait_minutes'], 0);
        const urged = isActiveUrged(order);
        const requiredTitle = normalizeRiderTitle(RiderApp.getField(order, ['requiredRiderTitle', 'required_rider_title'], '普通骑手'));
        const requiredLevel = Number(RiderApp.getField(order, ['requiredRiderLevel', 'required_rider_level'], 0));
        const tipAmount = Number(RiderApp.getField(order, ['tipAmount', 'tip_amount'], 0));
        const eta = RiderApp.getField(order, ['estimatedArrivalTime', 'estimated_arrival_time'], '接单后自动生成');
        const kitchenTime = RiderApp.getField(order, ['kitchenFinishTime', 'kitchen_finish_time'], '暂无');

        const levelText = requiredLevel <= 0 ? '普通用户订单' : requiredLevel === 1 ? '优先用户订单' : '尊享用户订单';
        const actionButtons = renderActionButtons(id, status);

        return `
      <article class="mt-order-card order-hall-card ${urged ? 'urgent-order' : ''}">
        <div class="mt-order-head">
          <b>#${RiderApp.escapeHtml(id)} · ${RiderApp.escapeHtml(merchant)}</b>
          <span class="mt-tag ${statusTagClass(status, urged)}">${urged ? '用户催单' : statusText(status)}</span>
        </div>

        ${urged ? `<div class="rider-urge-alert">🔔 用户已催单，请尽快接单或完成配送。</div>` : ''}

        <div class="order-hall-meta">
          <span>${RiderApp.escapeHtml(levelText)}</span>
          <span>要求：${RiderApp.escapeHtml(requiredTitle)}</span>
          <span>等待：${RiderApp.escapeHtml(wait)} 分钟</span>
          ${tipAmount > 0 ? `<span class="tip">打赏 ${RiderApp.formatMoney(tipAmount)}</span>` : ''}
        </div>

        <p>
          ${RiderApp.escapeHtml(summary)}<br>
          地址：${RiderApp.escapeHtml(address)}<br>
          出餐时间：${RiderApp.escapeHtml(kitchenTime)}<br>
          预计送达：${RiderApp.escapeHtml(eta)}
        </p>

        <div class="mt-order-head order-hall-foot">
          <span class="mt-tag">预计收入 ¥5.00${tipAmount > 0 ? ` + ${RiderApp.formatMoney(tipAmount)}` : ''}</span>
          <b>${RiderApp.formatMoney(total)}</b>
        </div>

        <div class="mt-actions order-hall-actions">
          <button data-action="detail" data-id="${RiderApp.escapeHtml(id)}">查看详情</button>
          ${actionButtons}
        </div>
      </article>
    `;
    }

    function renderActionButtons(id, status) {
        if (status === 2) {
            return `<button class="green" data-action="accept" data-id="${RiderApp.escapeHtml(id)}">立即接单</button>`;
        }
        if (status === 3) {
            return `<button class="green" data-action="finish" data-id="${RiderApp.escapeHtml(id)}">完成配送</button>`;
        }
        return '';
    }

    function handleOrderAction(event) {
        const action = event.currentTarget.dataset.action;
        const id = event.currentTarget.dataset.id;

        if (action === 'detail') return openDetail(id);
        if (action === 'accept') return acceptOrder(id);
        if (action === 'finish') return finishOrder(id);
    }

    async function acceptOrder(orderId) {
        if (!orderId) return RiderApp.toast('订单编号缺失，无法接单', 'error');
        const ok = window.confirm(`确认接下订单 #${orderId} 吗？`);
        if (!ok) return;

        try {
            await RiderApp.request('/rider/orders/accept', {
                method: 'POST',
                body: { orderId }
            });

            RiderApp.toast('接单成功，开始导航', 'success');

            setTimeout(() => {
                location.href = '/rider/navigation.html?orderId=' + encodeURIComponent(orderId);
            }, 650);
        } catch (e) {
            RiderApp.toast(e.message || '接单失败：订单可能已被接走、商家还未出餐，或骑手等级不足', 'error');
        }
    }

    async function finishOrder(orderId) {
        if (!orderId) return RiderApp.toast('订单编号缺失，无法完成配送', 'error');
        const ok = window.confirm(`确认订单 #${orderId} 已送达吗？`);
        if (!ok) return;

        try {
            await RiderApp.request('/rider/orders/finish', {
                method: 'POST',
                body: { orderId }
            });
            RiderApp.toast('配送完成，催单提醒已处理', 'success');
            switchTab('finished');
            await loadOrders();
        } catch (e) {
            RiderApp.toast(e.message || '完成配送失败，请确认订单状态', 'error');
        }
    }

    function switchTab(type) {
        state.type = type;
        RiderApp.$all('#orderTabs button').forEach(item => {
            item.classList.toggle('active', item.dataset.type === type);
        });
    }

    async function openDetail(orderId) {
        const mask = RiderApp.$('#detailMask');
        const box = RiderApp.$('#detailContent');
        if (!mask || !box) return;

        box.innerHTML = '正在加载订单详情...';
        mask.classList.remove('hidden');

        try {
            const detail = await RiderApp.request(`/rider/orders/detail?orderId=${encodeURIComponent(orderId)}`);
            renderDetail(detail || {});
        } catch (e) {
            box.innerHTML = `<div class="mt-empty error">${RiderApp.escapeHtml(e.message || '订单详情加载失败')}</div>`;
        }
    }

    function closeDetail() {
        const mask = RiderApp.$('#detailMask');
        if (mask) mask.classList.add('hidden');
    }

    function renderDetail(order) {
        const box = RiderApp.$('#detailContent');
        if (!box) return;

        const orderId = RiderApp.getField(order, ['orderId', 'order_id'], '');
        const status = Number(RiderApp.getField(order, ['status'], 2));
        const items = RiderApp.getField(order, ['items'], []);
        const urged = isActiveUrged(order);

        const itemRows = Array.isArray(items) && items.length
            ? items.map(item => {
                const name = RiderApp.getField(item, ['productName', 'product_name'], '商品');
                const quantity = RiderApp.getField(item, ['quantity'], 1);
                const price = RiderApp.getField(item, ['price'], 0);
                return `
            <div class="mt-detail-row">
              <span>${RiderApp.escapeHtml(name)} × ${RiderApp.escapeHtml(quantity)}</span>
              <b>${RiderApp.formatMoney(Number(price) * Number(quantity))}</b>
            </div>`;
            }).join('')
            : '<p class="muted">暂无商品明细</p>';

        box.innerHTML = `
      ${urged ? `<div class="rider-urge-alert detail-alert">🔔 用户催单提醒：请尽快接单或完成配送。</div>` : ''}

      <div class="mt-detail-row"><span>订单编号</span><b>#${RiderApp.escapeHtml(orderId)}</b></div>
      <div class="mt-detail-row"><span>订单状态</span><b>${statusText(status)}</b></div>
      <div class="mt-detail-row"><span>商家</span><b>${RiderApp.escapeHtml(RiderApp.getField(order, ['merchantName', 'merchant_name'], '商家'))}</b></div>
      <div class="mt-detail-row"><span>用户</span><b>${RiderApp.escapeHtml(RiderApp.getField(order, ['userName', 'user_name'], '用户'))}</b></div>
      <div class="mt-detail-row"><span>订单金额</span><b>${RiderApp.formatMoney(RiderApp.getField(order, ['totalPrice', 'total_price'], 0))}</b></div>

      <div class="mt-detail-title">商品明细</div>
      ${itemRows}

      <div class="mt-detail-title">配送信息</div>
      <p class="muted">地址：${RiderApp.escapeHtml(RiderApp.getField(order, ['address'], '暂无地址'))}</p>
      <p class="muted">备注：${RiderApp.escapeHtml(RiderApp.getField(order, ['remark'], '无'))}</p>
      <p class="muted">预计送达：${RiderApp.escapeHtml(RiderApp.getField(order, ['estimatedArrivalTime', 'estimated_arrival_time'], '接单后自动生成'))}</p>
      <p class="muted">完成时间：${RiderApp.escapeHtml(RiderApp.getField(order, ['finishTime', 'finish_time'], '未完成'))}</p>

      <div class="mt-actions order-hall-actions detail-actions">
        ${renderActionButtons(orderId, status)}
      </div>
    `;

        box.querySelectorAll('button[data-action]').forEach(btn => {
            btn.addEventListener('click', async event => {
                closeDetail();
                handleOrderAction(event);
            });
        });
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
