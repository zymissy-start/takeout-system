package com.example.takeoutsystem.entity;

import java.math.BigDecimal;

public class MerchantOrderVO {

    private Integer orderId;
    private String userName;
    private Integer status;
    private BigDecimal totalPrice;
    private String orderTime;
    private String summary;
<<<<<<< HEAD
=======
    private Integer isUrged;
    private Integer reminderCount;
    private String latestReminderTime;
    private Integer riderUrgeCount;
    private String riderUrgeTime;
    private Integer requiredRiderLevel;
    private String requiredRiderTitle;
>>>>>>> origin/feature-user-rider-merchant

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
<<<<<<< HEAD
=======

    public Integer getIsUrged() {
        return isUrged;
    }

    public void setIsUrged(Integer isUrged) {
        this.isUrged = isUrged;
    }

    public Integer getReminderCount() {
        return reminderCount;
    }

    public void setReminderCount(Integer reminderCount) {
        this.reminderCount = reminderCount;
    }

    public String getLatestReminderTime() {
        return latestReminderTime;
    }

    public void setLatestReminderTime(String latestReminderTime) {
        this.latestReminderTime = latestReminderTime;
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

    public Integer getRequiredRiderLevel() {
        return requiredRiderLevel;
    }

    public void setRequiredRiderLevel(Integer requiredRiderLevel) {
        this.requiredRiderLevel = requiredRiderLevel;
    }

    public String getRequiredRiderTitle() {
        return requiredRiderTitle;
    }

    public void setRequiredRiderTitle(String requiredRiderTitle) {
        this.requiredRiderTitle = requiredRiderTitle;
    }

>>>>>>> origin/feature-user-rider-merchant
}