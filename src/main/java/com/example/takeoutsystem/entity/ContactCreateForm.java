package com.example.takeoutsystem.entity;

public class ContactCreateForm {
    private Integer targetId;
    private Integer targetRole;
    private Integer orderId;
    private String title;
    private String content;

    public Integer getTargetId() { return targetId; }
    public void setTargetId(Integer targetId) { this.targetId = targetId; }
    public Integer getTargetRole() { return targetRole; }
    public void setTargetRole(Integer targetRole) { this.targetRole = targetRole; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
