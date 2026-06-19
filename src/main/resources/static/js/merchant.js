// static/js/merchant.js

let merchantUser = null;
let merchantProducts = [];
let categories = [];

const orderStatusText = {
    0: "待商家接单",
    1: "商家已接单",
    2: "已出餐，等待骑手",
    3: "骑手配送中",
    4: "已完成"
};

document.addEventListener("DOMContentLoaded", function () {
    merchantUser = requireLogin();

    if (!merchantUser) {
        return;
    }

    if (Number(merchantUser.roleType) !== 2) {
        showMessage("当前账号不是商家账号", "error");
        setTimeout(function () {
            logout();
        }, 1200);
        return;
    }

    document.getElementById("merchantUserInfo").innerText = "商家：" + (merchantUser.realName || merchantUser.username);

    initMenu();
    initEvents();

    loadDashboard();
    loadCategories();
    loadProducts();
    loadOrders();
});

function initMenu() {
    document.querySelectorAll(".manage-menu button").forEach(function (btn) {
        btn.addEventListener("click", function () {
            document.querySelectorAll(".manage-menu button").forEach(function (item) {
                item.classList.remove("active");
            });

            btn.classList.add("active");

            const page = btn.getAttribute("data-page");
            switchPage(page);

            if (page === "dashboard") {
                loadDashboard();
            }

            if (page === "products") {
                loadProducts();
            }

            if (page === "orders") {
                loadOrders();
            }
        });
    });
}

function switchPage(page) {
    document.querySelectorAll(".manage-section").forEach(function (section) {
        section.classList.remove("active");
    });

    document.getElementById(page + "Page").classList.add("active");
}

function initEvents() {
    document.getElementById("refreshDashboardBtn").addEventListener("click", loadDashboard);
    document.getElementById("saveProductBtn").addEventListener("click", saveProduct);
    document.getElementById("resetProductBtn").addEventListener("click", resetProductForm);
    document.getElementById("refreshOrdersBtn").addEventListener("click", loadOrders);
}

