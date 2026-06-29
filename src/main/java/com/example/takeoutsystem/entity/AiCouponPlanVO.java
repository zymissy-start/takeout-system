package com.example.takeoutsystem.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class AiCouponPlanVO {
    private BigDecimal productAmount = BigDecimal.ZERO;
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    private BigDecimal couponDiscount = BigDecimal.ZERO;
    private BigDecimal payAmount = BigDecimal.ZERO;
    private AiCouponVO bestCoupon;
    private List<AiCouponVO> availableCoupons = new ArrayList<>();
    private List<AiProductVO> cartItems = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();
}