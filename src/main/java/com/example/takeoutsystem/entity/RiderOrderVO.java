package com.example.takeoutsystem.entity;

import java.math.BigDecimal;

/**
 * 骑手端订单列表展示对象。
 * 主要用于可接订单大厅和我的配送订单列表。
 */
public class RiderOrderVO {

    private Integer orderId;
    private String userName;
    private String merchantName;
    private Integer status;
    private BigDecimal totalPrice;
    private String orderTime;
    private String kitchenFinishTime;
    private String estimatedArrivalTime;
    private String address;
    private String remark;
    private Integer isUrged;
    private Integer waitMinutes;
    private String summary;
    private java.math.BigDecimal tipAmount;
<<<<<<< HEAD
    private Integer riderUrgeCount;
    private String riderUrgeTime;
=======
    private Integer requiredRiderLevel;
    private String requiredRiderTitle;
    private Integer riderUrgeCount;
    private String riderUrgeTime;
    public Integer getRequiredRiderLevel() { return requiredRiderLevel; }

    public void setRequiredRiderLevel(Integer requiredRiderLevel) { this.requiredRiderLevel = requiredRiderLevel; }

    public String getRequiredRiderTitle() { return requiredRiderTitle; }

    public void setRequiredRiderTitle(String requiredRiderTitle) { this.requiredRiderTitle = requiredRiderTitle; }

>>>>>>> origin/feature-user-rider-merchant
    public java.math.BigDecimal getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(java.math.BigDecimal tipAmount) {
        this.tipAmount = tipAmount;
    }

    public Integer getRiderUrgeCount() {
        return riderUrgeCount;
    }

    public void setRiderUrgeCount(Integer riderUrgeCount) {
        this.riderUrgeCount = riderUrgeCount;
    }

    public String getRiderUrgeTime() {
        return riderUrgeTime;
    }

    public void setRiderUrgeTime(String riderUrgeTime) {
        this.riderUrgeTime = riderUrgeTime;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getKitchenFinishTime() {
        return kitchenFinishTime;
    }

    public void setKitchenFinishTime(String kitchenFinishTime) {
        this.kitchenFinishTime = kitchenFinishTime;
    }

    public String getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(String estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsUrged() {
        return isUrged;
    }

    public void setIsUrged(Integer isUrged) {
        this.isUrged = isUrged;
    }

    public Integer getWaitMinutes() {
        return waitMinutes;
    }

    public void setWaitMinutes(Integer waitMinutes) {
        this.waitMinutes = waitMinutes;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}