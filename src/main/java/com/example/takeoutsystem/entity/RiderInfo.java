package com.example.takeoutsystem.entity;

import java.math.BigDecimal;

/**
 * 骑手扩展信息实体类。
 * 对应数据库 rider_info 表，并额外封装 sys_user 表中的账号、姓名、电话等信息。
 */
public class RiderInfo {

    private Integer riderId;
    private Integer userId;
    private String username;
    private String realName;
    private String phone;
    private Integer isFullTime;
    private Integer status;
    private BigDecimal avgSpeed;

    public Integer getRiderId() {
        return riderId;
    }

    public void setRiderId(Integer riderId) {
        this.riderId = riderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getIsFullTime() {
        return isFullTime;
    }

    public void setIsFullTime(Integer isFullTime) {
        this.isFullTime = isFullTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(BigDecimal avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}