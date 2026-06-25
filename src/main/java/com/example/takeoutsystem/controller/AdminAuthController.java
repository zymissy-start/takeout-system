package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.SysUserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {
    private final SysUserMapper sysUserMapper;

    public AdminAuthController(SysUserMapper sysUserMapper) {
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
                || user.getRoleType() == null || user.getRoleType() != 4
                || user.getStatus() == null || user.getStatus() != 1
                || user.getPassword() == null || !user.getPassword().equals(password)) {
            return UserApiResult.error("管理员账号或密码错误");
        }

        // 管理员登录与普通用户登录隔离，避免 userId/session/localStorage 混用。
        session.removeAttribute("userId");
        session.removeAttribute("USER_ID");
        session.removeAttribute("loginUserId");
        session.removeAttribute("loginUser");
        session.removeAttribute("loginRoleType");

        session.setAttribute("adminId", user.getUserId());
        session.setAttribute("adminUser", user);
        session.setAttribute("adminRoleType", 4);
        user.setPassword(null);

        Map data = new HashMap<>();
        data.put("adminToken", String.valueOf(user.getUserId()));
        data.put("admin", user);
        return UserApiResult.success("登录成功", data);
    }

    @PostMapping("/logout")
    public UserApiResult logout(HttpSession session) {
        session.removeAttribute("adminId");
        session.removeAttribute("adminUser");
        session.removeAttribute("adminRoleType");
        return UserApiResult.success("退出成功", null);
    }

    private String str(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
