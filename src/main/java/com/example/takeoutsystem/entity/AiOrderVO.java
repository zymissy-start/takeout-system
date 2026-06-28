package com.example.takeoutsystem.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class AiOrderVO {
    private Integer orderId;
    private String orderNo;
    private Integer userId;
    private Integer merchantId;
    private String merchantName;
    private Integer riderId;
    private String riderName;
    private String riderPhone;
    private BigDecimal totalPrice;
    private BigDecimal productAmount;
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private Integer status;
    private String statusText;
    private Date orderTime;
    private Date merchantConfirmTime;
    private Date kitchenFinishTime;
    private Date estimatedArrivalTime;
    private Date finishTime;
    private String address;
    private Integer requiredRiderLevel;
    private String requiredRiderTitle;
    private Integer predictedMinutes;
    private String etaText;
    private List<AiProductVO> items;
}