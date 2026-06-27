(function () {
  let map = null;
  let marker = null;
  let geocoder = null;
  let placeSearch = null;

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
    $('#searchBtn').addEventListener('click', searchPlace);
    $('#keywordInput').addEventListener('keydown', event => {
      if (event.key === 'Enter') searchPlace();
    });
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
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${encodeURIComponent(key)}&plugin=AMap.Geocoder,AMap.PlaceSearch,AMap.Geolocation`;
    script.async = true;
    script.onload = initAmap;
    script.onerror = () => setStatus('地图加载失败。请检查网络、高德 Key 或 referer 白名单；也可以手动输入经纬度。', 'error');
    document.head.appendChild(script);
  }

  function initAmap() {
    try {
      const lat = $('#latInput').value;
      const lng = $('#lngInput').value;
      const center = isValidLatLng(lat, lng) ? [Number(lng), Number(lat)] : [116.397428, 39.90923];

      map = new AMap.Map('mapContainer', {
        zoom: isValidLatLng(lat, lng) ? 16 : 12,
        center
      });

      geocoder = new AMap.Geocoder({ city: '全国' });
      placeSearch = new AMap.PlaceSearch({ city: '全国', pageSize: 10, pageIndex: 1 });

      map.on('click', event => {
        setPicked(Number(event.lnglat.lat), Number(event.lnglat.lng), '正在解析地址...');
        reverseGeocode(Number(event.lnglat.lng), Number(event.lnglat.lat));
      });

      if (isValidLatLng(lat, lng)) {
        setMarker(Number(lng), Number(lat));
        reverseGeocode(Number(lng), Number(lat));
      }

      setStatus('地图已加载：点击地图即可选点。', 'success');
    } catch (e) {
      setStatus('地图初始化失败，请手动输入经纬度或使用浏览器定位。', 'error');
    }
  }

  function searchPlace() {
    const keyword = $('#keywordInput').value.trim();
    if (!keyword) return toast('请输入搜索关键词');
    if (!placeSearch) return toast('地图搜索不可用，请检查地图 Key 是否配置正确');

    setStatus('正在搜索地点...', 'info');
    placeSearch.search(keyword, (status, result) => {
      if (status !== 'complete' || !result.poiList || !result.poiList.pois || !result.poiList.pois.length) {
        setStatus('没有找到地点，请换个关键词，或手动输入经纬度。', 'warn');
        return;
      }
      const poi = result.poiList.pois[0];
      const lng = Number(poi.location.lng);
      const lat = Number(poi.location.lat);
      const address = [poi.name, poi.address].filter(Boolean).join('，');
      setPicked(lat, lng, address || keyword);
      if (map) map.setZoomAndCenter(17, [lng, lat]);
      setMarker(lng, lat);
      setStatus(`已选中搜索结果：${address || keyword}`, 'success');
    });
  }

  function browserLocate() {
    if (!navigator.geolocation) return toast('浏览器不支持定位，请手动输入经纬度');
    setStatus('正在获取浏览器定位，请允许定位权限...', 'info');
    navigator.geolocation.getCurrentPosition(position => {
      const lat = Number(position.coords.latitude);
      const lng = Number(position.coords.longitude);
      setPicked(lat, lng, '浏览器定位位置');
      if (map) map.setZoomAndCenter(17, [lng, lat]);
      setMarker(lng, lat);
      reverseGeocode(lng, lat);
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
    setPicked(Number(lat), Number(lng), $('#detailInput').value.trim() || '手动输入位置');
    if (map) map.setZoomAndCenter(17, [Number(lng), Number(lat)]);
    setMarker(Number(lng), Number(lat));
    reverseGeocode(Number(lng), Number(lat));
  }

  function setPicked(lat, lng, address) {
    $('#latInput').value = Number(lat).toFixed(6);
    $('#lngInput').value = Number(lng).toFixed(6);
    if (address) {
      $('#addressText').textContent = address;
      if (!$('#detailInput').value.trim() || $('#detailInput').dataset.auto === '1') {
        $('#detailInput').value = address;
        $('#detailInput').dataset.auto = '1';
      }
    }
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

  function reverseGeocode(lng, lat) {
    if (!geocoder) return;
    geocoder.getAddress([lng, lat], (status, result) => {
      if (status === 'complete' && result.regeocode) {
        const address = result.regeocode.formattedAddress || '已选择地图位置';
        setPicked(lat, lng, address);
      }
    });
  }

  function confirmPick() {
    const lat = $('#latInput').value.trim();
    const lng = $('#lngInput').value.trim();
    if (!isValidLatLng(lat, lng)) return toast('请先选择地图位置，或正确输入经纬度');

    const payload = {
      latitude: Number(lat),
      longitude: Number(lng),
      addressDetail: $('#detailInput').value.trim() || $('#addressText').textContent || '地图选点位置'
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
