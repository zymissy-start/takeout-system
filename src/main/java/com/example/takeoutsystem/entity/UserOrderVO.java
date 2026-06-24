package com.example.takeoutsystem.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class UserOrderVO {
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
    private BigDecimal payAmount;
    private Integer status;
    private Integer payStatus;
    private Date orderTime;
    private Date merchantConfirmTime;
    private Date kitchenFinishTime;
    private Date estimatedArrivalTime;
    private Date finishTime;
    private String address;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal receiverLatitude;
    private BigDecimal receiverLongitude;
    private String remark;
    private Integer isUrged;
    private Integer remindCount;
    private Date lastRemindTime;
    private String summary;
    private List<UserOrderItemVO> items;
    private List<OrderStatusLogVO> statusLogs;
    private List<OrderReminderVO> reminders;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getMerchantId() { return merchantId; }
    public void setMerchantId(Integer merchantId) { this.merchantId = merchantId; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public Integer getRiderId() { return riderId; }
    public void setRiderId(Integer riderId) { this.riderId = riderId; }
    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }
    public String getRiderPhone() { return riderPhone; }
    public void setRiderPhone(String riderPhone) { this.riderPhone = riderPhone; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public BigDecimal getProductAmount() { return productAmount; }
    public void setProductAmount(BigDecimal productAmount) { this.productAmount = productAmount; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getActualAmount() { return actualAmount; }
    public void setActualAmount(BigDecimal actualAmount) { this.actualAmount = actualAmount; }
    public BigDecimal getPayAmount() { return payAmount; }
    public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPayStatus() { return payStatus; }
    public void setPayStatus(Integer payStatus) { this.payStatus = payStatus; }
    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }
    public Date getMerchantConfirmTime() { return merchantConfirmTime; }
    public void setMerchantConfirmTime(Date merchantConfirmTime) { this.merchantConfirmTime = merchantConfirmTime; }
    public Date getKitchenFinishTime() { return kitchenFinishTime; }
    public void setKitchenFinishTime(Date kitchenFinishTime) { this.kitchenFinishTime = kitchenFinishTime; }
    public Date getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(Date estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }
    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }
    public BigDecimal getReceiverLatitude() { return receiverLatitude; }
    public void setReceiverLatitude(BigDecimal receiverLatitude) { this.receiverLatitude = receiverLatitude; }
    public BigDecimal getReceiverLongitude() { return receiverLongitude; }
    public void setReceiverLongitude(BigDecimal receiverLongitude) { this.receiverLongitude = receiverLongitude; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getIsUrged() { return isUrged; }
    public void setIsUrged(Integer isUrged) { this.isUrged = isUrged; }
    public Integer getRemindCount() { return remindCount; }
    public void setRemindCount(Integer remindCount) { this.remindCount = remindCount; }
    public Date getLastRemindTime() { return lastRemindTime; }
    public void setLastRemindTime(Date lastRemindTime) { this.lastRemindTime = lastRemindTime; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<UserOrderItemVO> getItems() { return items; }
    public void setItems(List<UserOrderItemVO> items) { this.items = items; }
    public List<OrderStatusLogVO> getStatusLogs() { return statusLogs; }
    public void setStatusLogs(List<OrderStatusLogVO> statusLogs) { this.statusLogs = statusLogs; }
    public List<OrderReminderVO> getReminders() { return reminders; }
    public void setReminders(List<OrderReminderVO> reminders) { this.reminders = reminders; }
}
