package com.example.takeoutsystem.entity;

public class MerchantDashboardStatistics {

    private Integer waitAcceptCount;
    private Integer cookingCount;
    private Integer waitRiderCount;
    private Integer finishedCount;

    public Integer getWaitAcceptCount() {
        return waitAcceptCount;
    }

    public void setWaitAcceptCount(Integer waitAcceptCount) {
        this.waitAcceptCount = waitAcceptCount;
    }

    public Integer getCookingCount() {
        return cookingCount;
    }

    public void setCookingCount(Integer cookingCount) {
        this.cookingCount = cookingCount;
    }

    public Integer getWaitRiderCount() {
        return waitRiderCount;
    }

    public void setWaitRiderCount(Integer waitRiderCount) {
        this.waitRiderCount = waitRiderCount;
    }

    public Integer getFinishedCount() {
        return finishedCount;
    }

    public void setFinishedCount(Integer finishedCount) {
        this.finishedCount = finishedCount;
    }
}