package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.AiAgentSummaryVO;
import com.example.takeoutsystem.entity.AiChatRequest;
import com.example.takeoutsystem.entity.AiChatResponseVO;
import com.example.takeoutsystem.entity.AiCouponPlanVO;
import com.example.takeoutsystem.entity.AiOrderVO;

import java.util.Map;

public interface AiAgentService {
    AiAgentSummaryVO getSummary(Integer userId);

    AiChatResponseVO chat(Integer userId, AiChatRequest request);

    AiCouponPlanVO optimizeCoupons(Integer userId, AiChatRequest request);

    AiOrderVO getRiderAndEta(Integer userId, Integer orderId);

    Map<String, Object> summonRider(Integer userId, Integer orderId);
}