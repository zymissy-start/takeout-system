package com.example.takeoutsystem.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 不修改原 user/index.html 文件，在运行时给用户首页插入 AI Agent 入口脚本。
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 20)
public class AiAgentStaticInjectFilter extends OncePerRequestFilter {
    private static final String AGENT_SCRIPT = "<script src=\"/assets/js/ai-agent-launcher.js\"></script>";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        boolean target = "GET".equalsIgnoreCase(request.getMethod())
                && ("/user/index.html".equals(uri) || "/user/".equals(uri));
        if (!target) {
            filterChain.doFilter(request, response);
            return;
        }

        BufferingResponseWrapper wrapper = new BufferingResponseWrapper(response);
        filterChain.doFilter(request, wrapper);
        wrapper.flushBuffer();

        byte[] raw = wrapper.toByteArray();
        String contentType = wrapper.getContentType();
        if (raw.length == 0 || (contentType != null && !contentType.toLowerCase().contains("text/html"))) {
            response.getOutputStream().write(raw);
            return;
        }

        String html = new String(raw, StandardCharsets.UTF_8);
        if (!html.contains("/assets/js/ai-agent-launcher.js")) {
            String lower = html.toLowerCase();
            int bodyEnd = lower.lastIndexOf("</body>");
            if (bodyEnd >= 0) {
                html = html.substring(0, bodyEnd) + AGENT_SCRIPT + html.substring(bodyEnd);
            } else {
                html = html + AGENT_SCRIPT;
            }
        }
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }

    private static class BufferingResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private ServletOutputStream outputStream;
        private PrintWriter writer;

        BufferingResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (outputStream == null) {
                outputStream = new ServletOutputStream() {
                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                    }

                    @Override
                    public void write(int b) {
                        buffer.write(b);
                    }
                };
            }
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() {
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8));
            }
            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            if (writer != null) writer.flush();
            if (outputStream != null) outputStream.flush();
        }

        byte[] toByteArray() {
            return buffer.toByteArray();
        }
    }
}