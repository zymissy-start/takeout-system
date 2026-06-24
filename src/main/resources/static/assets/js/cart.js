(function () {
  const CART_KEY = 'takeout_user_cart_v1';

  function readCart() {
    try { return JSON.parse(localStorage.getItem(CART_KEY) || '[]'); } catch (e) { return []; }
  }
  function writeCart(cart) {
    localStorage.setItem(CART_KEY, JSON.stringify(cart || []));
    window.dispatchEvent(new Event('cart:change'));
  }
  function getProductId(product) { return Number(App.getField(product, ['productId', 'product_id', 'id'], 0)); }
  function getMerchantId(product) { return Number(App.getField(product, ['merchantId', 'merchant_id'], 0)); }

  function add(product, count) {
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
        deliveryFee: Number(App.getField(product, ['deliveryFee', 'delivery_fee'], 3)),
        minOrderAmount: Number(App.getField(product, ['minOrderAmount', 'min_order_amount'], 0)),
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

  function clear() { writeCart([]); }
  function count() { return readCart().reduce((sum, item) => sum + Number(item.quantity || 0), 0); }
  function goodsAmount() { return readCart().reduce((sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0), 0); }

  window.Cart = { readCart, writeCart, add, change, clear, count, goodsAmount };
})();
