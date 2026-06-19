// static/js/customer.js

let loginUser = null;
let currentCategoryId = "";
let products = [];
let cart = [];

const statusTextMap = {
    0: "待商家接单",
    1: "商家已接单",
    2: "已出餐，等待骑手",
    3: "骑手配送中",
    4: "已完成"
};

document.addEventListener("DOMContentLoaded", function () {
    loginUser = requireLogin();

    if (!loginUser) {
        return;
    }

    initUserInfo();
    initMenuEvents();
    initSearchEvents();
    initOrderEvents();

    loadCategories();
    loadProducts();
});

function initUserInfo() {
    const name = loginUser.realName || loginUser.username;
    document.getElementById("userInfo").innerText = "你好，" + name;
}

function initMenuEvents() {
    const buttons = document.querySelectorAll(".menu-item");

    buttons.forEach(function (btn) {
        btn.addEventListener("click", function () {
            buttons.forEach(function (item) {
                item.classList.remove("active");
            });

            btn.classList.add("active");

            const page = btn.getAttribute("data-page");
            switchPage(page);

            if (page === "cart") {
                renderCart();
            }

            if (page === "orders") {
                loadOrders();
            }
        });
    });
}

function switchPage(page) {
    document.querySelectorAll(".page-section").forEach(function (section) {
        section.classList.remove("active");
    });

    if (page === "products") {
        document.getElementById("productsPage").classList.add("active");
    }

    if (page === "cart") {
        document.getElementById("cartPage").classList.add("active");
    }

    if (page === "orders") {
        document.getElementById("ordersPage").classList.add("active");
    }
}

function initSearchEvents() {
    document.getElementById("searchBtn").addEventListener("click", function () {
        loadProducts();
    });

    document.getElementById("keywordInput").addEventListener("keyup", function (event) {
        if (event.key === "Enter") {
            loadProducts();
        }
    });
}

function initOrderEvents() {
    document.getElementById("submitOrderBtn").addEventListener("click", submitOrder);
    document.getElementById("refreshOrdersBtn").addEventListener("click", loadOrders);
}

