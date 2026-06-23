(function () {
  const LEGACY_CART_KEY = 'takeout_user_cart_v1';

  function getUserCartKey() {
    if (!App.isLoggedIn()) return '';
    const user = App.getLocalUser();
    const uid = App.getField(user, ['userId', 'user_id', 'id', 'username'], '');
    return uid ? `${LEGACY_CART_KEY}_${uid}` : '';
  }

  function readJson(key) {
    try { return JSON.parse(localStorage.getItem(key) || '[]'); } catch (e) { return []; }
  }

  function writeJson(key, value) {
    localStorage.setItem(key, JSON.stringify(value || []));
  }

  function readCart() {
    const key = getUserCartKey();
    if (!key) return [];

    let cart = readJson(key);
    const legacyCart = readJson(LEGACY_CART_KEY);

    if (!cart.length && legacyCart.length) {
      cart = legacyCart;
      writeJson(key, cart);
      localStorage.removeItem(LEGACY_CART_KEY);
    }

    return cart;
  }

  function writeCart(cart) {
    const key = getUserCartKey();
    if (!key) {
      window.dispatchEvent(new Event('cart:change'));
      return;
    }
    writeJson(key, cart || []);
    localStorage.removeItem(LEGACY_CART_KEY);
    window.dispatchEvent(new Event('cart:change'));
  }

  function getProductId(product) { return Number(App.getField(product, ['productId', 'product_id', 'id'], 0)); }
  function getMerchantId(product) { return Number(App.getField(product, ['merchantId', 'merchant_id'], 0)); }

  function add(product, count) {
    if (!App.requireLogin('请先登录，登录后才能加入购物车。', { redirect: true, auto: false, closable: true })) return readCart();

    const cart = readCart();
    const productId = getProductId(product);
    if (!productId) throw new Error('商品ID缺失');
    const merchantId = getMerchantId(product);
    const hasOtherMerchant = cart.length && merchantId && cart.some(item => Number(item.merchantId) !== merchantId);
    if (hasOtherMerchant) {
      if (!confirm('购物车已有其他商家的商品，是否清空后重新加入？')) return cart;
      cart.splice(0, cart.length);
    }
    const exist = cart.find(item => Number(item.productId) === productId);
    if (exist) {
      exist.quantity += count || 1;
    } else {
      cart.push({
        productId,
        merchantId,
        name: App.getField(product, ['name', 'productName'], '未命名商品'),
        price: Number(App.getField(product, ['price'], 0)),
        imageUrl: App.getField(product, ['imageUrl', 'image_url'], ''),
        quantity: count || 1
      });
    }
    writeCart(cart);
    return cart;
  }

  function change(productId, delta) {
    const cart = readCart();
    const item = cart.find(x => Number(x.productId) === Number(productId));
    if (!item) return cart;
    item.quantity += delta;
    const next = cart.filter(x => x.quantity > 0);
    writeCart(next);
    return next;
  }

  function clear() {
    const key = getUserCartKey();
    if (key) localStorage.removeItem(key);
    localStorage.removeItem(LEGACY_CART_KEY);
    window.dispatchEvent(new Event('cart:change'));
  }

  function count() { return readCart().reduce((sum, item) => sum + Number(item.quantity || 0), 0); }
  function goodsAmount() { return readCart().reduce((sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0), 0); }

  window.Cart = { readCart, writeCart, add, change, clear, count, goodsAmount };
})();
