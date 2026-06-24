(function () {
    const state = {
        rider: null,
        statistics: {},
        currentLocation: null,
        availableOrders: [],
        deliveringOrders: [],
        finishedOrders: []
    };

    const fallbackLocation = {
        latitude: 34.664816,
        longitude: 112.365565,
        label: '模拟位置：西苑校区东门附近',
        mode: '模拟定位'
    };

    document.addEventListener('DOMContentLoaded', init);

    async function init() {
        state.rider = await RiderApp.requireRiderLogin();

        const refreshBtn = RiderApp.$('#refreshBtn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => location.reload());
        }

        const module = document.body.dataset.riderModule;

        if (module === 'recommend') await initRecommend();
        if (module === 'current') await initCurrent();
        if (module === 'tips') await initTips();
        if (module === 'growth') await initGrowth();
        if (module === 'location') await initLocationModule();
        if (module === 'wait-cooking') await initWaitCooking();
    }

    async function initRecommend() {
        await ensureLocation();
        await loadAvailableOrders();

        const sorted = sortOrdersByDistance(state.availableOrders);
        renderRecommend(sorted);
    }

    async function initCurrent() {
        const box = RiderApp.$('#moduleContent');
        box.innerHTML = '<div class="rapp-empty">正在加载当前配送...</div>';

        try {
            state.deliveringOrders = await RiderApp.request('/rider/orders/my?status=3');
            renderCurrent(state.deliveringOrders || []);
        } catch (e) {
            box.innerHTML = `<div class="rapp-empty">${RiderApp.escapeHtml(e.message || '加载失败')}</div>`;
        }
    }

    async function initTips() {
        const box = RiderApp.$('#moduleContent');
        box.innerHTML = '<div class="rapp-empty">正在加载打赏订单...</div>';

        try {
            const delivering = await RiderApp.request('/rider/orders/my?status=3');
            const finished = await RiderApp.request('/rider/orders/my?status=4');

            state.deliveringOrders = delivering || [];
            state.finishedOrders = finished || [];

            renderTips();
        } catch (e) {
            box.innerHTML = `<div class="rapp-empty">${RiderApp.escapeHtml(e.message || '加载失败')}</div>`;
        }
    }

    async function initGrowth() {
        try {
            state.statistics = await RiderApp.request('/rider/dashboard/statistics');
        } catch (e) {
            RiderApp.toast(e.message || '成长数据加载失败', 'error');
            state.statistics = {};
        }

        renderGrowth();
    }

    async function initLocationModule() {
        await ensureLocation();
        await loadAvailableOrders();
        renderLocationModule();
    }

    async function initWaitCooking() {
        const box = RiderApp.$('#moduleContent');
        box.innerHTML = '<div class="rapp-empty">正在加载待出餐订单...</div>';

        try {
            const orders = await RiderApp.request('/rider/orders/wait-cooking');
            renderWaitCooking(orders || []);
        } catch (e) {
            box.innerHTML = `<div class="rapp-empty">${RiderApp.escapeHtml(e.message || '加载失败')}</div>`;
        }
    }

    async function loadAvailableOrders() {
        try {
            state.availableOrders = await RiderApp.request('/rider/orders/available');
        } catch (e) {
            state.availableOrders = [];
            RiderApp.toast(e.message || '可接订单加载失败', 'error');
        }
    }

    function renderRecommend(orders) {
        const box = RiderApp.$('#moduleContent');

        if (!orders.length) {
            box.innerHTML = '<div class="rapp-empty">暂无可接订单</div>';
            return;
        }

        box.innerHTML = orders.map((order, index) => {
            const id = RiderApp.getField(order, ['orderId', 'order_id'], '');
            const merchant = RiderApp.getField(order, ['merchantName', 'merchant_name'], '商家');
            const address = RiderApp.getField(order, ['address'], '');
            const total = RiderApp.getField(order, ['totalPrice', 'total_price'], 0);
            const wait = RiderApp.getField(order, ['waitMinutes', 'wait_minutes'], 0);
<<<<<<< HEAD
            const urged = Number(RiderApp.getField(order, ['isUrged', 'is_urged'], 0)) === 1;
=======
            const status = Number(RiderApp.getField(order, ['status'], 0));
            const urged = [2, 3].includes(status) && Number(RiderApp.getField(order, ['isUrged', 'is_urged'], 0)) === 1;
>>>>>>> origin/feature-user-rider-merchant
            const distance = computeRouteDistance(order);

            return `
        <article class="rapp-order-card ${index === 0 ? 'nearest' : ''}">
          <div class="rapp-order-head">
            <b>${index === 0 ? '最近 · ' : ''}#${RiderApp.escapeHtml(id)} · ${RiderApp.escapeHtml(merchant)}</b>
            <span class="rapp-tag ${urged ? 'hot' : ''}">${urged ? '用户已催单' : '普通订单'}</span>
          </div>

          <div class="rapp-route-mini">
            <span class="rider">骑手</span>
            <i></i>
            <span class="merchant">商家</span>
            <i></i>
            <span class="user">用户</span>
          </div>

          <p>
            收货地址：${RiderApp.escapeHtml(address)}<br>
            等待时间：${wait} 分钟<br>
            预估路线：${distance.toFixed(2)} km
          </p>

          <div class="rapp-order-head">
            <span class="rapp-tag">距离优先推荐</span>
            <b>${RiderApp.formatMoney(total)}</b>
          </div>
        </article>
      `;
        }).join('');
    }

    function renderCurrent(orders) {
        const box = RiderApp.$('#moduleContent');

        if (!orders.length) {
            box.innerHTML = '<div class="rapp-empty">当前没有配送中的订单</div>';
            return;
        }

        box.innerHTML = orders.map(order => {
            const id = RiderApp.getField(order, ['orderId', 'order_id'], '');
            const merchant = RiderApp.getField(order, ['merchantName', 'merchant_name'], '商家');
            const address = RiderApp.getField(order, ['address'], '');
            const eta = RiderApp.getField(order, ['estimatedArrivalTime', 'estimated_arrival_time'], '暂无');
            const tip = RiderApp.getField(order, ['tipAmount', 'tip_amount'], 0);
            const distance = computeRouteDistance(order);

            return `
        <article class="rapp-order-card">
          <div class="rapp-order-head">
            <b>#${RiderApp.escapeHtml(id)} · ${RiderApp.escapeHtml(merchant)}</b>
            <span class="rapp-tag">配送中</span>
          </div>

          <p>
            配送地址：${RiderApp.escapeHtml(address)}<br>
            预计送达：${RiderApp.escapeHtml(eta)}<br>
            当前打赏：${RiderApp.formatMoney(tip)}<br>
            预估路线：${distance.toFixed(2)} km
          </p>

          <div class="rapp-actions">
            <button class="green" data-id="${RiderApp.escapeHtml(id)}">完成配送</button>
          </div>
        </article>
      `;
        }).join('');

        box.querySelectorAll('button[data-id]').forEach(btn => {
            btn.addEventListener('click', () => finishOrder(btn.dataset.id));
        });
    }

    function renderTips() {
        const box = RiderApp.$('#moduleContent');

        const orders = [
            ...(state.deliveringOrders || []),
            ...(state.finishedOrders || [])
        ];

        const tipped = orders.filter(order => {
            return Number(RiderApp.getField(order, ['tipAmount', 'tip_amount'], 0)) > 0;
        });

        const total = tipped.reduce((sum, order) => {
            return sum + Number(RiderApp.getField(order, ['tipAmount', 'tip_amount'], 0));
        }, 0);

        const tipTotal = RiderApp.$('#tipTotal');
        if (tipTotal) {
            tipTotal.textContent = `合计 ${RiderApp.formatMoney(total)}`;
        }

        if (!tipped.length) {
            box.innerHTML = '<div class="rapp-empty">暂无用户打赏订单</div>';
            return;
        }

        box.innerHTML = tipped.map(order => {
            const id = RiderApp.getField(order, ['orderId', 'order_id'], '');
            const merchant = RiderApp.getField(order, ['merchantName', 'merchant_name'], '商家');
            const address = RiderApp.getField(order, ['address'], '');
            const tip = RiderApp.getField(order, ['tipAmount', 'tip_amount'], 0);
            const status = Number(RiderApp.getField(order, ['status'], 3));

            return `
        <article class="rapp-order-card">
          <div class="rapp-order-head">
            <b>🎁 #${RiderApp.escapeHtml(id)} · ${RiderApp.escapeHtml(merchant)}</b>
            <span class="rapp-tag">${status === 4 ? '已完成' : '配送中'}</span>
          </div>

          <p>${RiderApp.escapeHtml(address || '暂无地址')}</p>

          <div class="rapp-order-head">
            <span class="rapp-tag">用户打赏</span>
            <b>${RiderApp.formatMoney(tip)}</b>
          </div>
        </article>
      `;
        }).join('');
    }

    function renderGrowth() {
        const stats = state.statistics;

        const finishedCount = Number(RiderApp.getField(stats, ['todayFinishedCount'], 0));
        const avgSpeed = Number(RiderApp.getField(stats, ['avgSpeed'], 10.5));
        const info = computeLevelInfo(finishedCount, avgSpeed);

        RiderApp.$('#growthTitle').textContent = info.title;
        RiderApp.$('#growthDesc').textContent = info.desc;
        RiderApp.$('#growthBadge').textContent = `${finishedCount} 单`;

        const activePath = RiderApp.$('#growthPathActive');
        const flashDot = RiderApp.$('#growthFlashDot');
        const kingDot = RiderApp.$('#growthKingDot');

<<<<<<< HEAD
        const progress = Math.min(1, finishedCount / 10);
=======
        const progress = Math.min(1, finishedCount / 15);
>>>>>>> origin/feature-user-rider-merchant
        const length = 760;

        activePath.style.strokeDasharray = length;
        activePath.style.strokeDashoffset = length * (1 - progress);

<<<<<<< HEAD
        flashDot.classList.toggle('active', finishedCount >= 5);
        kingDot.classList.toggle('active', finishedCount >= 10);
=======
        flashDot.classList.toggle('active', finishedCount >= 10);
        kingDot.classList.toggle('active', finishedCount >= 15);
>>>>>>> origin/feature-user-rider-merchant

        RiderApp.$('#moduleContent').innerHTML = `
      <article class="rapp-order-card">
        <div class="rapp-order-head">
          <b>速度计算逻辑</b>
          <span class="rapp-tag">动态衰减</span>
        </div>
        <p>
          基础速度：${avgSpeed.toFixed(1)} 分钟 / 公里<br>
          疲劳衰减：+${info.fatiguePenalty.toFixed(1)} 分钟 / 公里<br>
          等级加成：${info.levelBonusText}<br>
          实际速度：${info.effectiveSpeed.toFixed(1)} 分钟 / 公里
        </p>
      </article>
    `;
    }

    function renderLocationModule() {
        const sorted = sortOrdersByDistance(state.availableOrders);
        const nearest = sorted[0];

        let merchantText = '暂无可接订单';
        let userText = '暂无可接订单';
        let nearestText = '暂无可接订单';
        let distanceText = '暂无路线';

        if (nearest) {
            const merchant = resolveMerchantPoint(nearest);
            const user = resolveAddressPoint(RiderApp.getField(nearest, ['address'], ''));
            const distance = computeRouteDistance(nearest);
            const id = RiderApp.getField(nearest, ['orderId', 'order_id'], '');

            merchantText = merchant.label;
            userText = user.label;
            nearestText = `#${id}`;
            distanceText = `预计路线 ${distance.toFixed(2)} km`;
        }

        RiderApp.$('#moduleContent').innerHTML = `
      <article>
        <span>骑手位置</span>
        <b>${RiderApp.escapeHtml(state.currentLocation.label)}</b>
        <p>${state.currentLocation.longitude}, ${state.currentLocation.latitude}</p>
      </article>

      <article>
        <span>商家位置</span>
        <b>${RiderApp.escapeHtml(merchantText)}</b>
        <p>根据订单商家名称映射坐标</p>
      </article>

      <article>
        <span>用户位置</span>
        <b>${RiderApp.escapeHtml(userText)}</b>
        <p>根据用户收货地址映射坐标</p>
      </article>

      <article>
        <span>最近订单</span>
        <b>${RiderApp.escapeHtml(nearestText)}</b>
        <p>${RiderApp.escapeHtml(distanceText)}</p>
      </article>
    `;
    }

    function renderWaitCooking(orders) {
        const box = RiderApp.$('#moduleContent');

        if (!orders.length) {
            box.innerHTML = '<div class="rapp-empty">暂无待出餐订单</div>';
            return;
        }

        box.innerHTML = orders.map(order => {
            const id = RiderApp.getField(order, ['orderId', 'order_id'], '');
            const merchant = RiderApp.getField(order, ['merchantName', 'merchant_name'], '商家');
            const wait = RiderApp.getField(order, ['waitMinutes', 'wait_minutes'], 0);
            const urgeCount = RiderApp.getField(order, ['riderUrgeCount', 'rider_urge_count'], 0);
            const urgeTime = RiderApp.getField(order, ['riderUrgeTime', 'rider_urge_time'], '暂无');
            const summary = RiderApp.getField(order, ['summary'], '暂无商品明细');

            return `
        <article class="rapp-order-card">
          <div class="rapp-order-head">
            <b>#${RiderApp.escapeHtml(id)} · ${RiderApp.escapeHtml(merchant)}</b>
            <span class="rapp-tag hot">制作中</span>
          </div>

          <p>
            ${RiderApp.escapeHtml(summary)}<br>
            已等待：${wait} 分钟<br>
            已催促：${urgeCount} 次<br>
            最近催促：${RiderApp.escapeHtml(urgeTime || '暂无')}
          </p>

          <div class="rapp-actions">
            <button data-id="${RiderApp.escapeHtml(id)}">催促商家出餐</button>
          </div>
        </article>
      `;
        }).join('');

        box.querySelectorAll('button[data-id]').forEach(btn => {
            btn.addEventListener('click', () => urgeMerchant(btn.dataset.id));
        });
    }

    function computeLevelInfo(finishedCount, avgSpeed) {
        const fatiguePenalty = Math.min(1.8, finishedCount * 0.18);

        let title = '成长骑手';
        let speedBonus = 0;
        let levelBonusText = '暂无等级加成';
        let desc = '当前为成长骑手，系统主要推荐距离近、路线清晰的订单。';

<<<<<<< HEAD
        if (finishedCount >= 10) {
=======
        if (finishedCount >= 15) {
>>>>>>> origin/feature-user-rider-merchant
            title = '单王骑手';
            speedBonus = 1.2;
            levelBonusText = '-1.2 分钟 / 公里';
            desc = '今日已达成单王骑手，拥有最高速度修正和更高等级奖励。';
<<<<<<< HEAD
        } else if (finishedCount >= 5) {
=======
        } else if (finishedCount >= 10) {
>>>>>>> origin/feature-user-rider-merchant
            title = '闪电侠骑手';
            speedBonus = 0.7;
            levelBonusText = '-0.7 分钟 / 公里';
            desc = '今日已达成闪电侠骑手，获得速度修正和基础奖励。';
        }

        const effectiveSpeed = Math.max(3.5, avgSpeed + fatiguePenalty - speedBonus);

        return {
            title,
            desc,
            fatiguePenalty,
            speedBonus,
            levelBonusText,
            effectiveSpeed
        };
    }

    async function finishOrder(orderId) {
        try {
            await RiderApp.request('/rider/orders/finish', {
                method: 'POST',
                body: { orderId }
            });

            RiderApp.toast('配送完成，骑手状态与收入已更新', 'success');
            await initCurrent();
        } catch (e) {
            RiderApp.toast(e.message || '完成配送失败', 'error');
        }
    }

    async function urgeMerchant(orderId) {
        try {
            await RiderApp.request('/rider/orders/urge-merchant', {
                method: 'POST',
                body: { orderId }
            });

            RiderApp.toast('已提醒商家尽快出餐', 'success');
            await initWaitCooking();
        } catch (e) {
            RiderApp.toast(e.message || '催促失败', 'error');
        }
    }

    function sortOrdersByDistance(orders) {
        return orders.slice().sort((a, b) => {
            const distanceA = computeRouteDistance(a);
            const distanceB = computeRouteDistance(b);

            if (distanceA !== distanceB) {
                return distanceA - distanceB;
            }

<<<<<<< HEAD
            const urgedA = Number(RiderApp.getField(a, ['isUrged', 'is_urged'], 0));
            const urgedB = Number(RiderApp.getField(b, ['isUrged', 'is_urged'], 0));
=======
            const urgedA = [2, 3].includes(Number(RiderApp.getField(a, ['status'], 0))) ? Number(RiderApp.getField(a, ['isUrged', 'is_urged'], 0)) : 0;
            const urgedB = [2, 3].includes(Number(RiderApp.getField(b, ['status'], 0))) ? Number(RiderApp.getField(b, ['isUrged', 'is_urged'], 0)) : 0;
>>>>>>> origin/feature-user-rider-merchant

            if (urgedA !== urgedB) {
                return urgedB - urgedA;
            }

            const waitA = Number(RiderApp.getField(a, ['waitMinutes', 'wait_minutes'], 0));
            const waitB = Number(RiderApp.getField(b, ['waitMinutes', 'wait_minutes'], 0));

            return waitB - waitA;
        });
    }

    function computeRouteDistance(order) {
        const rider = state.currentLocation || fallbackLocation;
        const merchant = resolveMerchantPoint(order);
        const user = resolveAddressPoint(RiderApp.getField(order, ['address'], ''));

        const first = distanceKm(rider.latitude, rider.longitude, merchant.latitude, merchant.longitude);
        const second = distanceKm(merchant.latitude, merchant.longitude, user.latitude, user.longitude);

        return first + second;
    }

    function resolveMerchantPoint(order) {
        const merchantName = RiderApp.getField(order, ['merchantName', 'merchant_name'], '');

        if (String(merchantName).includes('汉堡王')) {
            return {
                latitude: 34.665620,
                longitude: 112.364200,
                label: '汉堡王商家附近'
            };
        }

        return {
            latitude: 34.665000,
            longitude: 112.365000,
            label: '默认商家点'
        };
    }

    function resolveAddressPoint(address) {
        const value = String(address || '');

        if (value.includes('西苑') && value.includes('6')) {
            return {
                latitude: 34.667100,
                longitude: 112.362400,
                label: '西苑 6 号宿舍楼'
            };
        }

        if (value.includes('西苑')) {
            return {
                latitude: 34.666600,
                longitude: 112.363300,
                label: '西苑校区'
            };
        }

        if (value.includes('软件学院')) {
            return {
                latitude: 34.663300,
                longitude: 112.367800,
                label: '软件学院'
            };
        }

        if (value.includes('东区') || value.includes('教学楼')) {
            return {
                latitude: 34.661900,
                longitude: 112.371200,
                label: '东区教学楼'
            };
        }

        return {
            latitude: 34.664300,
            longitude: 112.366600,
            label: '默认用户点'
        };
    }

    function distanceKm(lat1, lng1, lat2, lng2) {
        const rad = Math.PI / 180;
        const earth = 6371;

        const dLat = (Number(lat2) - Number(lat1)) * rad;
        const dLng = (Number(lng2) - Number(lng1)) * rad;

        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Number(lat1) * rad) * Math.cos(Number(lat2) * rad)
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        return earth * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    function ensureLocation() {
        return new Promise(resolve => {
            if (!navigator.geolocation) {
                state.currentLocation = fallbackLocation;
                resolve();
                return;
            }

            navigator.geolocation.getCurrentPosition(position => {
                state.currentLocation = {
                    latitude: Number(position.coords.latitude.toFixed(6)),
                    longitude: Number(position.coords.longitude.toFixed(6)),
                    label: '浏览器实时定位',
                    mode: '真实定位'
                };

                resolve();
            }, () => {
                state.currentLocation = fallbackLocation;
                resolve();
            }, {
                enableHighAccuracy: true,
                timeout: 5000,
                maximumAge: 10000
            });
        });
    }
})();