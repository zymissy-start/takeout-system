package com.example.takeoutsystem.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AiProductVO {
    private Integer productId;
    private Integer merchantId;
    private String merchantName;
    private Integer categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Integer orderCount;
    private Integer monthlySales;
    private BigDecimal rating;
    private String tag;
    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    private Integer avgDeliveryMinutes;
    private Integer quantity;
    private BigDecimal subtotalAmount;
}