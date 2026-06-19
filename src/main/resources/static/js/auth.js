// static/js/auth.js

document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");

    loginForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!username) {
            showMessage("请输入账号", "error");
            return;
        }

        if (!password) {
            showMessage("请输入密码", "error");
            return;
        }

        request({
            url: "/api/auth/login",
            method: "POST",
            data: {
                username: username,
                password: password
            }
        }).then(function (user) {
            if (!user) {
                showMessage("登录失败，后端未返回用户信息", "error");
                return;
            }

            if (Number(user.roleType) !== 1) {
                showMessage("当前阶段只开放顾客端，请使用普通用户账号登录", "error");
                return;
            }

            localStorage.setItem("loginUser", JSON.stringify(user));

            if (user.token) {
                localStorage.setItem("token", user.token);
            }

            location.href = "/customer/index.html";
        }).catch(function (error) {
            showMessage(error, "error");
        });
    });
});