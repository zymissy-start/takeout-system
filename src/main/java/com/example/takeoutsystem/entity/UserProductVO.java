package com.example.takeoutsystem.entity;

import java.math.BigDecimal;

public class UserProductVO {
    private Integer productId;
    private Integer merchantId;
    private String merchantName;
    private Integer categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer orderCount;
    private Integer monthlySales;
    private Integer stock;
    private BigDecimal rating;
    private String tag;
    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    private Integer avgDeliveryMinutes;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getMerchantId() { return merchantId; }
    public void setMerchantId(Integer merchantId) { this.merchantId = merchantId; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
    public Integer getMonthlySales() { return monthlySales; }
    public void setMonthlySales(Integer monthlySales) { this.monthlySales = monthlySales; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public Integer getAvgDeliveryMinutes() { return avgDeliveryMinutes; }
    public void setAvgDeliveryMinutes(Integer avgDeliveryMinutes) { this.avgDeliveryMinutes = avgDeliveryMinutes; }
}
