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
// ==================== Mock 数据区域开始 ====================

function getMockUsers() {
    const saved = localStorage.getItem("mockUsers");

    if (saved) {
        return JSON.parse(saved);
    }

    const users = [
        {
            userId: 1,
            username: "admin",
            password: "123456",
            realName: "系统管理员",
            phone: "13900000001",
            roleType: 4,
            status: 1
        },
        {
            userId: 2,
            username: "zhangsan",
            password: "123456",
            realName: "张三",
            phone: "13900000002",
            roleType: 1,
            status: 1
        },
        {
            userId: 3,
            username: "burger_king",
            password: "123456",
            realName: "汉堡王商家",
            phone: "13900000003",
            roleType: 2,
            status: 1
        },
        {
            userId: 4,
            username: "rider_knight",
            password: "123456",
            realName: "极速骑手",
            phone: "13900000004",
            roleType: 3,
            status: 1
        }
    ];

    localStorage.setItem("mockUsers", JSON.stringify(users));
    return users;
}

function saveMockUsers(users) {
    localStorage.setItem("mockUsers", JSON.stringify(users));
}

function getMockCategories() {
    return [
        { categoryId: 1, categoryName: "美食" },
        { categoryId: 2, categoryName: "饮品" },
        { categoryId: 3, categoryName: "跑腿代购" }
    ];
}

function getMockProducts() {
    const saved = localStorage.getItem("mockProducts");

    if (saved) {
        return JSON.parse(saved);
    }

    const products = [
        {
            productId: 1,
            merchantId: 3,
            categoryId: 1,
            categoryName: "美食",
            name: "经典双层芝士汉堡",
            description: "芝士浓郁，牛肉多汁",
            price: 25.00,
            imageUrl: "/images/burger1.jpg",
            orderCount: 120,
            status: 1
        },
        {
            productId: 2,
            merchantId: 3,
            categoryId: 1,
            categoryName: "美食",
            name: "香辣鸡腿堡",
            description: "外酥里嫩，微辣可口",
            price: 18.50,
            imageUrl: "/images/burger2.jpg",
            orderCount: 85,
            status: 1
        },
        {
            productId: 3,
            merchantId: 3,
            categoryId: 2,
            categoryName: "饮品",
            name: "冰镇可乐",
            description: "加冰口感更佳",
            price: 6.00,
            imageUrl: "/images/cola.jpg",
            orderCount: 200,
            status: 1
        }
    ];

    localStorage.setItem("mockProducts", JSON.stringify(products));
    return products;
}

function saveMockProducts(products) {
    localStorage.setItem("mockProducts", JSON.stringify(products));
}

function getMockOrders() {
    const saved = localStorage.getItem("mockOrders");
    return saved ? JSON.parse(saved) : [];
}

function saveMockOrders(orders) {
    localStorage.setItem("mockOrders", JSON.stringify(orders));
}

function fillCategoryName(product) {
    const category = getMockCategories().find(function (item) {
        return Number(item.categoryId) === Number(product.categoryId);
    });

    product.categoryName = category ? category.categoryName : "";
    return product;
}

