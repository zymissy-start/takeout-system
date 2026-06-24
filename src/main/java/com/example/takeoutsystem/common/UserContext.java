package com.example.takeoutsystem.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
//import javax.servlet.http.HttpSession;

/**
 * 当前用户解析工具。
 * 说明：正式接入登录模块后，建议登录同学把 userId 放入 Session 或 JWT，
 * 当前工具会优先读取 Session、X-User-Id、请求参数，最后仅为了课程演示默认使用 user_id=2。
 */
public class UserContext {
    private UserContext() {}

    public static Integer getCurrentUserId(HttpServletRequest request) {
        Integer id = getFromSession(request);
        if (id != null) return id;

        id = parseInt(request.getHeader("X-User-Id"));
        if (id != null) return id;

        id = parseAuthorization(request.getHeader("Authorization"));
        if (id != null) return id;

        id = parseInt(request.getParameter("userId"));
        if (id != null) return id;

        // 开发演示默认用户：db_init.sql 中 zhangsan 的 user_id=2。
        // 正式联调登录后，请改成抛出未登录异常。
        return 2;
    }

    private static Integer getFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object v = session.getAttribute("userId");
        if (v == null) v = session.getAttribute("USER_ID");
        if (v == null) v = session.getAttribute("loginUserId");
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Long) return ((Long) v).intValue();
        return parseInt(v == null ? null : String.valueOf(v));
    }

    private static Integer parseAuthorization(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        String token = value.trim();
        if (token.startsWith("Bearer ")) token = token.substring(7).trim();
        // 本课程设计用 userId 作为简化 token，便于前后端联调。生产环境应替换成 JWT。
        return parseInt(token);
    }

    private static Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try { return Integer.parseInt(value.trim()); } catch (Exception e) { return null; }
    }
}
