// static/js/api.js

// 后端接口基础路径。
// 如果前后端都在同一个 Spring Boot 项目里，保持空字符串即可。
const API_BASE = "";

// 后端没完成时可以临时改成 true 做页面调试。
// 正式验收一定要改成 false，数据必须来自数据库。
const USE_MOCK = true;

function request(options) {
    if (USE_MOCK) {
        return mockRequest(options);
    }

    return new Promise(function (resolve, reject) {
        const xhr = new XMLHttpRequest();
        const method = options.method || "GET";
        const url = API_BASE + options.url;

        xhr.open(method, url, true);
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

        const token = localStorage.getItem("token");
        if (token) {
            xhr.setRequestHeader("Authorization", token);
        }

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                try {
                    const res = JSON.parse(xhr.responseText || "{}");

                    if (xhr.status >= 200 && xhr.status < 300) {
                        if (res.code === 200 || res.code === 0) {
                            resolve(res.data);
                        } else {
                            reject(res.message || "请求失败");
                        }
                    } else {
                        reject(res.message || "服务器异常");
                    }
                } catch (e) {
                    reject("响应数据格式错误");
                }
            }
        };

        xhr.onerror = function () {
            reject("网络连接失败");
        };

        if (method === "GET") {
            xhr.send();
        } else {
            xhr.send(JSON.stringify(options.data || {}));
        }
    });
}

function getLoginUser() {
    const userText = localStorage.getItem("loginUser");
    if (!userText) {
        return null;
    }

    try {
        return JSON.parse(userText);
    } catch (e) {
        return null;
    }
}

function requireLogin() {
    const user = getLoginUser();

    if (!user) {
        location.href = "/login.html";
        return null;
    }

    return user;
}

function logout() {
    localStorage.removeItem("loginUser");
    localStorage.removeItem("token");
    location.href = "/login.html";
}

function formatMoney(value) {
    const num = Number(value || 0);
    return "￥" + num.toFixed(2);
}

function showMessage(text, type) {
    const box = document.getElementById("messageBox");
    if (!box) {
        alert(text);
        return;
    }

    box.innerText = text;
    box.className = "message-box show " + (type || "info");

    setTimeout(function () {
        box.className = "message-box";
    }, 2200);
}

function buildQuery(params) {
    const arr = [];

    Object.keys(params).forEach(function (key) {
        const value = params[key];

        if (value !== undefined && value !== null && value !== "") {
            arr.push(encodeURIComponent(key) + "=" + encodeURIComponent(value));
        }
    });

    return arr.length ? "?" + arr.join("&") : "";
}

// 临时 Mock，仅用于后端没完成前调试页面。
function mockRequest(options) {
    return new Promise(function (resolve, reject) {
        setTimeout(function () {
            if (options.url === "/api/auth/login") {
                const data = options.data || {};

                if (data.username === "zhangsan" && data.password === "123456") {
                    resolve({
                        userId: 2,
                        username: "zhangsan",
                        realName: "张三",
                        roleType: 1,
                        token: "mock-token"
                    });
                    return;
                }

                reject("账号或密码错误");
                return;
            }

            if (options.url === "/api/product/categories") {
                resolve([
                    { categoryId: 1, categoryName: "美食" },
                    { categoryId: 2, categoryName: "饮品" },
                    { categoryId: 3, categoryName: "跑腿代购" }
                ]);
                return;
            }

            if (options.url.indexOf("/api/product/list") === 0) {
                resolve([
                    {
                        productId: 1,
                        merchantId: 3,
                        categoryId: 1,
                        name: "经典双层芝士汉堡",
                        description: "芝士浓郁，牛肉多汁",
                        price: 25.00,
                        imageUrl: "/images/burger1.jpg",
                        orderCount: 120
                    },
                    {
                        productId: 2,
                        merchantId: 3,
                        categoryId: 1,
                        name: "香辣鸡腿堡",
                        description: "外酥里嫩，微辣可口",
                        price: 18.50,
                        imageUrl: "/images/burger2.jpg",
                        orderCount: 85
                    },
                    {
                        productId: 3,
                        merchantId: 3,
                        categoryId: 2,
                        name: "冰镇可乐",
                        description: "加冰口感更佳",
                        price: 6.00,
                        imageUrl: "/images/cola.jpg",
                        orderCount: 200
                    }
                ]);
                return;
            }

            if (options.url.indexOf("/api/customer/orders") === 0 && options.method === "GET") {
                resolve([]);
                return;
            }

            if (options.url === "/api/customer/orders" && options.method === "POST") {
                resolve({
                    orderId: Math.floor(Math.random() * 10000),
                    status: 0
                });
                return;
            }

            if (options.url.indexOf("/urge") !== -1) {
                resolve(true);
                return;
            }

            reject("Mock 接口未定义：" + options.url);
        }, 300);
    });
}