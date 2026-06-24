package com.example.takeoutsystem.entity;

public class UserStatsVO {
    private Integer orderCount;
    private Integer couponCount;
    private Integer addressCount;
    private Integer reviewCount;
    private Integer growthValue;
    private String levelName;
    private Integer creditScore;

    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
    public Integer getCouponCount() { return couponCount; }
    public void setCouponCount(Integer couponCount) { this.couponCount = couponCount; }
    public Integer getAddressCount() { return addressCount; }
    public void setAddressCount(Integer addressCount) { this.addressCount = addressCount; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public Integer getGrowthValue() { return growthValue; }
    public void setGrowthValue(Integer growthValue) { this.growthValue = growthValue; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
}