function loadCategories() {
    request({
        url: "/api/product/categories",
        method: "GET"
    }).then(function (list) {
        renderCategories(list || []);
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderCategories(list) {
    const box = document.getElementById("categoryTabs");

    let html = '<button class="category-tab active" data-id="">全部</button>';

    list.forEach(function (item) {
        html += `
            <button class="category-tab" data-id="${item.categoryId}">
                ${item.categoryName}
            </button>
        `;
    });

    box.innerHTML = html;

    document.querySelectorAll(".category-tab").forEach(function (tab) {
        tab.addEventListener("click", function () {
            document.querySelectorAll(".category-tab").forEach(function (item) {
                item.classList.remove("active");
            });

            tab.classList.add("active");
            currentCategoryId = tab.getAttribute("data-id");
            loadProducts();
        });
    });
}

function loadProducts() {
    const keyword = document.getElementById("keywordInput").value.trim();

    const query = buildQuery({
        categoryId: currentCategoryId,
        keyword: keyword
    });

    request({
        url: "/api/product/list" + query,
        method: "GET"
    }).then(function (list) {
        products = list || [];
        renderProducts(products);
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderProducts(list) {
    const box = document.getElementById("productList");

    if (!list.length) {
        box.innerHTML = `<div class="empty">暂无商品</div>`;
        return;
    }

    let html = "";

    list.forEach(function (item) {
        const imageUrl = item.imageUrl || item.image_url || "../images/default-food.png";

        html += `
            <div class="product-card">
                <div class="product-image">
                    <img src="${imageUrl}" alt="${item.name}" onerror="this.style.display='none'">
                </div>

                <div class="product-info">
                    <h3>${item.name}</h3>
                    <p>${item.description || "暂无描述"}</p>

                    <div class="product-meta">
                        <strong>${formatMoney(item.price)}</strong>
                        <span>已售 ${item.orderCount || item.order_count || 0}</span>
                    </div>

                    <button class="primary-btn small-btn" onclick="addToCart(${item.productId || item.product_id})">
                        加入点餐车
                    </button>
                </div>
            </div>
        `;
    });

    box.innerHTML = html;
}

function addToCart(productId) {
    const product = products.find(function (item) {
        return Number(item.productId || item.product_id) === Number(productId);
    });

    if (!product) {
        showMessage("商品不存在", "error");
        return;
    }

    const exist = cart.find(function (item) {
        return Number(item.productId) === Number(productId);
    });

    if (exist) {
        exist.quantity += 1;
    } else {
        cart.push({
            productId: product.productId || product.product_id,
            merchantId: product.merchantId || product.merchant_id,
            name: product.name,
            price: Number(product.price),
            quantity: 1
        });
    }

    showMessage("已加入点餐车", "success");
}

function renderCart() {
    const box = document.getElementById("cartList");

    if (!cart.length) {
        box.innerHTML = `<div class="empty">点餐车为空，请先选择商品</div>`;
        document.getElementById("cartTotal").innerText = formatMoney(0);
        return;
    }

    let html = "";

    cart.forEach(function (item) {
        html += `
            <div class="cart-item">
                <div>
                    <h3>${item.name}</h3>
                    <p>${formatMoney(item.price)} × ${item.quantity}</p>
                </div>

                <div class="quantity-box">
                    <button onclick="changeQuantity(${item.productId}, -1)">-</button>
                    <span>${item.quantity}</span>
                    <button onclick="changeQuantity(${item.productId}, 1)">+</button>
                </div>
            </div>
        `;
    });

    box.innerHTML = html;
    document.getElementById("cartTotal").innerText = formatMoney(calcCartTotal());
}

function changeQuantity(productId, delta) {
    const item = cart.find(function (cartItem) {
        return Number(cartItem.productId) === Number(productId);
    });

    if (!item) {
        return;
    }

    item.quantity += delta;

    if (item.quantity <= 0) {
        cart = cart.filter(function (cartItem) {
            return Number(cartItem.productId) !== Number(productId);
        });
    }

    renderCart();
}

function calcCartTotal() {
    return cart.reduce(function (sum, item) {
        return sum + item.price * item.quantity;
    }, 0);
}

function submitOrder() {
    if (!cart.length) {
        showMessage("点餐车为空，不能提交订单", "error");
        return;
    }

    const address = document.getElementById("addressInput").value.trim();
    const remark = document.getElementById("remarkInput").value.trim();

    if (!address) {
        showMessage("请输入收货地址", "error");
        return;
    }

    const merchantId = cart[0].merchantId;

    const hasDifferentMerchant = cart.some(function (item) {
        return Number(item.merchantId) !== Number(merchantId);
    });

    if (hasDifferentMerchant) {
        showMessage("当前版本暂不支持跨商家下单", "error");
        return;
    }

    const payload = {
        userId: loginUser.userId,
        merchantId: merchantId,
        address: address,
        remark: remark,
        items: cart.map(function (item) {
            return {
                productId: item.productId,
                quantity: item.quantity
            };
        })
    };

    request({
        url: "/api/customer/orders",
        method: "POST",
        data: payload
    }).then(function () {
        showMessage("订单提交成功", "success");

        cart = [];
        document.getElementById("addressInput").value = "";
        document.getElementById("remarkInput").value = "";

        switchPage("orders");
        document.querySelectorAll(".menu-item").forEach(function (item) {
            item.classList.remove("active");
        });
        document.querySelector('[data-page="orders"]').classList.add("active");

        loadOrders();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function loadOrders() {
    request({
        url: "/api/customer/orders" + buildQuery({ userId: loginUser.userId }),
        method: "GET"
    }).then(function (list) {
        renderOrders(list || []);
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderOrders(list) {
    const box = document.getElementById("orderList");

    if (!list.length) {
        box.innerHTML = `<div class="empty">暂无订单</div>`;
        return;
    }

    let html = "";

    list.forEach(function (order) {
        const status = Number(order.status);
        const statusText = statusTextMap[status] || "未知状态";

        html += `
            <div class="order-card">
                <div class="order-head">
                    <div>
                        <h3>订单 #${order.orderId || order.order_id}</h3>
                        <p>${order.orderTime || order.order_time || ""}</p>
                    </div>
                    <span class="status-badge">${statusText}</span>
                </div>

                <div class="status-line">
                    ${renderStatusSteps(status)}
                </div>

                <div class="order-detail">
                    <p>订单金额：<strong>${formatMoney(order.totalPrice || order.total_price)}</strong></p>
                    <p>收货地址：${order.address || "-"}</p>
                    <p>订单备注：${order.remark || "无"}</p>
                    <p>骑手信息：${order.riderName || order.rider_name || "暂无"} ${order.riderPhone || order.rider_phone || ""}</p>
                </div>

                <div class="order-actions">
                    ${status < 4 ? `<button class="outline-btn" onclick="urgeOrder(${order.orderId || order.order_id})">催单</button>` : ""}
                </div>
            </div>
        `;
    });

    box.innerHTML = html;
}

function renderStatusSteps(status) {
    const steps = [
        "待接单",
        "已接单",
        "已出餐",
        "配送中",
        "已完成"
    ];

    let html = "";

    steps.forEach(function (text, index) {
        const activeClass = index <= status ? "active" : "";

        html += `
            <div class="step ${activeClass}">
                <span>${index}</span>
                <p>${text}</p>
            </div>
        `;
    });

    return html;
}

function urgeOrder(orderId) {
    request({
        url: "/api/customer/orders/" + orderId + "/urge",
        method: "POST"
    }).then(function () {
        showMessage("催单成功", "success");
        loadOrders();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}