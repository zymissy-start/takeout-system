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

            localStorage.setItem("loginUser", JSON.stringify(user));

            if (user.token) {
                localStorage.setItem("token", user.token);
            }

            const roleType = Number(user.roleType);

            if (roleType === 1) {
                location.href = "/customer/index.html";
                return;
            }

            if (roleType === 2) {
                location.href = "/merchant/index.html";
                return;
            }

            if (roleType === 3) {
                location.href = "/rider/index.html";
                return;
            }

            if (roleType === 4) {
                location.href = "/admin/index.html";
                return;
            }

            showMessage("未知用户角色，无法跳转", "error");
        }).catch(function (error) {
            showMessage(error, "error");
        });
    });
});