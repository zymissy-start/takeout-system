package com.example.takeoutsystem.entity;

import java.math.BigDecimal;

/**
 * 商家店铺信息修改表单。
 */
public class MerchantShopForm {

    private String storeName;
    private String storeLogo;
    private String storeNotice;
    private BigDecimal minOrderAmount;
    private BigDecimal deliveryFee;
    private Integer deliveryTime;
    private BigDecimal distanceKm;
    private Integer businessStatus;

    /**
     * 联系电话保存到 sys_user.phone。
     */
    private String contactPhone;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getStoreNotice() {
        return storeNotice;
    }

    public void setStoreNotice(String storeNotice) {
        this.storeNotice = storeNotice;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(Integer businessStatus) {
        this.businessStatus = businessStatus;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}