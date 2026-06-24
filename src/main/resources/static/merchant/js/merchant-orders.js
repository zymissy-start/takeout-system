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
        return '';
    }

    function renderOrders() {
        const box = MerchantApp.$('#orderList');

        if (!state.orders.length) {
            box.innerHTML = '<div class="empty-state">暂无订单</div>';
            return;
        }

        box.innerHTML = state.orders.map(order => {
            const id = MerchantApp.getField(order, ['orderId', 'order_id', 'id'], '');
            const userName = MerchantApp.getField(order, ['userName', 'user_name'], '用户');
            const status = MerchantApp.getField(order, ['status'], 0);
            const summary = MerchantApp.getField(order, ['summary'], '暂无商品明细');
            const total = MerchantApp.getField(order, ['totalPrice', 'total_price', 'payAmount'], 0);
            const time = MerchantApp.getField(order, ['orderTime', 'order_time', 'createTime'], '');

            return `
        <article class="order-card" data-id="${MerchantApp.escapeHtml(id)}">
          <div class="order-card-head">
            <b>订单 #${MerchantApp.escapeHtml(id)} · ${MerchantApp.escapeHtml(userName)}</b>
            <span class="status-pill ${pillClass(status)}">${statusText(status)}</span>
          </div>

          <div class="order-items">
            ${MerchantApp.escapeHtml(summary)}
          </div>

          <div class="order-card-foot">
            <span class="muted small">${MerchantApp.escapeHtml(time || '')}</span>
            <strong class="price">${MerchantApp.formatMoney(total)}</strong>
          </div>

          <div class="order-actions">
            <button data-action="detail" data-id="${MerchantApp.escapeHtml(id)}">详情</button>
            <button data-action="print" data-id="${MerchantApp.escapeHtml(id)}">打印订单</button>
            ${Number(status) === 0 ? `<button class="main" data-action="accept" data-id="${MerchantApp.escapeHtml(id)}">确认接单</button>` : ''}
            ${Number(status) === 1 ? `<button class="main" data-action="finish" data-id="${MerchantApp.escapeHtml(id)}">出餐完成</button>` : ''}
            ${Number(status) === 2 ? `<button class="main" data-action="rider" data-id="${MerchantApp.escapeHtml(id)}">召唤骑手</button>` : ''}
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

        if (action === 'print') {
            printOrder(id);
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

        if (action === 'rider') {
            postOrderAction('/merchant/order/call-rider', id, '召唤骑手成功');
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
        const orderId = MerchantApp.getField(order, ['orderId', 'order_id'], '');

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
        <b>#${MerchantApp.escapeHtml(orderId)}</b>
      </div>

      <div class="detail-row">
        <span>订单状态</span>
        <span class="status-pill ${pillClass(status)}">${statusText(status)}</span>
      </div>

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
      <p class="muted">骑手姓名：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['riderName', 'rider_name'], '暂未分配'))}</p>
      <p class="muted">骑手电话：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['riderPhone', 'rider_phone'], '暂未分配'))}</p>

      <div class="detail-section-title">流程时间</div>
      <p class="muted">商家接单：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['merchantConfirmTime', 'merchant_confirm_time'], '未接单'))}</p>
      <p class="muted">出餐完成：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['kitchenFinishTime', 'kitchen_finish_time'], '未出餐'))}</p>
      <p class="muted">预计送达：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['estimatedArrivalTime', 'estimated_arrival_time'], '暂无'))}</p>
      <p class="muted">完成时间：${MerchantApp.escapeHtml(MerchantApp.getField(order, ['finishTime', 'finish_time'], '未完成'))}</p>

      <div class="order-actions" style="margin-top:16px;">
        <button class="main" id="detailPrintBtn">打印订单</button>
      </div>
    `;

        MerchantApp.$('#detailPrintBtn').addEventListener('click', () => printOrder(orderId));
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

    async function printOrder(orderId) {
        try {
            const order = await MerchantApp.request(`/merchant/orders/print-data?orderId=${encodeURIComponent(orderId)}`);
            renderReceipt(order);

            setTimeout(() => {
                window.print();
            }, 100);
        } catch (e) {
            MerchantApp.toast(e.message || '打印失败', 'error');
        }
    }

    function renderReceipt(order) {
        let box = document.querySelector('#merchantPrintReceipt');

        if (!box) {
            box = document.createElement('section');
            box.id = 'merchantPrintReceipt';
            box.className = 'merchant-print-receipt';
            document.body.appendChild(box);
        }

        const items = MerchantApp.getField(order, ['items'], []) || [];

        box.innerHTML = `
            <header class="print-receipt-header">
                <h2>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['merchantName', 'merchant_name'], '饭点优选商家'))}</h2>
                <p>订单打印小票</p>
            </header>

            <div class="print-receipt-info">
                <p>店铺电话：<b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['merchantPhone', 'merchant_phone'], '未设置'))}</b></p>
                <p>店铺地址：<b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['merchantAddress', 'merchant_address'], '未设置'))}</b></p>
                <p>订单编号：<b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['orderId', 'order_id'], '--'))}</b></p>
                <p>订单状态：<b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['statusText', 'status_text'], '--'))}</b></p>
                <p>下单时间：<b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['orderTime', 'order_time'], '--'))}</b></p>
                <p>打印时间：<b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['printTime', 'print_time'], '--'))}</b></p>
                <p>顾客姓名：<b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['userName', 'user_name'], '--'))}</b></p>
                <p>收货地址：<b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['address'], '--'))}</b></p>
                <p>订单备注：<b>${MerchantApp.escapeHtml(MerchantApp.getField(order, ['remark'], '无') || '无')}</b></p>
            </div>

            <table class="print-receipt-table">
                <thead>
                    <tr>
                        <th>商品</th>
                        <th>数量</th>
                        <th>单价</th>
                        <th>小计</th>
                    </tr>
                </thead>
                <tbody>
                    ${items.length ? items.map(item => `
                        <tr>
                            <td>${MerchantApp.escapeHtml(MerchantApp.getField(item, ['productName', 'product_name'], '未命名商品'))}</td>
                            <td>${MerchantApp.escapeHtml(MerchantApp.getField(item, ['quantity'], 0))}</td>
                            <td>${MerchantApp.formatMoney(MerchantApp.getField(item, ['price'], 0))}</td>
                            <td>${MerchantApp.formatMoney(MerchantApp.getField(item, ['subtotal'], 0))}</td>
                        </tr>
                    `).join('') : `
                        <tr>
                            <td colspan="4">暂无商品明细</td>
                        </tr>
                    `}
                </tbody>
            </table>

            <footer class="print-receipt-footer">
                <p>合计金额：<b>${MerchantApp.formatMoney(MerchantApp.getField(order, ['totalPrice', 'total_price'], 0))}</b></p>
                <p>请核对商品后及时出餐。</p>
            </footer>
        `;
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