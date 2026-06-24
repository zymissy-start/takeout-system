package com.example.takeoutsystem.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 骑手端订单详情对象。
 * 包含订单基础信息、配送信息、时间节点和商品明细。
 */
public class RiderOrderDetailVO {

    private Integer orderId;
    private String userName;
    private String merchantName;
    private Integer status;
    private BigDecimal totalPrice;
    private String orderTime;
    private String kitchenFinishTime;
    private String estimatedArrivalTime;
    private String finishTime;
    private String address;
    private String remark;
    private Integer isUrged;
    private List<RiderOrderItemVO> items;

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

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
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

    public List<RiderOrderItemVO> getItems() {
        return items;
    }

    public void setItems(List<RiderOrderItemVO> items) {
        this.items = items;
    }
}