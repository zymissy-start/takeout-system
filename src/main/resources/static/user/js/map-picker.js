(function () {
  let map = null;
  let marker = null;

  document.addEventListener('DOMContentLoaded', init);

  function init() {
    bindEvents();
    initFromQuery();
    loadAmap();
  }

  function bindEvents() {
    $('#closeBtn').addEventListener('click', () => window.close());
    $('#confirmBtn').addEventListener('click', confirmPick);
    $('#browserLocateBtn').addEventListener('click', browserLocate);
    $('#latInput').addEventListener('change', applyManualPoint);
    $('#lngInput').addEventListener('change', applyManualPoint);
  }

  function initFromQuery() {
    const params = new URLSearchParams(location.search);
    const lat = params.get('lat') || '';
    const lng = params.get('lng') || '';
    if (isValidLatLng(lat, lng)) {
      $('#latInput').value = lat;
      $('#lngInput').value = lng;
      $('#addressText').textContent = `已带入原坐标：${lat}, ${lng}`;
    }
  }

  function loadAmap() {
    const key = window.USER_MAP_CONFIG && window.USER_MAP_CONFIG.amapKey;
    if (!key) {
      setStatus('未配置高德地图 Key。你仍然可以手动输入经纬度，或使用浏览器定位。', 'warn');
      return;
    }

    if (window.AMap) {
      initAmap();
      return;
    }

    const script = document.createElement('script');
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${encodeURIComponent(key)}&plugin=AMap.Geolocation`;
    script.async = true;
    script.onload = initAmap;
    script.onerror = () => setStatus('地图加载失败。请检查网络、高德 Key 或 referer 白名单；也可以手动输入经纬度。', 'error');
    document.head.appendChild(script);
  }

  function initAmap() {
    try {
      const lat = $('#latInput').value;
      const lng = $('#lngInput').value;
      const hasCoord = isValidLatLng(lat, lng);
      const center = hasCoord ? [Number(lng), Number(lat)] : [116.397428, 39.90923];

      map = new AMap.Map('mapContainer', {
        zoom: hasCoord ? 16 : 12,
        center
      });

      map.on('click', event => {
        const clickLat = Number(event.lnglat.lat);
        const clickLng = Number(event.lnglat.lng);
        $('#latInput').value = clickLat.toFixed(6);
        $('#lngInput').value = clickLng.toFixed(6);
        setMarker(clickLng, clickLat);
      });

      if (hasCoord) {
        setMarker(Number(lng), Number(lat));
      }

      setStatus('地图已加载：点击地图即可选点。', 'success');
    } catch (e) {
      setStatus('地图初始化失败，请手动输入经纬度或使用浏览器定位。', 'error');
    }
  }

  function browserLocate() {
    if (!navigator.geolocation) return toast('浏览器不支持定位，请手动输入经纬度');
    setStatus('正在获取浏览器定位，请允许定位权限...', 'info');
    navigator.geolocation.getCurrentPosition(position => {
      const lat = Number(position.coords.latitude);
      const lng = Number(position.coords.longitude);
      $('#latInput').value = lat.toFixed(6);
      $('#lngInput').value = lng.toFixed(6);
      if (map) map.setZoomAndCenter(17, [lng, lat]);
      setMarker(lng, lat);
      setStatus('浏览器定位成功。', 'success');
    }, () => {
      setStatus('定位失败：请允许浏览器定位权限，或手动输入经纬度。', 'error');
    }, { enableHighAccuracy: true, timeout: 10000, maximumAge: 30000 });
  }

  function applyManualPoint() {
    const lat = $('#latInput').value.trim();
    const lng = $('#lngInput').value.trim();
    if (!lat && !lng) return;
    if (!isValidLatLng(lat, lng)) return toast('经纬度格式不正确');
    if (map) map.setZoomAndCenter(17, [Number(lng), Number(lat)]);
    setMarker(Number(lng), Number(lat));
  }

  function setMarker(lng, lat) {
    if (!window.AMap || !map) return;
    if (!marker) {
      marker = new AMap.Marker({ position: [lng, lat] });
      map.add(marker);
    } else {
      marker.setPosition([lng, lat]);
    }
  }

  function confirmPick() {
    const lat = $('#latInput').value.trim();
    const lng = $('#lngInput').value.trim();
    if (!isValidLatLng(lat, lng)) return toast('请先选择地图位置，或正确输入经纬度');

    const payload = {
      latitude: Number(lat),
      longitude: Number(lng),
      addressDetail: $('#detailInput').value.trim() || '地图选点位置'
    };

    localStorage.setItem('pickedLocation', JSON.stringify(payload));
    if (window.opener && !window.opener.closed) {
      window.opener.postMessage({ type: 'USER_MAP_PICKED', payload }, location.origin);
    }
    toast('已选择位置，正在返回地址表单');
    setTimeout(() => window.close(), 450);
  }

  function isValidLatLng(lat, lng) {
    const la = Number(lat);
    const lo = Number(lng);
    return Number.isFinite(la) && Number.isFinite(lo) && la >= -90 && la <= 90 && lo >= -180 && lo <= 180;
  }

  function setStatus(text, type) {
    const el = $('#mapStatus');
    el.textContent = text;
    el.dataset.type = type || 'info';
  }

  function toast(message) {
    if (window.App && App.toast) return App.toast(message);
    alert(message);
  }

  function $(selector) {
    return document.querySelector(selector);
  }
})();
