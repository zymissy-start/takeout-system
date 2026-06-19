// static/js/rider.js

let riderUser = null;

document.addEventListener("DOMContentLoaded", function () {
    riderUser = requireLogin();

    if (!riderUser) {
        return;
    }

    if (Number(riderUser.roleType) !== 3) {
        showMessage("当前账号不是骑手账号", "error");
        setTimeout(function () {
            logout();
        }, 1200);
        return;
    }

    document.getElementById("riderUserInfo").innerText = "骑手：" + (riderUser.realName || riderUser.username);

    initMenu();
    initEvents();

    loadAvailableOrders();
    loadMyOrders();
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

            if (page === "available") {
                loadAvailableOrders();
            }

            if (page === "mine") {
                loadMyOrders();
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
    document.getElementById("refreshAvailableBtn").addEventListener("click", loadAvailableOrders);
    document.getElementById("refreshMineBtn").addEventListener("click", loadMyOrders);
}

function loadAvailableOrders() {
    request({
        url: "/api/rider/available-orders",
        method: "GET"
    }).then(function (list) {
        renderAvailableOrders(list || []);
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderAvailableOrders(list) {
    const box = document.getElementById("availableOrderList");

    if (!list.length) {
        box.innerHTML = `<div class="empty">暂无可接订单。需要商家先在商家端点击“标记出餐”。</div>`;
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
                    <span class="badge yellow">待骑手接单</span>
                </div>

                <p>收货地址：${order.address}</p>
                <p>订单备注：${order.remark || "无"}</p>
                <p>订单金额：<strong>${formatMoney(order.totalPrice)}</strong></p>

                <div class="order-items">
                    ${renderOrderItems(order.items || [])}
                </div>

                <div class="action-row" style="margin-top: 14px;">
                    <button class="success-btn" onclick="takeOrder(${order.orderId})">接配送单</button>
                </div>
            </div>
        `;
    });

    box.innerHTML = html;
}

function loadMyOrders() {
    request({
        url: "/api/rider/my-orders" + buildQuery({ riderId: riderUser.userId }),
        method: "GET"
    }).then(function (list) {
        renderMyOrders(list || []);
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function renderMyOrders(list) {
    const box = document.getElementById("myOrderList");

    if (!list.length) {
        box.innerHTML = `<div class="empty">暂无配送订单</div>`;
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
                    <span class="badge ${status === 4 ? "green" : ""}">
                        ${status === 4 ? "已完成" : "配送中"}
                    </span>
                </div>

                <p>收货地址：${order.address}</p>
                <p>订单备注：${order.remark || "无"}</p>
                <p>预计送达：${order.estimatedArrivalTime || "暂无"}</p>

                <div class="order-items">
                    ${renderOrderItems(order.items || [])}
                </div>

                <div class="action-row" style="margin-top: 14px;">
                    ${status === 3 ? `<button class="success-btn" onclick="completeOrder(${order.orderId})">确认送达</button>` : ""}
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

function takeOrder(orderId) {
    request({
        url: "/api/rider/orders/" + orderId + "/take",
        method: "POST",
        data: {
            riderId: riderUser.userId,
            riderName: riderUser.realName || riderUser.username,
            riderPhone: riderUser.phone || "13800000000"
        }
    }).then(function () {
        showMessage("接单成功", "success");
        loadAvailableOrders();
        loadMyOrders();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}

function completeOrder(orderId) {
    request({
        url: "/api/rider/orders/" + orderId + "/complete",
        method: "POST"
    }).then(function () {
        showMessage("订单已完成", "success");
        loadMyOrders();
    }).catch(function (error) {
        showMessage(error, "error");
    });
}