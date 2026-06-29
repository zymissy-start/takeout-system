package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.AiAgentUserContext;
import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.entity.AiChatRequest;
import com.example.takeoutsystem.service.AiAgentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/ai-agent")
public class UserAiAgentController {
    private final AiAgentService aiAgentService;

    public UserAiAgentController(AiAgentService aiAgentService) {
        this.aiAgentService = aiAgentService;
    }

    @GetMapping("/summary")
    public UserApiResult summary(HttpServletRequest request) {
        Integer userId = AiAgentUserContext.requireUserId(request);
        return UserApiResult.success(aiAgentService.getSummary(userId));
    }

    @PostMapping("/chat")
    public UserApiResult chat(HttpServletRequest request, @RequestBody(required = false) AiChatRequest form) {
        Integer userId = AiAgentUserContext.requireUserId(request);
        return UserApiResult.success(aiAgentService.chat(userId, form));
    }

    @PostMapping("/coupon-plan")
    public UserApiResult couponPlan(HttpServletRequest request, @RequestBody(required = false) AiChatRequest form) {
        Integer userId = AiAgentUserContext.requireUserId(request);
        return UserApiResult.success(aiAgentService.optimizeCoupons(userId, form));
    }

    @GetMapping("/rider")
    public UserApiResult rider(HttpServletRequest request, @RequestParam(required = false) Integer orderId) {
        Integer userId = AiAgentUserContext.requireUserId(request);
        return UserApiResult.success(aiAgentService.getRiderAndEta(userId, orderId));
    }

    @PostMapping("/rider/summon")
    public UserApiResult summonRider(HttpServletRequest request, @RequestBody(required = false) AiChatRequest form) {
        Integer userId = AiAgentUserContext.requireUserId(request);
        Integer orderId = form == null ? null : form.getOrderId();
        return UserApiResult.success(aiAgentService.summonRider(userId, orderId));
    }
}