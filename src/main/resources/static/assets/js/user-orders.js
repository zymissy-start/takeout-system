(function () {
  const state = { status: 'all', orders: [] };

  document.addEventListener('DOMContentLoaded', init);

  async function init() {
    bindEvents();
    await loadOrders();
    const urlOrderId = new URLSearchParams(location.search).get('orderId');
    if (urlOrderId) openDetail(urlOrderId);
  }

  function bindEvents() {
    App.$('#orderTabs').querySelectorAll('button').forEach(btn => {
      btn.addEventListener('click', () => {
        App.$('#orderTabs').querySelectorAll('button').forEach(x => x.classList.remove('active'));
        btn.classList.add('active');
        state.status = btn.dataset.status;
        loadOrders();
      });
    });
    App.$('#closeDetailBtn').addEventListener('click', () => App.$('#orderDetailMask').classList.add('hidden'));
  }

  async function loadOrders() {
    const box = App.$('#orderList');
    box.innerHTML = `<div class="empty-state">正在加载订单...</div>`;
    const params = new URLSearchParams();
    if (state.status !== 'all') params.set('status', state.status);
    try {
      const data = await App.request(`/api/user/orders?${params.toString()}`);
      state.orders = Array.isArray(data) ? data : (data.records || data.list || data.rows || []);
      renderOrders();
    } catch (e) {
      box.innerHTML = `<div class="empty-state">${App.escapeHtml(e.message || '订单加载失败')}</div>`;
    }
  }

  function statusText(status) {
    const map = {
      '-1': '已取消',
      '0': '待商家接单',
      '1': '商家已接单',
      '2': '待骑手取餐',
      '3': '配送中',
      '4': '已完成'
    };
    return map[String(status)] || '未知状态';
  }

  function pillClass(status) {
    if (String(status) === '4') return 'done';
    if (String(status) === '-1') return 'cancel';
    return '';
  }


  function normalizeRiderTitle(title) {
    const value = String(title || '普通骑手').trim();
    if (!value || value === '普通') return '普通骑手';
    if (value === '闪电侠') return '闪电侠骑手';
    if (value === '单王' || value === '单王配送') return '单王配送骑手';
    return value.includes('骑手') ? value : `${value}骑手`;
  }

  function renderOrders() {
    const box = App.$('#orderList');
    if (!state.orders.length) {
      box.innerHTML = `<div class="empty-state">暂无订单</div>`;
      return;
    }
    box.innerHTML = state.orders.map(order => {
      const id = App.getField(order, ['orderId', 'order_id', 'id'], '');
      const merchantName = App.getField(order, ['merchantName', 'merchant_name', 'storeName'], '商家');
      const status = App.getField(order, ['status'], 0);
      const itemsText = renderItemsText(order);
      const total = App.getField(order, ['payAmount', 'pay_amount', 'totalPrice', 'total_price'], 0);
      const time = App.getField(order, ['orderTime', 'order_time', 'createTime'], '');
      const riderLevel = normalizeRiderTitle(App.getField(order, ['requiredRiderTitle', 'required_rider_title'], '普通骑手'));
      return `
        <article class="order-card" data-id="${id}">
          <div class="order-card-head">
            <b>${App.escapeHtml(merchantName)}</b>
            <span class="status-pill ${pillClass(status)}">${statusText(status)}</span>
          </div>
          <div class="order-items">${itemsText}</div>
          <div class="order-card-foot">
            <span class="muted small">${App.escapeHtml(time || '')} · ${App.escapeHtml(riderLevel)}匹配</span>
            <strong>${App.formatMoney(total)}</strong>
          </div>
          <div class="order-actions">
            <button data-action="detail" data-id="${id}">详情</button>
            ${Number(status) === 0 ? `<button data-action="cancel" data-id="${id}">取消订单</button>` : ''}
            ${[1,2,3].includes(Number(status)) ? `<button data-action="urge" data-id="${id}" class="main">催单</button>` : ''}
            ${[3,4].includes(Number(status)) ? `<button data-action="tip" data-id="${id}">打赏骑手</button>` : ''}
            ${Number(status) === 4 ? `<button data-action="comment" data-id="${id}">评价</button><button data-action="reorder" data-id="${id}" class="main">再来一单</button>` : ''}
          </div>
        </article>`;
    }).join('');
    box.querySelectorAll('button[data-action]').forEach(btn => btn.addEventListener('click', handleAction));
  }

  function renderItemsText(order) {
    const items = App.getField(order, ['items', 'orderItems'], []);
    if (Array.isArray(items) && items.length) {
      return items.map(item => `${App.escapeHtml(App.getField(item, ['productName', 'product_name', 'name'], '商品'))} × ${App.getField(item, ['quantity'], 1)}`).join('<br>');
    }
    return App.escapeHtml(App.getField(order, ['summary', 'itemsText'], '点击详情查看商品明细'));
  }

  async function handleAction(event) {
    const { action, id } = event.currentTarget.dataset;
    if (action === 'detail') return openDetail(id);
    if (action === 'cancel') return cancelOrder(id);
    if (action === 'urge') return urgeOrder(id);
    if (action === 'reorder') return reorder(id);
    if (action === 'tip') return tipOrder(id);
    if (action === 'comment') return commentOrder(id);
  }

  async function openDetail(orderId) {
    try {
      const order = await App.request(`/api/user/orders/${orderId}`);
      renderDetail(order);
      App.$('#orderDetailMask').classList.remove('hidden');
    } catch (e) {
      App.toast(e.message || '详情加载失败');
    }
  }

  function renderDetail(order) {
    const status = App.getField(order, ['status'], 0);
    const total = App.getField(order, ['payAmount', 'pay_amount', 'totalPrice', 'total_price'], 0);
    const items = App.getField(order, ['items', 'orderItems'], []);
    const logs = App.getField(order, ['statusLogs', 'status_logs'], []);
    const reminders = App.getField(order, ['reminders'], []);
    const times = Array.isArray(logs) && logs.length
      ? logs.map(log => [App.getField(log, ['statusText','status_text'], '订单状态更新'), App.getField(log, ['createTime','create_time'], ''), App.getField(log, ['remark'], '')])
      : [
        ['下单成功', App.getField(order, ['orderTime', 'order_time'], '')],
        ['商家接单', App.getField(order, ['merchantConfirmTime', 'merchant_confirm_time'], '')],
        ['出餐完成', App.getField(order, ['kitchenFinishTime', 'kitchen_finish_time'], '')],
        ['骑手配送', App.getField(order, ['estimatedArrivalTime', 'estimated_arrival_time'], '')],
        ['订单完成', App.getField(order, ['finishTime', 'finish_time'], '')]
      ].filter(x => x[1]);
    App.$('#orderDetail').innerHTML = `
      <div class="order-line"><b>订单状态</b><span class="status-pill ${pillClass(status)}">${statusText(status)}</span></div>
      <div class="timeline">${times.map(t => `<div><b>${App.escapeHtml(t[0])}</b><span>${App.escapeHtml(t[1] || '')}</span>${t[2] ? `<p class="muted small">${App.escapeHtml(t[2])}</p>` : ''}</div>`).join('') || '<div><b>等待商家处理</b><span>订单已提交</span></div>'}</div>
      <div class="section-title compact"><h2>商品明细</h2></div>
      ${(Array.isArray(items) ? items : []).map(item => `
        <div class="order-line"><span>${App.escapeHtml(App.getField(item, ['productName','product_name','name'], '商品'))} × ${App.getField(item, ['quantity'], 1)}</span><b>${App.formatMoney(Number(App.getField(item, ['price'], 0)) * Number(App.getField(item, ['quantity'], 1)))}</b></div>
      `).join('') || '<p class="muted">暂无商品明细，需后端返回 items 字段。</p>'}
      <div class="price-row total"><span>合计</span><b>${App.formatMoney(total)}</b></div>
      ${Array.isArray(reminders) && reminders.length ? `<div class="reminder-list"><b>催单记录</b>${reminders.map(r => `<p>${App.escapeHtml(App.getField(r, ['content'], '已催单'))}<span>${App.escapeHtml(App.getField(r, ['status'], 'UNREAD'))}</span></p>`).join('')}</div>` : ''}
      <p class="muted small">骑手匹配：${App.escapeHtml(normalizeRiderTitle(App.getField(order, ['requiredRiderTitle','required_rider_title'], '普通骑手')))}；已打赏：${App.formatMoney(App.getField(order, ['tipAmount','tip_amount'], 0))}</p>
      ${[3, 4].includes(Number(status)) ? `<div class="order-actions detail-actions"><button class="main" data-detail-tip="${App.escapeHtml(App.getField(order, ['orderId','order_id'], ''))}">打赏骑手</button></div>` : ''}
      <p class="muted small">收货地址：${App.escapeHtml(App.getField(order, ['receiverAddress','receiver_address','address'], ''))}</p>
      <p class="muted small">备注：${App.escapeHtml(App.getField(order, ['remark'], '无'))}</p>`;

    const tipBtn = App.$('#orderDetail button[data-detail-tip]');
    if (tipBtn) {
      tipBtn.addEventListener('click', () => openTipModal(tipBtn.dataset.detailTip));
    }
  }

  async function cancelOrder(orderId) {
    if (!confirm('确认取消该订单吗？')) return;
    try {
      await App.request(`/api/user/orders/${orderId}/cancel`, { method: 'PUT' });
      App.toast('订单已取消');
      loadOrders();
    } catch (e) { App.toast(e.message || '取消失败'); }
  }

  async function urgeOrder(orderId) {
    try {
      const data = await App.request(`/api/user/orders/${orderId}/urge`, { method: 'PUT' });
      App.toast(App.getField(data || {}, ['message'], '已提醒商家/骑手'));
      loadOrders();
    } catch (e) { App.toast(e.message || '催单失败'); }
  }

  function tipOrder(orderId) {
    openTipModal(orderId);
  }

  function openTipModal(orderId) {
    ensureTipModal();
    App.$('#tipOrderId').value = orderId;
    App.$('#tipAmountInput').value = '2';
    App.$('#tipMask').classList.remove('hidden');
  }

  function closeTipModal() {
    const mask = App.$('#tipMask');
    if (mask) mask.classList.add('hidden');
  }

  function ensureTipModal() {
    if (App.$('#tipMask')) return;

    const div = document.createElement('div');
    div.id = 'tipMask';
    div.className = 'modal-mask hidden';
    div.innerHTML = `
      <div class="modal tip-modal">
        <div class="modal-title">
          <h3>打赏骑手</h3>
          <button id="closeTipBtn" class="icon-btn" type="button">×</button>
        </div>
        <input id="tipOrderId" type="hidden" />
        <p class="muted small">打赏会直接累加到该订单骑手收入中，配送中或已完成订单可打赏。</p>
        <div class="tip-presets">
          <button type="button" data-tip="2">¥2</button>
          <button type="button" data-tip="5">¥5</button>
          <button type="button" data-tip="10">¥10</button>
        </div>
        <input id="tipAmountInput" class="input" type="number" min="1" max="100" step="0.01" value="2" placeholder="输入打赏金额，最高100元" />
        <div class="order-actions detail-actions">
          <button id="submitTipBtn" class="main" type="button">确认打赏</button>
        </div>
      </div>
    `;
    document.body.appendChild(div);

    App.$('#closeTipBtn').addEventListener('click', closeTipModal);
    div.addEventListener('click', event => {
      if (event.target === div) closeTipModal();
    });
    div.querySelectorAll('button[data-tip]').forEach(btn => {
      btn.addEventListener('click', () => {
        App.$('#tipAmountInput').value = btn.dataset.tip;
      });
    });
    App.$('#submitTipBtn').addEventListener('click', submitTip);
  }

  async function submitTip() {
    const orderId = App.$('#tipOrderId').value;
    const amount = Number(App.$('#tipAmountInput').value || 0);

    if (!amount || amount <= 0) {
      App.toast('请输入正确的打赏金额');
      return;
    }

    if (amount > 100) {
      App.toast('单次打赏不能超过100元');
      return;
    }

    try {
      const data = await App.request(`/api/user/orders/${orderId}/tip`, {
        method: 'POST',
        body: { tipAmount: amount, amount }
      });
      App.toast(App.getField(data || {}, ['message'], '打赏成功，骑手端已入账'));
      closeTipModal();
      await loadOrders();
      if (!App.$('#orderDetailMask').classList.contains('hidden')) {
        await openDetail(orderId);
      }
    } catch (e) {
      App.toast(e.message || '打赏失败');
    }
  }

  async function reorder(orderId) {
    try {
      const order = await App.request(`/api/user/orders/${orderId}`);
      const items = App.getField(order, ['items', 'orderItems'], []);
      if (!Array.isArray(items) || !items.length) return App.toast('后端未返回商品明细，无法再来一单');
      Cart.clear();
      items.forEach(item => Cart.add({
        productId: App.getField(item, ['productId','product_id'], 0),
        merchantId: App.getField(order, ['merchantId','merchant_id'], 0),
        name: App.getField(item, ['productName','product_name','name'], '商品'),
        price: App.getField(item, ['price'], 0),
        imageUrl: App.getField(item, ['imageUrl','image_url'], '')
      }, Number(App.getField(item, ['quantity'], 1))));
      App.toast('已加入购物车');
      setTimeout(() => location.href = '/user/index.html', 650);
    } catch (e) { App.toast(e.message || '操作失败'); }
  }

  async function commentOrder(orderId) {
    const content = prompt('请输入评价内容：');
    if (!content) return;
    const score = Number(prompt('请输入评分 1-5：', '5') || 5);
    try {
      await App.request(`/api/user/orders/${orderId}/comments`, { method: 'POST', body: { score, content } });
      App.toast('评价成功');
    } catch (e) { App.toast(e.message || '评价失败'); }
  }
})();
