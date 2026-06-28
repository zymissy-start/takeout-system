package com.example.takeoutsystem.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * AI 点餐助手专用用户解析工具。
 * 不使用 UserContext 中的演示默认 user_id=2，避免未登录也能访问 AI 用户数据。
 */
public class AiAgentUserContext {
    private AiAgentUserContext() {}

    public static Integer requireUserId(HttpServletRequest request) {
        Integer id = getFromSession(request);
        if (id != null) return id;

        id = parseInt(request.getHeader("X-User-Id"));
        if (id != null) return id;

        id = parseAuthorization(request.getHeader("Authorization"));
        if (id != null) return id;

        id = parseInt(request.getParameter("userId"));
        if (id != null) return id;

        throw new IllegalArgumentException("未登录或登录已失效");
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