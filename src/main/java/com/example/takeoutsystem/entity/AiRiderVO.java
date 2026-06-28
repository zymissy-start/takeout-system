package com.example.takeoutsystem.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AiRiderVO {
    private Integer riderInfoId;
    private Integer userId;
    private String riderName;
    private String phone;
    private Integer status;
    private BigDecimal avgSpeed;
    private Integer totalFinishedCount;
    private Integer riderLevel;
    private String riderTitle;
}