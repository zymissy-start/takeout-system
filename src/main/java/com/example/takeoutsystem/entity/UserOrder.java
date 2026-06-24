package com.example.takeoutsystem.entity;

import java.math.BigDecimal;
import java.util.Date;

public class UserOrder {
    private Integer orderId;
    private String orderNo;
    private Integer userId;
    private Integer merchantId;
    private Integer riderId;
    private BigDecimal totalPrice;
    private BigDecimal productAmount;
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private BigDecimal tipAmount;
    private Integer requiredRiderLevel;
    private String requiredRiderTitle;
    private Integer status;
    private Integer payStatus;
    private Integer addressId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal receiverLatitude;
    private BigDecimal receiverLongitude;
    private String address;
    private String remark;
    private Integer isUrged;
    private Date orderTime;

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getMerchantId() { return merchantId; }
    public void setMerchantId(Integer merchantId) { this.merchantId = merchantId; }
    public Integer getRiderId() { return riderId; }
    public void setRiderId(Integer riderId) { this.riderId = riderId; }
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
    public BigDecimal getTipAmount() { return tipAmount; }
    public void setTipAmount(BigDecimal tipAmount) { this.tipAmount = tipAmount; }
    public Integer getRequiredRiderLevel() { return requiredRiderLevel; }
    public void setRequiredRiderLevel(Integer requiredRiderLevel) { this.requiredRiderLevel = requiredRiderLevel; }
    public String getRequiredRiderTitle() { return requiredRiderTitle; }
    public void setRequiredRiderTitle(String requiredRiderTitle) { this.requiredRiderTitle = requiredRiderTitle; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPayStatus() { return payStatus; }
    public void setPayStatus(Integer payStatus) { this.payStatus = payStatus; }
    public Integer getAddressId() { return addressId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }
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
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getIsUrged() { return isUrged; }
    public void setIsUrged(Integer isUrged) { this.isUrged = isUrged; }
    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }
}
