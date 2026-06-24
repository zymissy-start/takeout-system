package com.example.takeoutsystem.entity;

import java.math.BigDecimal;

/**
 * 用户端商店列表视图。首页先展示商店，用户进入商店后再浏览该商店商品。
 */
public class UserMerchantVO {
    private Integer merchantId;
    private String storeName;
    private String storeLogo;
    private String storeNotice;
    private BigDecimal rating;
    private Integer monthlySales;
    private BigDecimal minOrderAmount;
    private BigDecimal deliveryFee;
    private Integer deliveryTime;
    private BigDecimal distanceKm;
    private Integer status;
    private Integer productCount;
    private BigDecimal lowestPrice;
    private String topProductNames;

    public Integer getMerchantId() { return merchantId; }
    public void setMerchantId(Integer merchantId) { this.merchantId = merchantId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getStoreLogo() { return storeLogo; }
    public void setStoreLogo(String storeLogo) { this.storeLogo = storeLogo; }
    public String getStoreNotice() { return storeNotice; }
    public void setStoreNotice(String storeNotice) { this.storeNotice = storeNotice; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Integer getMonthlySales() { return monthlySales; }
    public void setMonthlySales(Integer monthlySales) { this.monthlySales = monthlySales; }
    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    public Integer getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(Integer deliveryTime) { this.deliveryTime = deliveryTime; }
    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }
    public BigDecimal getLowestPrice() { return lowestPrice; }
    public void setLowestPrice(BigDecimal lowestPrice) { this.lowestPrice = lowestPrice; }
    public String getTopProductNames() { return topProductNames; }
    public void setTopProductNames(String topProductNames) { this.topProductNames = topProductNames; }
}
