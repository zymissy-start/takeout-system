package com.example.takeoutsystem.entity;

import java.math.BigDecimal;

public class UserLevelVO {
    private Integer levelId;
    private String levelName;
    private Integer minGrowth;
    private Integer maxGrowth;
    private Integer growthValue;
    private Integer nextMinGrowth;
    private String nextLevelName;
    private Integer nextNeedGrowth;
    private Integer progressPercent;
    private BigDecimal deliveryDiscountRate;
    private Integer remindCooldownSeconds;
    private Integer priorityFlag;
    private String description;

    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    public Integer getMinGrowth() { return minGrowth; }
    public void setMinGrowth(Integer minGrowth) { this.minGrowth = minGrowth; }
    public Integer getMaxGrowth() { return maxGrowth; }
    public void setMaxGrowth(Integer maxGrowth) { this.maxGrowth = maxGrowth; }
    public Integer getGrowthValue() { return growthValue; }
    public void setGrowthValue(Integer growthValue) { this.growthValue = growthValue; }
    public Integer getNextMinGrowth() { return nextMinGrowth; }
    public void setNextMinGrowth(Integer nextMinGrowth) { this.nextMinGrowth = nextMinGrowth; }
    public String getNextLevelName() { return nextLevelName; }
    public void setNextLevelName(String nextLevelName) { this.nextLevelName = nextLevelName; }
    public Integer getNextNeedGrowth() { return nextNeedGrowth; }
    public void setNextNeedGrowth(Integer nextNeedGrowth) { this.nextNeedGrowth = nextNeedGrowth; }
    public Integer getProgressPercent() { return progressPercent; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
    public BigDecimal getDeliveryDiscountRate() { return deliveryDiscountRate; }
    public void setDeliveryDiscountRate(BigDecimal deliveryDiscountRate) { this.deliveryDiscountRate = deliveryDiscountRate; }
    public Integer getRemindCooldownSeconds() { return remindCooldownSeconds; }
    public void setRemindCooldownSeconds(Integer remindCooldownSeconds) { this.remindCooldownSeconds = remindCooldownSeconds; }
    public Integer getPriorityFlag() { return priorityFlag; }
    public void setPriorityFlag(Integer priorityFlag) { this.priorityFlag = priorityFlag; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