function mockRequest(options) {
    return new Promise(function (resolve, reject) {
        setTimeout(function () {
            const url = options.url;
            const method = options.method || "GET";
            const data = options.data || {};

            // 登录
            if (url === "/api/auth/login" && method === "POST") {
                const users = getMockUsers();

                const user = users.find(function (item) {
                    return item.username === data.username && item.password === data.password;
                });

                if (!user) {
                    reject("账号或密码错误");
                    return;
                }

                if (Number(user.status) !== 1) {
                    reject("账号已被禁用");
                    return;
                }

                resolve({
                    userId: user.userId,
                    username: user.username,
                    realName: user.realName,
                    phone: user.phone,
                    roleType: user.roleType,
                    token: "mock-token-" + user.userId
                });
                return;
            }

            // 商品分类
            if (url === "/api/product/categories" && method === "GET") {
                resolve(getMockCategories());
                return;
            }

            // 顾客商品列表
            if (url.indexOf("/api/product/list") === 0 && method === "GET") {
                const query = new URLSearchParams(url.split("?")[1] || "");
                const categoryId = query.get("categoryId");
                const keyword = query.get("keyword");

                let products = getMockProducts().filter(function (item) {
                    return Number(item.status) === 1;
                });

                if (categoryId) {
                    products = products.filter(function (item) {
                        return Number(item.categoryId) === Number(categoryId);
                    });
                }

                if (keyword) {
                    products = products.filter(function (item) {
                        return item.name.indexOf(keyword) !== -1;
                    });
                }

                resolve(products);
                return;
            }

            // 顾客创建订单
            if (url === "/api/customer/orders" && method === "POST") {
                const products = getMockProducts();
                const orders = getMockOrders();

                let totalPrice = 0;

                const items = (data.items || []).map(function (item) {
                    const product = products.find(function (p) {
                        return Number(p.productId) === Number(item.productId);
                    });

                    if (!product) {
                        return null;
                    }

                    const quantity = Number(item.quantity);
                    totalPrice += Number(product.price) * quantity;

                    product.orderCount = Number(product.orderCount || 0) + quantity;

                    return {
                        productId: product.productId,
                        name: product.name,
                        quantity: quantity,
                        price: Number(product.price)
                    };
                }).filter(Boolean);

                saveMockProducts(products);

                const order = {
                    orderId: Date.now(),
                    userId: data.userId,
                    merchantId: data.merchantId,
                    riderId: null,
                    totalPrice: totalPrice,
                    status: 0,
                    orderTime: new Date().toLocaleString(),
                    merchantConfirmTime: "",
                    kitchenFinishTime: "",
                    estimatedArrivalTime: "",
                    finishTime: "",
                    riderPhone: "",
                    riderName: "",
                    address: data.address,
                    remark: data.remark,
                    isUrged: 0,
                    items: items
                };

                orders.unshift(order);
                saveMockOrders(orders);

                resolve(order);
                return;
            }

            // 顾客查看自己的订单
            if (url.indexOf("/api/customer/orders") === 0 && method === "GET") {
                const query = new URLSearchParams(url.split("?")[1] || "");
                const userId = query.get("userId");

                const orders = getMockOrders().filter(function (order) {
                    return Number(order.userId) === Number(userId);
                });

                resolve(orders);
                return;
            }

            // 顾客催单
            if (url.indexOf("/api/customer/orders/") === 0 && url.indexOf("/urge") !== -1 && method === "POST") {
                const orderId = Number(url.split("/")[4]);
                const orders = getMockOrders();

                const order = orders.find(function (item) {
                    return Number(item.orderId) === orderId;
                });

                if (!order) {
                    reject("订单不存在");
                    return;
                }

                order.isUrged = 1;
                saveMockOrders(orders);

                resolve(true);
                return;
            }

            // 商家统计
            if (url.indexOf("/api/merchant/statistics") === 0 && method === "GET") {
                const query = new URLSearchParams(url.split("?")[1] || "");
                const merchantId = Number(query.get("merchantId"));

                const products = getMockProducts().filter(function (item) {
                    return Number(item.merchantId) === merchantId;
                });

                const orders = getMockOrders().filter(function (item) {
                    return Number(item.merchantId) === merchantId;
                });

                const todayRevenue = orders.reduce(function (sum, item) {
                    return sum + Number(item.totalPrice || 0);
                }, 0);

                const pendingOrderCount = orders.filter(function (item) {
                    return Number(item.status) === 0;
                }).length;

                resolve({
                    todayOrderCount: orders.length,
                    todayRevenue: todayRevenue,
                    productCount: products.length,
                    pendingOrderCount: pendingOrderCount
                });
                return;
            }

            // 商家商品列表
            if (url.indexOf("/api/merchant/products") === 0 && method === "GET") {
                const query = new URLSearchParams(url.split("?")[1] || "");
                const merchantId = Number(query.get("merchantId"));

                const products = getMockProducts().filter(function (item) {
                    return Number(item.merchantId) === merchantId;
                }).map(fillCategoryName);

                resolve(products);
                return;
            }

            // 商家新增商品
            if (url === "/api/merchant/products" && method === "POST") {
                const products = getMockProducts();

                const newProduct = fillCategoryName({
                    productId: Date.now(),
                    merchantId: data.merchantId,
                    categoryId: data.categoryId,
                    name: data.name,
                    description: data.description,
                    price: data.price,
                    imageUrl: data.imageUrl,
                    orderCount: 0,
                    status: 1
                });

                products.unshift(newProduct);
                saveMockProducts(products);

                resolve(newProduct);
                return;
            }

            // 商家修改商品
            if (url.indexOf("/api/merchant/products/") === 0 && method === "PUT") {
                const productId = Number(url.split("/")[4]);
                const products = getMockProducts();

                const product = products.find(function (item) {
                    return Number(item.productId) === productId;
                });

                if (!product) {
                    reject("商品不存在");
                    return;
                }

                product.categoryId = data.categoryId;
                product.name = data.name;
                product.description = data.description;
                product.price = data.price;
                product.imageUrl = data.imageUrl;

                fillCategoryName(product);
                saveMockProducts(products);

                resolve(product);
                return;
            }

            // 商家上下架商品
            if (url.indexOf("/api/merchant/products/") === 0 && url.indexOf("/toggle") !== -1 && method === "POST") {
                const productId = Number(url.split("/")[4]);
                const products = getMockProducts();

                const product = products.find(function (item) {
                    return Number(item.productId) === productId;
                });

                if (!product) {
                    reject("商品不存在");
                    return;
                }

                product.status = Number(product.status) === 1 ? 0 : 1;
                saveMockProducts(products);

                resolve(product);
                return;
            }

            // 商家订单列表
            if (url.indexOf("/api/merchant/orders") === 0 && method === "GET") {
                const query = new URLSearchParams(url.split("?")[1] || "");
                const merchantId = Number(query.get("merchantId"));

                const orders = getMockOrders().filter(function (item) {
                    return Number(item.merchantId) === merchantId;
                });

                resolve(orders);
                return;
            }

            // 商家接单
            if (url.indexOf("/api/merchant/orders/") === 0 && url.indexOf("/accept") !== -1 && method === "POST") {
                const orderId = Number(url.split("/")[4]);
                const orders = getMockOrders();

                const order = orders.find(function (item) {
                    return Number(item.orderId) === orderId;
                });

                if (!order) {
                    reject("订单不存在");
                    return;
                }

                if (Number(order.status) !== 0) {
                    reject("当前订单状态不能接单");
                    return;
                }

                order.status = 1;
                order.merchantConfirmTime = new Date().toLocaleString();

                saveMockOrders(orders);
                resolve(order);
                return;
            }

            // 商家出餐
            if (url.indexOf("/api/merchant/orders/") === 0 && url.indexOf("/finish") !== -1 && method === "POST") {
                const orderId = Number(url.split("/")[4]);
                const orders = getMockOrders();

                const order = orders.find(function (item) {
                    return Number(item.orderId) === orderId;
                });

                if (!order) {
                    reject("订单不存在");
                    return;
                }

                if (Number(order.status) !== 1) {
                    reject("当前订单状态不能出餐");
                    return;
                }

                order.status = 2;
                order.kitchenFinishTime = new Date().toLocaleString();

                saveMockOrders(orders);
                resolve(order);
                return;
            }

            // 骑手可接订单
            if (url === "/api/rider/available-orders" && method === "GET") {
                const orders = getMockOrders().filter(function (item) {
                    return Number(item.status) === 2;
                });

                resolve(orders);
                return;
            }

            // 骑手我的订单
            if (url.indexOf("/api/rider/my-orders") === 0 && method === "GET") {
                const query = new URLSearchParams(url.split("?")[1] || "");
                const riderId = Number(query.get("riderId"));

                const orders = getMockOrders().filter(function (item) {
                    return Number(item.riderId) === riderId;
                });

                resolve(orders);
                return;
            }

            // 骑手接单
            if (url.indexOf("/api/rider/orders/") === 0 && url.indexOf("/take") !== -1 && method === "POST") {
                const orderId = Number(url.split("/")[4]);
                const orders = getMockOrders();

                const order = orders.find(function (item) {
                    return Number(item.orderId) === orderId;
                });

                if (!order) {
                    reject("订单不存在");
                    return;
                }

                if (Number(order.status) !== 2) {
                    reject("当前订单不可接");
                    return;
                }

                order.status = 3;
                order.riderId = data.riderId;
                order.riderName = data.riderName;
                order.riderPhone = data.riderPhone;
                order.estimatedArrivalTime = new Date(Date.now() + 30 * 60 * 1000).toLocaleString();

                saveMockOrders(orders);
                resolve(order);
                return;
            }

            // 骑手完成配送
            if (url.indexOf("/api/rider/orders/") === 0 && url.indexOf("/complete") !== -1 && method === "POST") {
                const orderId = Number(url.split("/")[4]);
                const orders = getMockOrders();

                const order = orders.find(function (item) {
                    return Number(item.orderId) === orderId;
                });

                if (!order) {
                    reject("订单不存在");
                    return;
                }

                if (Number(order.status) !== 3) {
                    reject("当前订单不能完成");
                    return;
                }

                order.status = 4;
                order.finishTime = new Date().toLocaleString();

                saveMockOrders(orders);
                resolve(order);
                return;
            }

            // 管理员统计
            if (url === "/api/admin/statistics" && method === "GET") {
                const users = getMockUsers();
                const products = getMockProducts();
                const orders = getMockOrders();

                const totalRevenue = orders.reduce(function (sum, item) {
                    return sum + Number(item.totalPrice || 0);
                }, 0);

                resolve({
                    userCount: users.length,
                    productCount: products.length,
                    orderCount: orders.length,
                    totalRevenue: totalRevenue
                });
                return;
            }

            // 管理员用户列表
            if (url === "/api/admin/users" && method === "GET") {
                const users = getMockUsers().map(function (user) {
                    return {
                        userId: user.userId,
                        username: user.username,
                        realName: user.realName,
                        phone: user.phone,
                        roleType: user.roleType,
                        status: user.status
                    };
                });

                resolve(users);
                return;
            }

            // 管理员启用/禁用用户
            if (url.indexOf("/api/admin/users/") === 0 && url.indexOf("/toggle") !== -1 && method === "POST") {
                const userId = Number(url.split("/")[4]);
                const users = getMockUsers();

                const user = users.find(function (item) {
                    return Number(item.userId) === userId;
                });

                if (!user) {
                    reject("用户不存在");
                    return;
                }

                user.status = Number(user.status) === 1 ? 0 : 1;
                saveMockUsers(users);

                resolve(user);
                return;
            }

            // 管理员商品列表
            if (url === "/api/admin/products" && method === "GET") {
                const products = getMockProducts().map(fillCategoryName);
                resolve(products);
                return;
            }

            // 管理员上下架商品
            if (url.indexOf("/api/admin/products/") === 0 && url.indexOf("/toggle") !== -1 && method === "POST") {
                const productId = Number(url.split("/")[4]);
                const products = getMockProducts();

                const product = products.find(function (item) {
                    return Number(item.productId) === productId;
                });

                if (!product) {
                    reject("商品不存在");
                    return;
                }

                product.status = Number(product.status) === 1 ? 0 : 1;
                saveMockProducts(products);

                resolve(product);
                return;
            }

            // 管理员订单列表
            if (url === "/api/admin/orders" && method === "GET") {
                resolve(getMockOrders());
                return;
            }

            reject("Mock 接口未定义：" + method + " " + url);
        }, 250);
    });
}

// ==================== Mock 数据区域结束 ====================