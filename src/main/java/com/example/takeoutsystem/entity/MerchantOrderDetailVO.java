package com.example.takeoutsystem.entity;

import java.math.BigDecimal;
import java.util.List;

public class MerchantOrderDetailVO {

    private Integer orderId;
    private String userName;
    private String userPhone;
    private Integer userId;
    private Integer riderId;
    private Integer status;
    private BigDecimal totalPrice;
    private String orderTime;
    private String merchantConfirmTime;
    private String kitchenFinishTime;
    private String estimatedArrivalTime;
    private String finishTime;
    private String riderName;
    private String riderPhone;
    private String address;
    private String remark;
    private Integer isUrged;
    private List<MerchantOrderItemVO> items;

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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRiderId() {
        return riderId;
    }

    public void setRiderId(Integer riderId) {
        this.riderId = riderId;
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

    public String getMerchantConfirmTime() {
        return merchantConfirmTime;
    }

    public void setMerchantConfirmTime(String merchantConfirmTime) {
        this.merchantConfirmTime = merchantConfirmTime;
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

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
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

    public List<MerchantOrderItemVO> getItems() {
        return items;
    }

    public void setItems(List<MerchantOrderItemVO> items) {
        this.items = items;
    }
}