// static/js/admin.js

let adminUser = null;

const roleText = {
    1: "顾客",
    2: "商家",
    3: "骑手",
    4: "管理员"
};

const orderStatusText = {
    0: "待商家接单",
    1: "商家已接单",
    2: "已出餐，等待骑手",
    3: "骑手配送中",
    4: "已完成"
};

document.addEventListener("DOMContentLoaded", function () {
    adminUser = requireLogin();

    if (!adminUser) {
        return;
    }

    if (Number(adminUser.roleType) !== 4) {
        showMessage("当前账号不是管理员账号", "error");
        setTimeout(function () {
            logout();
        }, 1200);
        return;
    }

    document.getElementById("adminUserInfo").innerText = "管理员：" + (adminUser.realName || adminUser.username);

    initMenu();
    initEvents();

    loadAdminStats();
    loadUsers();
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
                loadAdminStats();
            }

            if (page === "users") {
                loadUsers();
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
    document.getElementById("refreshAdminStatsBtn").addEventListener("click", loadAdminStats);
    document.getElementById("refreshUsersBtn").addEventListener("click", loadUsers);
    document.getElementById("refreshProductsBtn").addEventListener("click", loadProducts);
    document.getElementById("refreshAdminOrdersBtn").addEventListener("click", loadOrders);
}

function loadAdminStats() {
    request({
        url: "/api/admin/statistics",
        method: "GET"
    }).then(function (data) {
        renderAdminStats(data || {});
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderAdminStats(data) {
    document.getElementById("adminStats").innerHTML = `
        <div class="stat-card">
            <p>用户总数</p>
            <strong>${data.userCount || 0}</strong>
        </div>
        <div class="stat-card">
            <p>商品总数</p>
            <strong>${data.productCount || 0}</strong>
        </div>
        <div class="stat-card">
            <p>订单总数</p>
            <strong>${data.orderCount || 0}</strong>
        </div>
        <div class="stat-card">
            <p>平台交易额</p>
            <strong>${formatMoney(data.totalRevenue || 0)}</strong>
        </div>
    `;
}

function loadUsers() {
    request({
        url: "/api/admin/users",
        method: "GET"
    }).then(function (list) {
        renderUsers(list || []);
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderUsers(list) {
    const tbody = document.getElementById("userTbody");

    if (!list.length) {
        tbody.innerHTML = `<tr><td colspan="6">暂无用户</td></tr>`;
        return;
    }

    let html = "";

    list.forEach(function (user) {
        html += `
            <tr>
                <td>${user.userId}</td>
                <td>${user.username}</td>
                <td>${user.realName || "-"}</td>
                <td>${roleText[user.roleType] || "未知"}</td>
                <td>
                    ${Number(user.status) === 1
            ? '<span class="badge green">正常</span>'
            : '<span class="badge red">禁用</span>'}
                </td>
                <td>
                    <button class="${Number(user.status) === 1 ? "danger-btn" : "success-btn"}" onclick="toggleUser(${user.userId})">
                        ${Number(user.status) === 1 ? "禁用" : "启用"}
                    </button>
                </td>
            </tr>
        `;
    });

    tbody.innerHTML = html;
}

function toggleUser(userId) {
    request({
        url: "/api/admin/users/" + userId + "/toggle",
        method: "POST"
    }).then(function () {
        showMessage("用户状态已更新", "success");
        loadUsers();
        loadAdminStats();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function loadProducts() {
    request({
        url: "/api/admin/products",
        method: "GET"
    }).then(function (list) {
        renderProducts(list || []);
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderProducts(list) {
    const tbody = document.getElementById("adminProductTbody");

    if (!list.length) {
        tbody.innerHTML = `<tr><td colspan="7">暂无商品</td></tr>`;
        return;
    }

    let html = "";

    list.forEach(function (item) {
        html += `
            <tr>
                <td>${item.productId}</td>
                <td>${item.name}</td>
                <td>${item.merchantId}</td>
                <td>${item.categoryName || "-"}</td>
                <td>${formatMoney(item.price)}</td>
                <td>
                    ${Number(item.status) === 1
            ? '<span class="badge green">上架</span>'
            : '<span class="badge red">下架</span>'}
                </td>
                <td>
                    <button class="${Number(item.status) === 1 ? "warning-btn" : "success-btn"}" onclick="toggleProduct(${item.productId})">
                        ${Number(item.status) === 1 ? "下架" : "上架"}
                    </button>
                </td>
            </tr>
        `;
    });

    tbody.innerHTML = html;
}

function toggleProduct(productId) {
    request({
        url: "/api/admin/products/" + productId + "/toggle",
        method: "POST"
    }).then(function () {
        showMessage("商品状态已更新", "success");
        loadProducts();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function loadOrders() {
    request({
        url: "/api/admin/orders",
        method: "GET"
    }).then(function (list) {
        renderOrders(list || []);
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderOrders(list) {
    const box = document.getElementById("adminOrderList");

    if (!list.length) {
        box.innerHTML = `<div class="empty">暂无订单</div>`;
        return;
    }

    let html = "";

    list.forEach(function (order) {
        html += `
            <div class="order-card">
                <div class="order-card-head">
                    <div>
                        <h3>订单 #${order.orderId}</h3>
                        <p>${order.orderTime || ""}</p>
                    </div>
                    <span class="badge">${orderStatusText[order.status] || "未知状态"}</span>
                </div>

                <p>顾客ID：${order.userId}</p>
                <p>商家ID：${order.merchantId}</p>
                <p>骑手：${order.riderName || "暂无"} ${order.riderPhone || ""}</p>
                <p>收货地址：${order.address}</p>
                <p>订单金额：<strong>${formatMoney(order.totalPrice)}</strong></p>

                <div class="order-items">
                    ${renderOrderItems(order.items || [])}
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