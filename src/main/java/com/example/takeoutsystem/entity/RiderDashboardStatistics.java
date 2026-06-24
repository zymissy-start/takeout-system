package com.example.takeoutsystem.entity;

import java.math.BigDecimal;

/**
 * 骑手工作台统计数据。
 * 新增骑手等级、奖金、打赏、进度条等字段，用于增强骑手端页面展示。
 */
public class RiderDashboardStatistics {

    private Integer todayFinishedCount;
    private Integer deliveringCount;
    private Integer availableOrderCount;

    private BigDecimal baseIncome;
    private BigDecimal bonusAmount;
    private BigDecimal tipAmount;
    private BigDecimal estimatedIncome;

    private Integer riderStatus;
    private BigDecimal avgSpeed;

    private String riderTitle;
    private String riderTitleDesc;
    private String nextTarget;
    private Integer progressPercent;

    public Integer getTodayFinishedCount() {
        return todayFinishedCount;
    }

    public void setTodayFinishedCount(Integer todayFinishedCount) {
        this.todayFinishedCount = todayFinishedCount;
    }

    public Integer getDeliveringCount() {
        return deliveringCount;
    }

    public void setDeliveringCount(Integer deliveringCount) {
        this.deliveringCount = deliveringCount;
    }

    public Integer getAvailableOrderCount() {
        return availableOrderCount;
    }

    public void setAvailableOrderCount(Integer availableOrderCount) {
        this.availableOrderCount = availableOrderCount;
    }

    public BigDecimal getBaseIncome() {
        return baseIncome;
    }

    public void setBaseIncome(BigDecimal baseIncome) {
        this.baseIncome = baseIncome;
    }

    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public BigDecimal getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(BigDecimal tipAmount) {
        this.tipAmount = tipAmount;
    }

    public BigDecimal getEstimatedIncome() {
        return estimatedIncome;
    }

    public void setEstimatedIncome(BigDecimal estimatedIncome) {
        this.estimatedIncome = estimatedIncome;
    }

    public Integer getRiderStatus() {
        return riderStatus;
    }

    public void setRiderStatus(Integer riderStatus) {
        this.riderStatus = riderStatus;
    }

    public BigDecimal getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(BigDecimal avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getRiderTitle() {
        return riderTitle;
    }

    public void setRiderTitle(String riderTitle) {
        this.riderTitle = riderTitle;
    }

    public String getRiderTitleDesc() {
        return riderTitleDesc;
    }

    public void setRiderTitleDesc(String riderTitleDesc) {
        this.riderTitleDesc = riderTitleDesc;
    }

    public String getNextTarget() {
        return nextTarget;
    }

    public void setNextTarget(String nextTarget) {
        this.nextTarget = nextTarget;
    }

    public Integer getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }
}