package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.SysUserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {
    private final SysUserMapper sysUserMapper;

    public UserAuthController(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @PostMapping("/login")
    public UserApiResult login(@RequestBody Map body, HttpSession session) {
        String username = str(body.get("username"));
        String password = str(body.get("password"));
        if (username.isEmpty() || password.isEmpty()) {
            return UserApiResult.error("请填写账号和密码");
        }
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null
                || user.getRoleType() == null || user.getRoleType() != 1
                || user.getStatus() == null || user.getStatus() != 1
                || user.getPassword() == null || !user.getPassword().equals(password)) {
            return UserApiResult.error("用户账号或密码错误");
        }
        return loginSuccess(user, session, "登录成功");
    }

    @PostMapping("/register")
    public UserApiResult register(@RequestBody Map body, HttpSession session) {
        String username = str(body.get("username"));
        String password = str(body.get("password"));
        String realName = str(body.get("realName"));
        String phone = str(body.get("phone"));
        if (username.length() < 3 || password.length() < 6 || realName.isEmpty()) {
            return UserApiResult.error("请填写有效的账号、密码和姓名");
        }
        if (sysUserMapper.selectByUsername(username) != null) {
            return UserApiResult.error("账号已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRoleType(1);
        user.setCreditScore(10);
        user.setStatus(1);
        user.setLevelId(1);
        user.setGrowthValue(0);
        sysUserMapper.insert(user);
        return loginSuccess(user, session, "注册成功");
    }

    private UserApiResult loginSuccess(SysUser user, HttpSession session, String message) {
        // 普通用户登录与管理员登录隔离。
        session.removeAttribute("adminId");
        session.removeAttribute("adminUser");
        session.removeAttribute("adminRoleType");

        session.setAttribute("userId", user.getUserId());
        session.setAttribute("loginUserId", user.getUserId());
        session.setAttribute("loginUser", user);
        session.setAttribute("loginRoleType", 1);
        user.setPassword(null);
        Map data = new HashMap<>();
        data.put("token", String.valueOf(user.getUserId()));
        data.put("user", user);
        return UserApiResult.success(message, data);
    }

    private String str(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
