package com.example.takeoutsystem.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private Path resolvedDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @PostConstruct
    public void init() throws IOException {
        resolvedDir = Paths.get(uploadDir).toAbsolutePath();
        if (!Files.exists(resolvedDir)) {
            Files.createDirectories(resolvedDir);
        }
    }

    @PostMapping("/image")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        // 登录校验：商家、用户、骑手任一角色
        if (session.getAttribute("loginMerchant") == null
                && session.getAttribute("loginUserId") == null
                && session.getAttribute("loginRider") == null) {
            return Map.of("code", 401, "message", "请先登录后再上传");
        }

        if (file.isEmpty()) {
            return Map.of("code", 400, "message", "请选择文件");
        }

        if (file.getSize() > MAX_SIZE) {
            return Map.of("code", 400, "message", "文件大小不能超过5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return Map.of("code", 400, "message", "仅支持 jpg/png/gif/webp 格式的图片");
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        try {
            Path target = resolvedDir.resolve(fileName);
            file.transferTo(target.toFile());

            String url = "/uploads/" + fileName;
            return Map.of("code", 0, "data", Map.of("url", url), "message", "上传成功");
        } catch (IOException e) {
            return Map.of("code", 500, "message", "文件保存失败：" + e.getMessage());
        }
    }
}
