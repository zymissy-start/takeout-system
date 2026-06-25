package com.example.takeoutsystem.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 当前管理员解析工具。
 *
 * 管理员登录信息与普通用户登录信息隔离，避免同一浏览器中
 * 管理员账号被用户端接口识别为普通用户。
 */
public class AdminContext {
    private AdminContext() {}

    public static Integer getCurrentAdminId(HttpServletRequest request) {
        Integer id = getFromSession(request);
        if (id != null) return id;

        id = parseAuthorization(request.getHeader("Authorization"));
        if (id != null) return id;

        id = parseInt(request.getHeader("X-Admin-Id"));
        if (id != null) return id;

        id = parseInt(request.getParameter("adminId"));
        if (id != null) return id;

        throw new UnauthenticatedException("管理员未登录或登录已失效");
    }

    private static Integer getFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object v = session.getAttribute("adminId");
        if (v == null) v = session.getAttribute("ADMIN_ID");
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Long) return ((Long) v).intValue();
        return parseInt(v == null ? null : String.valueOf(v));
    }

    private static Integer parseAuthorization(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        String token = value.trim();
        if (token.startsWith("Bearer ")) token = token.substring(7).trim();
        return parseInt(token);
    }

    private static Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
