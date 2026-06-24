(function () {
    document.addEventListener('DOMContentLoaded', init);

    function init() {
        document.querySelector('#riderRegisterForm').addEventListener('submit', submitRegister);
    }

    async function submitRegister(event) {
        event.preventDefault();

        const data = {
            username: value('#usernameInput'),
            realName: value('#realNameInput'),
            password: value('#passwordInput'),
            confirmPassword: value('#confirmPasswordInput'),
            phone: value('#phoneInput'),
            vehicleType: value('#vehicleTypeInput'),
            vehicleNumber: value('#vehicleNumberInput')
        };

        if (!/^[A-Za-z0-9_]{3,20}$/.test(data.username)) {
            alert('账号只能包含字母、数字、下划线，长度为3到20位');
            return;
        }

        if (data.password.length < 6 || data.password.length > 20) {
            alert('密码长度必须为6到20位');
            return;
        }

        if (data.password !== data.confirmPassword) {
            alert('两次输入的密码不一致');
            return;
        }

        if (!data.realName) {
            alert('骑手姓名不能为空');
            return;
        }

        if (!/^1\d{10}$/.test(data.phone)) {
            alert('手机号格式不正确');
            return;
        }

        try {
            await request('/rider/register', data);
            alert('骑手注册成功，请登录');
            location.href = '/rider/login.html';
        } catch (e) {
            alert(e.message || '骑手注册失败');
        }
    }

    async function request(url, body) {
        const response = await fetch(url, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
            },
            body: new URLSearchParams(body)
        });

        const result = await response.json();

        const success =
            result.success === true ||
            result.code === 200 ||
            result.code === 0;

        if (!success) {
            throw new Error(result.message || '请求失败');
        }

        return result.data;
    }

    function value(selector) {
        const el = document.querySelector(selector);
        return el ? el.value.trim() : '';
    }
})();