(function () {
    let orders = [];

    document.addEventListener('DOMContentLoaded', init);

    async function init() {
        await MerchantApp.requireMerchantLogin();

        MerchantApp.$('#printByIdBtn').addEventListener('click', printByInput);
        MerchantApp.$('#refreshPrintOrdersBtn').addEventListener('click', loadOrders);

        await loadOrders();
    }

    async function loadOrders() {
        const box = MerchantApp.$('#printOrderList');
        box.innerHTML = '<div class="empty-state">正在加载订单...</div>';

        try {
            const data = await MerchantApp.request('/merchant/orders/list');
            orders = Array.isArray(data) ? data : (data.records || data.list || data.rows || []);
            renderOrders();
        } catch (e) {
            box.innerHTML = `<div class="empty-state">${MerchantApp.escapeHtml(e.message || '订单加载失败')}</div>`;
        }
    }

    function renderOrders() {
        const box = MerchantApp.$('#printOrderList');

        if (!orders.length) {
            box.innerHTML = '<div class="empty-state">暂无可打印订单</div>';
            return;
        }

        box.innerHTML = orders.map(order => {
            const id = MerchantApp.getField(order, ['orderId', 'order_id', 'id'], '');
            const userName = MerchantApp.getField(order, ['userName', 'user_name'], '用户');
            const status = MerchantApp.getField(order, ['status'], 0);
            const summary = MerchantApp.getField(order, ['summary'], '暂无商品明细');
            const total = MerchantApp.getField(order, ['totalPrice', 'total_price', 'payAmount'], 0);
            const time = MerchantApp.getField(order, ['orderTime', 'order_time', 'createTime'], '');

            return `
                <article class="order-card">
                    <div class="order-card-head">
                        <b>订单 #${MerchantApp.escapeHtml(id)} · ${MerchantApp.escapeHtml(userName)}</b>
                        <span class="status-pill">${statusText(status)}</span>
                    </div>

                    <div class="order-items">
                        ${MerchantApp.escapeHtml(summary)}
                    </div>

                    <div class="order-card-foot">
                        <span class="muted small">${MerchantApp.escapeHtml(time || '')}</span>
                        <strong class="price">${MerchantApp.formatMoney(total)}</strong>
                    </div>

                    <div class="order-actions">
                        <button class="main" data-print-id="${MerchantApp.escapeHtml(id)}">打印订单</button>
                    </div>
                </article>
            `;
        }).join('');

        box.querySelectorAll('[data-print-id]').forEach(btn => {
            btn.addEventListener('click', () => printOrder(btn.dataset.printId));
        });
    }

    function printByInput() {
        const orderId = MerchantApp.$('#printOrderIdInput').value.trim();

        if (!orderId) {
            MerchantApp.toast('请输入订单ID', 'error');
            return;
        }

        printOrder(orderId);
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
})();