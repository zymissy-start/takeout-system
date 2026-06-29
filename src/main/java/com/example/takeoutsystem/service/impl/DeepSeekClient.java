package com.example.takeoutsystem.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeepSeekClient {
    private static final String DEFAULT_URL = "https://api.deepseek.com/chat/completions";
    private static final String DEFAULT_MODEL = "deepseek-chat";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    public boolean isEnabled() {
        return !blank(apiKey());
    }

    public String chat(String systemPrompt, String userPrompt) {
        String key = apiKey();
        if (blank(key)) return null;
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", valueOrDefault(config("deepseek.model", "DEEPSEEK_MODEL"), DEFAULT_MODEL));
            body.put("temperature", 0.6);
            body.put("stream", false);
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(message("system", systemPrompt));
            messages.add(message("user", userPrompt));
            body.put("messages", messages);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(valueOrDefault(config("deepseek.api.url", "DEEPSEEK_API_URL"), DEFAULT_URL)))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + key)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null;
            }
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            return content.isMissingNode() || content.isNull() ? null : content.asText();
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> message(String role, String content) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("role", role);
        map.put("content", content);
        return map;
    }

    private String apiKey() {
        return config("deepseek.api.key", "DEEPSEEK_API_KEY");
    }

    private String config(String propertyName, String envName) {
        String value = System.getProperty(propertyName);
        if (blank(value)) value = System.getenv(envName);
        return value;
    }

    private String valueOrDefault(String value, String fallback) {
        return blank(value) ? fallback : value.trim();
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }
}