package com.example.takeoutsystem.entity;

public class UserProfileVO {
    private Integer userId;
    private String username;
    private String realName;
    private String phone;
    private Integer roleType;
    private Integer creditScore;
    private Integer levelId;
    private String levelName;
    private Integer growthValue;
    private Integer status;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getRoleType() { return roleType; }
    public void setRoleType(Integer roleType) { this.roleType = roleType; }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    public Integer getGrowthValue() { return growthValue; }
    public void setGrowthValue(Integer growthValue) { this.growthValue = growthValue; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
