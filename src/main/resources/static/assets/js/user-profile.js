(function () {
  const state = { addresses: [], editingId: null };
  document.addEventListener('DOMContentLoaded', init);

  async function init() {
    bindEvents();
    if (!App.isLoggedIn()) {
      renderGuestProfile();
      App.requireLogin('请先登录，登录后才能查看个人中心。', { redirect: true, auto: false, closable: true });
      return;
    }
    await Promise.all([loadProfile(), loadAddresses(), loadStats()]);
  }

  function renderGuestProfile() {
    App.$('#profileAvatar').textContent = '登';
    App.$('#profileName').textContent = '请先登录';
    App.$('#profilePhone').textContent = '登录后查看收货地址、订单和优惠信息';
    App.$('#creditScore').textContent = 0;
    App.$('#couponCount').textContent = 0;
    App.$('#orderCount').textContent = 0;
    App.$('#logoutBtn').innerHTML = '去登录 <span>›</span>';
    App.$('#addressList').innerHTML = `
      <div class="login-required-card compact">
        <div class="login-required-icon">👤</div>
        <h2>登录后管理个人信息</h2>
        <p>收货地址、订单、收藏和优惠券都需要登录后使用。</p>
        <button id="profileLoginBtn" class="primary-btn">去登录</button>
      </div>`;
    App.$('#profileLoginBtn').addEventListener('click', () => { location.href = App.loginRedirectUrl(); });
  }

  function bindEvents() {
    App.$('#addAddressBtn').addEventListener('click', () => { if (!App.requireLogin('请先登录，登录后才能新增收货地址。', { redirect: true, auto: false, closable: true })) return; openAddressModal(); });
    App.$('#closeAddressBtn').addEventListener('click', closeAddressModal);
    App.$('#saveAddressBtn').addEventListener('click', saveAddress);
    App.$('#logoutBtn').addEventListener('click', logout);
    App.$('#favoriteBtn').addEventListener('click', () => { if (!App.requireLogin('请先登录，登录后才能查看收藏。', { redirect: true, auto: false, closable: true })) return; App.toast('收藏页接口预留：/api/user/favorites'); });
    App.$('#couponBtn').addEventListener('click', () => { if (!App.requireLogin('请先登录，登录后才能查看优惠券。', { redirect: true, auto: false, closable: true })) return; App.toast('优惠券接口预留：/api/user/coupons'); });
  }

  async function loadProfile() {
    const user = await App.loadCurrentUser();
    const name = App.getField(user, ['realName', 'real_name', 'username'], '用户');
    App.$('#profileName').textContent = name;
    App.$('#profileAvatar').textContent = String(name).slice(0, 1);
    App.$('#profilePhone').textContent = App.getField(user, ['phone'], '未绑定手机号');
    App.$('#creditScore').textContent = App.getField(user, ['creditScore', 'credit_score'], 0);
  }

  async function loadStats() {
    try {
      const stats = await App.request('/api/user/stats');
      App.$('#couponCount').textContent = App.getField(stats, ['couponCount', 'coupon_count'], 0);
      App.$('#orderCount').textContent = App.getField(stats, ['orderCount', 'order_count'], 0);
    } catch (e) {
      App.$('#couponCount').textContent = 0;
      App.$('#orderCount').textContent = 0;
    }
  }

  async function loadAddresses() {
    try {
      const list = await App.request('/api/user/addresses');
      state.addresses = Array.isArray(list) ? list : [];
      renderAddresses();
    } catch (e) {
      App.$('#addressList').innerHTML = `<div class="empty-state">${App.escapeHtml(e.message || '地址加载失败')}</div>`;
    }
  }

  function renderAddresses() {
    const box = App.$('#addressList');
    if (!state.addresses.length) {
      box.innerHTML = `<div class="empty-state">暂无地址，请先新增收货地址</div>`;
      return;
    }
    box.innerHTML = state.addresses.map(addr => {
      const id = App.getField(addr, ['addressId', 'address_id', 'id'], '');
      const name = App.getField(addr, ['receiverName', 'receiver_name'], '收货人');
      const phone = App.getField(addr, ['receiverPhone', 'receiver_phone'], '');
      const detail = App.getField(addr, ['addressDetail', 'address_detail', 'address'], '');
      const isDefault = Number(App.getField(addr, ['isDefault', 'is_default'], 0)) === 1;
      return `<article class="address-card" data-id="${id}">
        <div class="top"><span>${App.escapeHtml(name)} ${App.escapeHtml(phone)}</span>${isDefault ? '<span class="default-tag">默认</span>' : ''}</div>
        <p>${App.escapeHtml(detail)}</p>
        <div class="actions">
          ${isDefault ? '' : `<button data-action="default" data-id="${id}">设默认</button>`}
          <button data-action="edit" data-id="${id}">编辑</button>
          <button data-action="delete" data-id="${id}">删除</button>
        </div>
      </article>`;
    }).join('');
    box.querySelectorAll('button[data-action]').forEach(btn => btn.addEventListener('click', handleAddressAction));
  }

  function openAddressModal(addr) {
    state.editingId = addr ? App.getField(addr, ['addressId', 'address_id', 'id'], null) : null;
    App.$('#addressModalTitle').textContent = state.editingId ? '编辑地址' : '新增地址';
    App.$('#receiverNameInput').value = addr ? App.getField(addr, ['receiverName', 'receiver_name'], '') : '';
    App.$('#receiverPhoneInput').value = addr ? App.getField(addr, ['receiverPhone', 'receiver_phone'], '') : '';
    App.$('#addressTextInput').value = addr ? App.getField(addr, ['addressDetail', 'address_detail', 'address'], '') : '';
    App.$('#defaultAddressInput').checked = addr ? Number(App.getField(addr, ['isDefault', 'is_default'], 0)) === 1 : false;
    App.$('#addressMask').classList.remove('hidden');
  }

  function closeAddressModal() { App.$('#addressMask').classList.add('hidden'); }

  async function saveAddress() {
    const body = {
      receiverName: App.$('#receiverNameInput').value.trim(),
      receiverPhone: App.$('#receiverPhoneInput').value.trim(),
      addressDetail: App.$('#addressTextInput').value.trim(),
      isDefault: App.$('#defaultAddressInput').checked ? 1 : 0
    };
    if (!body.receiverName || !body.receiverPhone || !body.addressDetail) return App.toast('请完整填写地址');
    try {
      if (state.editingId) {
        await App.request(`/api/user/addresses/${state.editingId}`, { method: 'PUT', body });
      } else {
        await App.request('/api/user/addresses', { method: 'POST', body });
      }
      App.toast('保存成功');
      closeAddressModal();
      loadAddresses();
    } catch (e) { App.toast(e.message || '保存失败'); }
  }

  async function handleAddressAction(event) {
    const { action, id } = event.currentTarget.dataset;
    const addr = state.addresses.find(a => String(App.getField(a, ['addressId', 'address_id', 'id'], '')) === String(id));
    if (action === 'edit') return openAddressModal(addr);
    if (action === 'delete') {
      if (!confirm('确定删除该地址吗？')) return;
      try {
        await App.request(`/api/user/addresses/${id}`, { method: 'DELETE' });
        App.toast('已删除');
        loadAddresses();
      } catch (e) { App.toast(e.message || '删除失败'); }
    }
    if (action === 'default') {
      try {
        await App.request(`/api/user/addresses/${id}/default`, { method: 'PUT' });
        App.toast('已设为默认地址');
        loadAddresses();
      } catch (e) { App.toast(e.message || '设置失败'); }
    }
  }

  function logout() {
    if (!App.isLoggedIn()) {
      location.href = App.loginRedirectUrl();
      return;
    }
    if (!confirm('确认退出登录吗？')) return;
    App.clearAuthCache();
    location.href = '/login.html';
  }
})();
