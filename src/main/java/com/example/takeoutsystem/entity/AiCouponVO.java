package com.example.takeoutsystem.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AiCouponVO {
    private Integer userCouponId;
    private Integer couponId;
    private String title;
    private BigDecimal amount;
    private BigDecimal minAmount;
    private Integer userCouponStatus;
    private Date endTime;
    private Integer available;
    private String reason;
}