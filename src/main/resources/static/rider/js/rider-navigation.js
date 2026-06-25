(function () {
    const $ = (id) => document.getElementById(id);

    const state = {
        order: null,
        map: null,
        AMap: null,
        lines: [],
        markers: [],
        routes: [],
        selectedIndex: 0
    };

    document.addEventListener('DOMContentLoaded', function () {
        $('refreshBtn').addEventListener('click', loadNavigationOrder);
        $('openAppBtn').addEventListener('click', openAmapApp);
        loadNavigationOrder();
    });

    async function loadNavigationOrder() {
        setStatus('正在读取当前骑手订单...');
        clearRoutes();

        try {
            const params = new URLSearchParams(location.search);
            const orderId = params.get('orderId');
            const riderId = params.get('riderId') || localStorage.getItem('riderId') || '';

            const query = new URLSearchParams();
            if (orderId) query.set('orderId', orderId);
            if (riderId) query.set('riderId', riderId);

            const res = await fetch('/api/rider/navigation/active?' + query.toString(), {
                headers: buildHeaders()
            });

            const body = await safeJson(res);

            console.log('骑手导航接口返回：', body);

            const success =
                res.ok &&
                (
                    body.code === 200 ||
                    body.code === 0 ||
                    body.success === true ||
                    body.status === 200
                );

            if (!success) {
                throw new Error(body.message || body.msg || '读取导航订单失败');
            }

            state.order = body.data || body.result || body;

            if (!state.order || !state.order.orderId) {
                throw new Error('导航接口没有返回有效订单数据');
            }

            renderOrder(state.order);
            await initMapAndPlan(state.order);
        } catch (err) {
            console.error(err);
            showError(err.message || '导航功能加载失败');
        }
    }

    async function safeJson(res) {
        const text = await res.text();

        if (!text) {
            return {};
        }

        try {
            return JSON.parse(text);
        } catch (e) {
            return {
                code: res.status,
                success: false,
                message: text
            };
        }
    }

    function buildHeaders() {
        const headers = {
            'Accept': 'application/json'
        };

        const token = localStorage.getItem('token');
        if (token) {
            headers.Authorization = 'Bearer ' + token;
        }

        const riderUser = readJson(localStorage.getItem('takeout_rider_user_v1'))
            || readJson(localStorage.getItem('takeout_rider_user'))
            || readJson(localStorage.getItem('currentUser'));

        if (riderUser && (riderUser.userId || riderUser.id || riderUser.riderId)) {
            headers['X-User-Id'] = riderUser.userId || riderUser.id || riderUser.riderId;
        }

        const riderId = localStorage.getItem('riderId');
        if (riderId) {
            headers['X-User-Id'] = riderId;
        }

        return headers;
    }

    function readJson(text) {
        try {
            return JSON.parse(text || '{}');
        } catch (e) {
            return null;
        }
    }

    function renderOrder(order) {
        $('orderTitle').textContent = (order.orderNo || ('订单 #' + order.orderId)) + ' · 开始导航';
        $('storeName').textContent = order.storeName || '商家门店';
        $('storeAddress').textContent = order.storeAddress || '商家位置';
        $('receiverName').textContent = order.receiverName ? ('用户：' + order.receiverName) : '用户收货点';
        $('receiverAddress').textContent = order.receiverAddress || '用户收货地址';

        setStatus('起点为商家位置，终点为用户收货地址，正在规划三条候选路线');
    }

    async function initMapAndPlan(order) {
        const cfg = window.RIDER_MAP_CONFIG || {};

        if (!cfg.amapKey || cfg.amapKey === '这里填写骑手端高德Web端JS API Key') {
            throw new Error('请先在 /rider/js/rider-map-config.js 中配置骑手端高德 Web JS API Key');
        }

        const AMap = await loadAMap(cfg);
        state.AMap = AMap;

        const start = toLngLat(order.startLongitude, order.startLatitude);
        const end = toLngLat(order.endLongitude, order.endLatitude);

        if (!start || !end) {
            throw new Error('商家或用户经纬度为空，无法规划路线');
        }

        if (!state.map) {
            state.map = new AMap.Map('navMap', {
                zoom: 14,
                resizeEnable: true,
                center: [start[0], start[1]]
            });

            state.map.addControl(new AMap.Scale());
            state.map.addControl(new AMap.ToolBar({
                position: 'RB'
            }));
        }

        drawMarkers(start, end);
        await planThreeRoutes(start, end);
    }

    function loadAMap(cfg) {
        if (window.AMap) {
            return Promise.resolve(window.AMap);
        }

        if (cfg.amapSecurityCode) {
            window._AMapSecurityConfig = {
                securityJsCode: cfg.amapSecurityCode
            };
        }

        return new Promise((resolve, reject) => {
            const script = document.createElement('script');

            script.src =
                'https://webapi.amap.com/maps?v=2.0'
                + '&key=' + encodeURIComponent(cfg.amapKey)
                + '&plugin=AMap.Driving,AMap.Scale,AMap.ToolBar';

            script.onload = () => resolve(window.AMap);
            script.onerror = () => reject(new Error('高德地图脚本加载失败，请检查网络和 Key'));

            document.head.appendChild(script);
        });
    }

    function toLngLat(lng, lat) {
        const x = Number(lng);
        const y = Number(lat);

        if (!Number.isFinite(x) || !Number.isFinite(y)) {
            return null;
        }

        return [x, y];
    }

    function drawMarkers(start, end) {
        const AMap = state.AMap;

        state.markers.forEach(m => state.map.remove(m));

        state.markers = [
            new AMap.Marker({
                position: start,
                title: '商家取餐点',
                label: {
                    content: '商',
                    direction: 'top'
                }
            }),
            new AMap.Marker({
                position: end,
                title: '用户收货点',
                label: {
                    content: '客',
                    direction: 'top'
                }
            })
        ];

        state.map.add(state.markers);
        state.map.setFitView(state.markers, true, [80, 80, 80, 80]);
    }

    async function planThreeRoutes(start, end) {
        const AMap = state.AMap;

        setStatus('正在向高德地图请求最短距离、最快时间、实时路况路线...');
        clearRoutes();

        const policies = [
            {
                name: '最短距离',
                policy: AMap.DrivingPolicy.LEAST_DISTANCE,
                desc: '优先选择距离更短的路径'
            },
            {
                name: '最快时间',
                policy: AMap.DrivingPolicy.LEAST_TIME,
                desc: '优先选择预计耗时更短的路径'
            },
            {
                name: '躲避拥堵',
                policy: AMap.DrivingPolicy.REAL_TRAFFIC,
                desc: '结合实时路况避开拥堵'
            }
        ];

        const results = await Promise.all(
            policies.map(p => searchRoute(p, start, end))
        );

        const unique = dedupeRoutes(results.filter(Boolean));

        unique.sort((a, b) => {
            return a.distance - b.distance || a.duration - b.duration;
        });

        state.routes = unique.slice(0, 3);

        if (!state.routes.length) {
            $('routeCards').innerHTML =
                '<div class="empty">高德暂未返回可用路线，请检查坐标是否在可导航区域内。</div>';
            setStatus('未规划到可用路线');
            return;
        }

        state.selectedIndex = 0;
        renderRouteCards();
        drawRouteLines();

        setStatus('已按最短距离推荐 ' + state.routes.length + ' 条路线，默认选中第 1 条');
    }

    function searchRoute(policy, start, end) {
        const AMap = state.AMap;

        return new Promise((resolve) => {
            const driving = new AMap.Driving({
                policy: policy.policy,
                extensions: 'all'
            });

            driving.search(start, end, function (status, result) {
                if (status === 'complete' && result && result.routes && result.routes.length) {
                    const route = result.routes[0];

                    resolve({
                        name: policy.name,
                        desc: policy.desc,
                        distance: Number(route.distance || 0),
                        duration: Number(route.time || 0),
                        route: route
                    });
                } else {
                    resolve(null);
                }
            });
        });
    }

    function dedupeRoutes(routes) {
        const map = new Map();

        routes.forEach(r => {
            const key = Math.round(r.distance / 10) + '-' + Math.round(r.duration / 30);

            if (!map.has(key)) {
                map.set(key, r);
            }
        });

        return Array.from(map.values());
    }

    function renderRouteCards() {
        $('routeCards').innerHTML = state.routes.map((r, i) => `
            <div class="route-item ${i === state.selectedIndex ? 'active' : ''}" data-index="${i}">
                <div class="route-rank">${i + 1}</div>
                <div>
                    <div class="route-name">${r.name}</div>
                    <div class="route-meta">${r.desc} · 预计 ${formatMinutes(r.duration)}</div>
                </div>
                <div class="route-distance">${formatDistance(r.distance)}</div>
            </div>
        `).join('');

        document.querySelectorAll('.route-item').forEach(item => {
            item.addEventListener('click', function () {
                state.selectedIndex = Number(this.dataset.index);
                renderRouteCards();
                drawRouteLines();
            });
        });
    }

    function drawRouteLines() {
        const AMap = state.AMap;

        state.lines.forEach(line => state.map.remove(line));
        state.lines = [];

        state.routes.forEach((r, i) => {
            const path = [];

            (r.route.steps || []).forEach(step => {
                (step.path || []).forEach(point => {
                    path.push(point);
                });
            });

            if (!path.length) {
                return;
            }

            const active = i === state.selectedIndex;

            const line = new AMap.Polyline({
                path: path,
                strokeColor: active ? '#08c3d1' : '#9aa8b8',
                strokeWeight: active ? 8 : 5,
                strokeOpacity: active ? 0.95 : 0.38,
                lineJoin: 'round',
                zIndex: active ? 20 : 10
            });

            state.lines.push(line);
        });

        state.map.add(state.lines);
        state.map.setFitView([...state.markers, ...state.lines], true, [80, 80, 80, 80]);
    }

    function clearRoutes() {
        if (state.map && state.lines.length) {
            state.lines.forEach(line => state.map.remove(line));
        }

        state.lines = [];
        state.routes = [];
        $('routeCards').innerHTML = '<div class="empty">路线规划中...</div>';
    }

    function openAmapApp() {
        const order = state.order;

        if (!order) {
            return;
        }

        const start = toLngLat(order.startLongitude, order.startLatitude);
        const end = toLngLat(order.endLongitude, order.endLatitude);

        if (!start || !end) {
            return;
        }

        const url =
            'https://uri.amap.com/navigation?from='
            + start[0] + ',' + start[1] + ',商家取餐点'
            + '&to='
            + end[0] + ',' + end[1] + ',用户收货点'
            + '&mode=car'
            + '&policy=1'
            + '&coordinate=gaode'
            + '&callnative=1'
            + '&src=takeout-rider';

        window.open(url, '_blank');
    }

    function setStatus(text) {
        $('statusText').textContent = text;
    }

    function showError(message) {
        $('orderTitle').textContent = '导航加载失败';
        setStatus(message);

        $('routeCards').innerHTML =
            '<div class="error-tip">'
            + escapeHtml(message)
            + '<br>请检查：订单是否已分配骑手、商家坐标、用户收货坐标、高德 Key。'
            + '</div>';
    }

    function formatDistance(meter) {
        if (!meter) {
            return '--';
        }

        return meter >= 1000
            ? (meter / 1000).toFixed(2) + ' km'
            : Math.round(meter) + ' m';
    }

    function formatMinutes(seconds) {
        if (!seconds) {
            return '--';
        }

        return Math.max(1, Math.round(seconds / 60)) + ' 分钟';
    }

    function escapeHtml(text) {
        return String(text || '').replace(/[&<>"]/g, s => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;'
        }[s]));
    }
})();