(function () {
    const state = {
        rider: null,
        statistics: {}
    };

    document.addEventListener('DOMContentLoaded', init);

    async function init() {
        bindEvents();

        state.rider = await RiderApp.requireRiderLogin();

        renderRiderInfo();
        await loadStatistics();
        loadUnreadCount();
        setInterval(loadUnreadCount, 30000);
    }

    function bindEvents() {
        const logoutBtn = RiderApp.$('#logoutBtn');

        if (logoutBtn) {
            logoutBtn.addEventListener('click', logout);
        }
    }

    function renderRiderInfo() {
        const name = RiderApp.getField(state.rider, ['realName', 'real_name', 'username'], '骑手');
        const username = RiderApp.getField(state.rider, ['username'], '');
        const phone = RiderApp.getField(state.rider, ['phone'], '未绑定手机号');

        RiderApp.$('#riderAvatar').textContent = String(name).slice(0, 1);
        RiderApp.$('#riderName').textContent = name;
        RiderApp.$('#riderSub').textContent = `账号：${username} · 电话：${phone}`;
    }

    async function loadStatistics() {
        try {
            state.statistics = await RiderApp.request('/rider/dashboard/statistics');
        } catch (e) {
            RiderApp.toast(e.message || '统计数据加载失败', 'error');
            state.statistics = {};
        }

        renderStatistics();
    }

    function renderStatistics() {
        const stats = state.statistics;

        const finishedCount = Number(RiderApp.getField(stats, ['todayFinishedCount'], 0));
        const deliveringCount = Number(RiderApp.getField(stats, ['deliveringCount'], 0));
        const availableCount = Number(RiderApp.getField(stats, ['availableOrderCount'], 0));
        const estimatedIncome = RiderApp.getField(stats, ['estimatedIncome'], 0);
        const avgSpeed = Number(RiderApp.getField(stats, ['avgSpeed'], 10.5));
        const riderStatus = Number(RiderApp.getField(stats, ['riderStatus'], 0));

        const levelInfo = computeLevelInfo(finishedCount, avgSpeed);

        RiderApp.$('#todayFinishedCount').textContent = finishedCount;
        RiderApp.$('#deliveringCount').textContent = deliveringCount;
        RiderApp.$('#availableOrderCount').textContent = availableCount;
        RiderApp.$('#estimatedIncome').textContent = RiderApp.formatMoney(estimatedIncome);

        RiderApp.$('#riderLevelBadge').textContent = levelInfo.title;
        RiderApp.$('#effectiveSpeed').textContent = `${levelInfo.effectiveSpeed.toFixed(1)} 分/公里`;
        RiderApp.$('#portalTitle').textContent = `${levelInfo.title} · 配送中心`;
        RiderApp.$('#portalDesc').textContent = levelInfo.desc;

        renderStatus(riderStatus, deliveringCount, availableCount, levelInfo);
    }

    function computeLevelInfo(finishedCount, avgSpeed) {
        const fatiguePenalty = Math.min(1.8, finishedCount * 0.18);

        let title = '成长骑手';
        let speedBonus = 0;
        let desc = '成长阶段系统会优先推荐距离更近、路线更清晰的订单。';

        if (finishedCount >= 15) {
            title = '单王骑手';
            speedBonus = 1.2;
            desc = '今日已达到单王等级，拥有最高速度修正和更高等级奖励。';
        } else if (finishedCount >= 10) {
            title = '闪电侠骑手';
            speedBonus = 0.7;
            desc = '今日已达到闪电侠等级，适合承接距离近、等待时间较长的订单。';
        }

        const effectiveSpeed = Math.max(3.5, avgSpeed + fatiguePenalty - speedBonus);

        return {
            title,
            desc,
            fatiguePenalty,
            speedBonus,
            effectiveSpeed
        };
    }

    function renderStatus(riderStatus, deliveringCount, availableCount, levelInfo) {
        const icon = RiderApp.$('#statusIcon');
        const title = RiderApp.$('#statusTitle');
        const desc = RiderApp.$('#statusDesc');

        if (!icon || !title || !desc) return;

        if (riderStatus === 1 || deliveringCount > 0) {
            icon.textContent = '🛵';
            title.textContent = '配送进行中';
            desc.textContent = `当前有 ${deliveringCount} 个配送任务，实际速度约 ${levelInfo.effectiveSpeed.toFixed(1)} 分钟 / 公里。`;
            return;
        }

        if (availableCount > 0) {
            icon.textContent = '⚡';
            title.textContent = '可接单状态';
            desc.textContent = `当前有 ${availableCount} 个可接订单，建议进入“智能推荐”选择最近订单。`;
            return;
        }

        icon.textContent = '🟢';
        title.textContent = '空闲待命';
        desc.textContent = '当前暂无可接订单，可等待商家出餐或进入催促出餐模块查看制作中订单。';
    }

    async function loadUnreadCount() {
        try {
            const data = await RiderApp.request('/rider/contact/unread-count');
            const count = Number(RiderApp.getField(data, ['unreadCount'], 0));
            renderBadge(count);
        } catch (e) {
            // 静默失败，不影响主流程
        }
    }

    function renderBadge(count) {
        const gridBadge = RiderApp.$('#gridMsgBadge');
        const navBadge = RiderApp.$('#navMsgBadge');
        const banner = RiderApp.$('#unreadBanner');
        const bannerText = RiderApp.$('#unreadBannerText');

        if (count > 0) {
            const label = count > 99 ? '99+' : String(count);
            if (gridBadge) {
                gridBadge.textContent = label;
                gridBadge.classList.add('show');
            }
            if (navBadge) {
                navBadge.textContent = label;
                navBadge.classList.add('show');
            }
            if (banner) {
                banner.style.display = '';
                if (bannerText) {
                    bannerText.textContent = `您有 ${count} 条未读消息，点击查看详情`;
                }
            }
        } else {
            if (gridBadge) {
                gridBadge.textContent = '';
                gridBadge.classList.remove('show');
            }
            if (navBadge) {
                navBadge.textContent = '';
                navBadge.classList.remove('show');
            }
            if (banner) {
                banner.style.display = 'none';
            }
        }
    }

    async function logout() {
        try {
            await RiderApp.request('/rider/logout', {
                method: 'POST'
            });
        } catch (e) {
            // 即使后端退出失败，也清理本地状态。
        }

        RiderApp.clearRider();
        location.href = '/rider/login.html';
    }
})();