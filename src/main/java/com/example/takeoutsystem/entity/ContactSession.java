package com.example.takeoutsystem.entity;

import java.time.LocalDateTime;

public class ContactSession {
    private Integer sessionId;
    private Integer initiatorId;
    private Integer initiatorRole;
    private Integer targetId;
    private Integer targetRole;
    private Integer orderId;
    private String title;
    private Integer status;
    private String lastMessage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }
    public Integer getInitiatorId() { return initiatorId; }
    public void setInitiatorId(Integer initiatorId) { this.initiatorId = initiatorId; }
    public Integer getInitiatorRole() { return initiatorRole; }
    public void setInitiatorRole(Integer initiatorRole) { this.initiatorRole = initiatorRole; }
    public Integer getTargetId() { return targetId; }
    public void setTargetId(Integer targetId) { this.targetId = targetId; }
    public Integer getTargetRole() { return targetRole; }
    public void setTargetRole(Integer targetRole) { this.targetRole = targetRole; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
