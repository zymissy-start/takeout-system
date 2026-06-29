package com.example.takeoutsystem.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiAgentSummaryVO {
    private Boolean deepSeekEnabled;
    private String welcomeText;
    private List<AiProductVO> recommendations = new ArrayList<>();
    private List<AiProductVO> historyProducts = new ArrayList<>();
    private List<AiCouponVO> availableCoupons = new ArrayList<>();
    private AiOrderVO activeOrder;
    private AiCouponPlanVO couponPlan;
}