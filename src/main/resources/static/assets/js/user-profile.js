(function () {
<<<<<<< HEAD
  const state = { addresses: [], editingId: null, picked: null };
=======
  const state = { addresses: [], editingId: null, picked: null, levelDetails: null };
>>>>>>> origin/feature-user-rider-merchant
  document.addEventListener('DOMContentLoaded', init);
  window.addEventListener('message', handleMapMessage);
  window.addEventListener('storage', handleStoragePick);

  async function init() {
    bindEvents();
    await Promise.all([loadProfile(), loadAddresses(), loadStats(), loadLevel()]);
    applyLocalPickedLocation();
  }

  function bindEvents() {
    App.$('#addAddressBtn').addEventListener('click', () => openAddressModal());
    App.$('#closeAddressBtn').addEventListener('click', closeAddressModal);
    App.$('#saveAddressBtn').addEventListener('click', saveAddress);
    App.$('#geoLocationBtn').addEventListener('click', locateByBrowser);
    App.$('#mapPickBtn').addEventListener('click', openMapPicker);
    App.$('#logoutBtn').addEventListener('click', logout);
    App.$('#favoriteBtn').addEventListener('click', () => App.toast('收藏页接口预留：/api/user/favorites'));
    App.$('#couponBtn').addEventListener('click', () => App.toast('优惠券接口预留：/api/user/coupons'));
<<<<<<< HEAD
=======
    App.$('#levelDetailBtn').addEventListener('click', openLevelDetail);
    App.$('#closeLevelDetailBtn').addEventListener('click', closeLevelDetail);
  }


  async function openLevelDetail() {
    const mask = App.$('#levelDetailMask');
    const box = App.$('#levelDetailContent');
    mask.classList.remove('hidden');
    box.innerHTML = '正在加载等级详情...';
    try {
      state.levelDetails = await App.request('/api/user/level/details');
      renderLevelDetail(state.levelDetails);
    } catch (e) {
      box.innerHTML = `<div class="empty-state">${App.escapeHtml(e.message || '等级详情加载失败')}</div>`;
    }
  }

  function closeLevelDetail() {
    App.$('#levelDetailMask').classList.add('hidden');
  }

  function renderLevelDetail(data) {
    const box = App.$('#levelDetailContent');
    const current = App.getField(data, ['current'], {});
    const rules = App.getField(data, ['orderLevelRules', 'order_level_rules'], []);
    const riderRules = App.getField(data, ['riderLevelRules', 'rider_level_rules'], []);
    const orderCount = App.getField(current, ['orderCount', 'order_count'], 0);
    const orderTitle = App.getField(current, ['orderTitle', 'order_title'], '普通');
    const matched = App.getField(current, ['matchedRiderTitle', 'matched_rider_title'], '普通骑手');
    const nextNeed = App.getField(current, ['nextNeedOrders', 'next_need_orders'], 0);
    const currentLevel = Number(App.getField(current, ['orderLevel', 'order_level'], 0));

    const ruleCards = Array.isArray(rules) ? rules.map(rule => {
      const level = Number(App.getField(rule, ['level'], 0));
      const privileges = App.getField(rule, ['privileges'], []);
      const items = Array.isArray(privileges)
        ? privileges.map(item => `<li>${App.escapeHtml(item)}</li>`).join('')
        : '';
      return `<article class="level-rule-card ${level === currentLevel ? 'active' : ''}">
        <h4>${App.escapeHtml(App.getField(rule, ['title'], '等级'))}</h4>
        <p>条件：${App.escapeHtml(App.getField(rule, ['range'], ''))}</p>
        <p>匹配：${App.escapeHtml(App.getField(rule, ['matchedRider'], ''))}</p>
        <ul>${items}</ul>
      </article>`;
    }).join('') : '';

    const riderRows = Array.isArray(riderRules) ? riderRules.map(rule => `
      <article class="level-rule-card">
        <h4>${App.escapeHtml(App.getField(rule, ['title'], '骑手等级'))}</h4>
        <p>条件：${App.escapeHtml(App.getField(rule, ['condition'], ''))}</p>
        <p>可接：${App.escapeHtml(App.getField(rule, ['canTake'], ''))}</p>
      </article>
    `).join('') : '';

    box.innerHTML = `
      <div class="level-summary-box">
        <b>当前用户等级：${App.escapeHtml(orderTitle)}</b>
        <p class="muted">你已有 ${App.escapeHtml(orderCount)} 单有效点餐，当前订单会匹配：${App.escapeHtml(matched)}。</p>
        <p class="muted">${Number(nextNeed) > 0 ? `距离下一等级还差 ${nextNeed} 单。` : '你已达到最高用户等级。'}</p>
      </div>
      <h4>用户等级</h4>
      <div class="level-rule-grid">${ruleCards}</div>
      <h4>骑手等级与接单范围</h4>
      <div class="level-rule-grid">${riderRows}</div>
      <div class="level-note">${App.escapeHtml(App.getField(data, ['note'], '高等级用户会优先匹配高等级骑手。'))}</div>
    `;
>>>>>>> origin/feature-user-rider-merchant
  }

  async function loadProfile() {
    const user = await App.loadCurrentUser();
    const name = App.getField(user, ['realName', 'real_name', 'username'], '用户');
    App.$('#profileName').textContent = name;
    App.$('#profileAvatar').textContent = String(name).slice(0, 1);
    App.$('#profilePhone').textContent = App.getField(user, ['phone'], '未绑定手机号');
    App.$('#creditScore').textContent = App.getField(user, ['creditScore', 'credit_score'], 0);
    const levelName = App.getField(user, ['levelName', 'level_name'], 'Lv1 普通用户');
    App.$('#profileLevelBadge').textContent = levelName;
  }

  async function loadLevel() {
    try {
      const level = await App.request('/api/user/level');
      const levelName = App.getField(level, ['levelName', 'level_name'], 'Lv1 普通用户');
      const progress = Number(App.getField(level, ['progressPercent', 'progress_percent'], 0));
      const rate = Number(App.getField(level, ['deliveryDiscountRate', 'delivery_discount_rate'], 1));
      const cooldown = App.getField(level, ['remindCooldownSeconds', 'remind_cooldown_seconds'], 180);
      const nextNeed = App.getField(level, ['nextNeedGrowth', 'next_need_growth'], 0);
      App.$('#profileLevelBadge').textContent = levelName;
      App.$('#levelNameText').textContent = levelName;
      App.$('#levelProgressBar').style.width = Math.max(0, Math.min(100, progress)) + '%';
<<<<<<< HEAD
      App.$('#levelDesc').textContent = App.getField(level, ['description'], '完成订单、评价订单可提升成长值');
      App.$('#deliveryDiscountText').textContent = rate >= 1 ? '无折扣' : `${Math.round(rate * 100)}折`;
      App.$('#remindCooldownText').textContent = `${cooldown}秒`;
      App.$('#nextNeedText').textContent = Number(nextNeed) <= 0 ? '已满级' : `${nextNeed}成长值`;
=======
      const orderTitle = App.getField(level, ['orderTitle', 'order_title'], '普通');
      const matchedRider = App.getField(level, ['matchedRiderTitle', 'matched_rider_title'], '普通骑手');
      const orderCount = App.getField(level, ['orderCount', 'order_count'], 0);
      App.$('#levelDesc').textContent = `当前用户等级：${orderTitle}，有效点餐 ${orderCount} 单；下单后优先匹配：${matchedRider}。`;
      App.$('#deliveryDiscountText').textContent = rate >= 1 ? '无折扣' : `${Math.round(rate * 100)}折`;
      App.$('#remindCooldownText').textContent = `${cooldown}秒`;
      const nextNeedOrders = App.getField(level, ['nextNeedOrders', 'next_need_orders'], null);
      App.$('#nextNeedText').textContent = nextNeedOrders !== null && Number(nextNeedOrders) > 0
        ? `${nextNeedOrders}单`
        : (Number(nextNeed) <= 0 ? '已满级' : `${nextNeed}成长值`);
>>>>>>> origin/feature-user-rider-merchant
    } catch (e) {
      App.$('#levelDesc').textContent = '等级信息加载失败，请检查 /api/user/level';
    }
  }

  async function loadStats() {
    try {
      const stats = await App.request('/api/user/stats');
      App.$('#couponCount').textContent = App.getField(stats, ['couponCount', 'coupon_count'], 0);
      App.$('#orderCount').textContent = App.getField(stats, ['orderCount', 'order_count'], 0);
      App.$('#creditScore').textContent = App.getField(stats, ['creditScore', 'credit_score'], App.$('#creditScore').textContent || 0);
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
      box.innerHTML = `<div class="empty-state">暂无地址，请先新增收货地址。建议使用真实定位或地图选点保存经纬度。</div>`;
      return;
    }
    box.innerHTML = state.addresses.map(addr => {
      const id = App.getField(addr, ['addressId', 'address_id', 'id'], '');
      const name = App.getField(addr, ['receiverName', 'receiver_name'], '收货人');
      const phone = App.getField(addr, ['receiverPhone', 'receiver_phone'], '');
      const detail = App.getField(addr, ['addressDetail', 'address_detail', 'address'], '');
      const lat = App.getField(addr, ['latitude'], '');
      const lng = App.getField(addr, ['longitude'], '');
      const isDefault = Number(App.getField(addr, ['isDefault', 'is_default'], 0)) === 1;
      return `<article class="address-card" data-id="${id}">
        <div class="top"><span>${App.escapeHtml(name)} ${App.escapeHtml(phone)}</span>${isDefault ? '<span class="default-tag">默认</span>' : ''}</div>
        <p>${App.escapeHtml(detail)}</p>
        <p class="muted small">坐标：${lat && lng ? `${lat}, ${lng}` : '未定位'}</p>
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
    App.$('#latitudeInput').value = addr ? App.getField(addr, ['latitude'], '') : '';
    App.$('#longitudeInput').value = addr ? App.getField(addr, ['longitude'], '') : '';
    App.$('#defaultAddressInput').checked = addr ? Number(App.getField(addr, ['isDefault', 'is_default'], 0)) === 1 : false;
    updateCoordText();
    App.$('#addressMask').classList.remove('hidden');
  }

  function closeAddressModal() { App.$('#addressMask').classList.add('hidden'); }

  async function locateByBrowser() {
    if (!navigator.geolocation) return App.toast('浏览器不支持真实定位');
    App.toast('正在请求定位权限...');
    navigator.geolocation.getCurrentPosition(pos => {
      App.$('#latitudeInput').value = Number(pos.coords.latitude).toFixed(6);
      App.$('#longitudeInput').value = Number(pos.coords.longitude).toFixed(6);
      if (!App.$('#addressTextInput').value.trim()) {
        App.$('#addressTextInput').value = '当前位置，请补充详细地址';
      }
      updateCoordText();
      App.toast('定位成功，请补充详细地址');
    }, () => {
      App.toast('定位失败：请允许浏览器定位权限，或使用地图选点');
    }, { enableHighAccuracy: true, timeout: 10000, maximumAge: 30000 });
  }

  function openMapPicker() {
    const lat = App.$('#latitudeInput').value || '';
    const lng = App.$('#longitudeInput').value || '';
    const url = `/user/map-picker.html?lat=${encodeURIComponent(lat)}&lng=${encodeURIComponent(lng)}`;
    window.open(url, 'mapPicker', 'width=900,height=680');
    App.toast('请在地图窗口选择位置后点确定');
  }

  function handleMapMessage(event) {
    if (event.origin !== location.origin) return;
    if (!event.data || event.data.type !== 'USER_MAP_PICKED') return;
    applyPickedLocation(event.data.payload);
  }

  function handleStoragePick(event) {
    if (event.key === 'pickedLocation') applyLocalPickedLocation();
  }

  function applyLocalPickedLocation() {
    try {
      const picked = JSON.parse(localStorage.getItem('pickedLocation') || 'null');
      if (picked && picked.latitude && picked.longitude && !App.$('#addressMask').classList.contains('hidden')) applyPickedLocation(picked);
    } catch (e) {}
  }

  function applyPickedLocation(picked) {
    state.picked = picked;
    App.$('#latitudeInput').value = picked.latitude || '';
    App.$('#longitudeInput').value = picked.longitude || '';
    if (picked.addressDetail) App.$('#addressTextInput').value = picked.addressDetail;
    updateCoordText();
    App.toast('地图位置已填入');
  }

  function updateCoordText() {
    App.$('#latText').textContent = App.$('#latitudeInput').value || '未选择';
    App.$('#lngText').textContent = App.$('#longitudeInput').value || '未选择';
  }

  async function saveAddress() {
    const body = {
      receiverName: App.$('#receiverNameInput').value.trim(),
      receiverPhone: App.$('#receiverPhoneInput').value.trim(),
      addressDetail: App.$('#addressTextInput').value.trim(),
      latitude: App.$('#latitudeInput').value ? Number(App.$('#latitudeInput').value) : null,
      longitude: App.$('#longitudeInput').value ? Number(App.$('#longitudeInput').value) : null,
      isDefault: App.$('#defaultAddressInput').checked ? 1 : 0
    };
    if (!body.receiverName || !body.receiverPhone || !body.addressDetail) return App.toast('请完整填写地址');
    if (!body.latitude || !body.longitude) return App.toast('请先真实定位或地图选点，保存经纬度');
    try {
      if (state.editingId) {
        await App.request(`/api/user/addresses/${state.editingId}`, { method: 'PUT', body });
      } else {
        await App.request('/api/user/addresses', { method: 'POST', body });
      }
      App.toast('保存成功');
      closeAddressModal();
      localStorage.removeItem('pickedLocation');
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
    if (!confirm('确认退出登录吗？')) return;
    localStorage.removeItem('token');
    localStorage.removeItem('Authorization');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userInfo');
    location.href = '/login.html';
  }
})();