function loadDashboard() {
    request({
        url: "/api/merchant/statistics" + buildQuery({ merchantId: merchantUser.userId }),
        method: "GET"
    }).then(function (data) {
        renderDashboard(data || {});
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderDashboard(data) {
    document.getElementById("merchantStats").innerHTML = `
        <div class="stat-card">
            <p>今日订单数</p>
            <strong>${data.todayOrderCount || 0}</strong>
        </div>
        <div class="stat-card">
            <p>今日营业额</p>
            <strong>${formatMoney(data.todayRevenue || 0)}</strong>
        </div>
        <div class="stat-card">
            <p>商品数量</p>
            <strong>${data.productCount || 0}</strong>
        </div>
        <div class="stat-card">
            <p>待处理订单</p>
            <strong>${data.pendingOrderCount || 0}</strong>
        </div>
    `;
}

function loadCategories() {
    request({
        url: "/api/product/categories",
        method: "GET"
    }).then(function (list) {
        categories = list || [];
        renderCategorySelect();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderCategorySelect() {
    const select = document.getElementById("categorySelect");
    let html = "";

    categories.forEach(function (item) {
        html += `<option value="${item.categoryId}">${item.categoryName}</option>`;
    });

    select.innerHTML = html;
}

function loadProducts() {
    request({
        url: "/api/merchant/products" + buildQuery({ merchantId: merchantUser.userId }),
        method: "GET"
    }).then(function (list) {
        merchantProducts = list || [];
        renderProducts();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderProducts() {
    const tbody = document.getElementById("merchantProductTbody");

    if (!merchantProducts.length) {
        tbody.innerHTML = `<tr><td colspan="7">暂无商品</td></tr>`;
        return;
    }

    let html = "";

    merchantProducts.forEach(function (item) {
        html += `
            <tr>
                <td>${item.productId}</td>
                <td>${item.name}</td>
                <td>${item.categoryName || "-"}</td>
                <td>${formatMoney(item.price)}</td>
                <td>${item.orderCount || 0}</td>
                <td>
                    ${Number(item.status) === 1
            ? '<span class="badge green">上架</span>'
            : '<span class="badge red">下架</span>'}
                </td>
                <td>
                    <div class="action-row">
                        <button class="outline-btn" onclick="editProduct(${item.productId})">编辑</button>
                        <button class="${Number(item.status) === 1 ? 'warning-btn' : 'success-btn'}" onclick="toggleProduct(${item.productId})">
                            ${Number(item.status) === 1 ? "下架" : "上架"}
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });

    tbody.innerHTML = html;
}

function saveProduct() {
    const productId = document.getElementById("productIdInput").value;
    const name = document.getElementById("productNameInput").value.trim();
    const categoryId = document.getElementById("categorySelect").value;
    const price = document.getElementById("priceInput").value;
    const imageUrl = document.getElementById("imageInput").value.trim();
    const description = document.getElementById("descInput").value.trim();

    if (!name) {
        showMessage("请输入商品名称", "error");
        return;
    }

    if (!price || Number(price) <= 0) {
        showMessage("请输入正确的商品价格", "error");
        return;
    }

    const payload = {
        productId: productId ? Number(productId) : null,
        merchantId: merchantUser.userId,
        categoryId: Number(categoryId),
        name: name,
        price: Number(price),
        imageUrl: imageUrl,
        description: description
    };

    const isEdit = !!productId;

    request({
        url: isEdit ? "/api/merchant/products/" + productId : "/api/merchant/products",
        method: isEdit ? "PUT" : "POST",
        data: payload
    }).then(function () {
        showMessage("保存成功", "success");
        resetProductForm();
        loadProducts();
        loadDashboard();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function editProduct(productId) {
    const item = merchantProducts.find(function (product) {
        return Number(product.productId) === Number(productId);
    });

    if (!item) {
        showMessage("商品不存在", "error");
        return;
    }

    document.getElementById("productIdInput").value = item.productId;
    document.getElementById("productNameInput").value = item.name;
    document.getElementById("categorySelect").value = item.categoryId;
    document.getElementById("priceInput").value = item.price;
    document.getElementById("imageInput").value = item.imageUrl || "";
    document.getElementById("descInput").value = item.description || "";
}

function resetProductForm() {
    document.getElementById("productIdInput").value = "";
    document.getElementById("productNameInput").value = "";
    document.getElementById("priceInput").value = "";
    document.getElementById("imageInput").value = "";
    document.getElementById("descInput").value = "";

    if (categories.length) {
        document.getElementById("categorySelect").value = categories[0].categoryId;
    }
}

function toggleProduct(productId) {
    request({
        url: "/api/merchant/products/" + productId + "/toggle",
        method: "POST"
    }).then(function () {
        showMessage("状态已更新", "success");
        loadProducts();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function loadOrders() {
    request({
        url: "/api/merchant/orders" + buildQuery({ merchantId: merchantUser.userId }),
        method: "GET"
    }).then(function (list) {
        renderOrders(list || []);
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderOrders(list) {
    const box = document.getElementById("merchantOrderList");

    if (!list.length) {
        box.innerHTML = `<div class="empty">暂无订单</div>`;
        return;
    }

    let html = "";

    list.forEach(function (order) {
        const status = Number(order.status);

        html += `
            <div class="order-card">
                <div class="order-card-head">
                    <div>
                        <h3>订单 #${order.orderId}</h3>
                        <p>${order.orderTime || ""}</p>
                    </div>
                    <span class="badge">${orderStatusText[status] || "未知状态"}</span>
                </div>

                <p>收货地址：${order.address}</p>
                <p>订单备注：${order.remark || "无"}</p>
                <p>订单金额：<strong>${formatMoney(order.totalPrice)}</strong></p>
                <p>是否催单：${Number(order.isUrged) === 1 ? '<span class="badge yellow">已催单</span>' : "未催单"}</p>

                <div class="order-items">
                    ${renderOrderItems(order.items || [])}
                </div>

                <div class="action-row" style="margin-top: 14px;">
                    ${status === 0 ? `<button class="success-btn" onclick="acceptOrder(${order.orderId})">接单</button>` : ""}
                    ${status === 1 ? `<button class="warning-btn" onclick="finishOrder(${order.orderId})">标记出餐</button>` : ""}
                </div>
            </div>
        `;
    });

    box.innerHTML = html;
}

function renderOrderItems(items) {
    if (!items.length) {
        return "暂无订单明细";
    }

    return items.map(function (item) {
        return `<p>${item.name || "商品"} × ${item.quantity}，单价 ${formatMoney(item.price)}</p>`;
    }).join("");
}

function acceptOrder(orderId) {
    request({
        url: "/api/merchant/orders/" + orderId + "/accept",
        method: "POST"
    }).then(function () {
        showMessage("已接单", "success");
        loadOrders();
        loadDashboard();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function finishOrder(orderId) {
    request({
        url: "/api/merchant/orders/" + orderId + "/finish",
        method: "POST"
    }).then(function () {
        showMessage("已标记出餐", "success");
        loadOrders();
        loadDashboard();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}