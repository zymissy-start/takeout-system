package com.example.takeoutsystem.entity;

import java.math.BigDecimal;
import java.util.Date;

public class UserAddress {
    private Integer addressId;
    private Integer userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String addressDetail;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String tag;
    private Integer isDefault;
    private Date createTime;
    private Date updateTime;

    public Integer getAddressId() { return addressId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
