package com.example.takeoutsystem.entity;

import java.util.Date;

public class OrderReminderVO {
    private Integer reminderId;
    private Integer orderId;
    private Integer userId;
    private String targetType;
    private Integer targetId;
    private String content;
    private String status;
    private Date createTime;
    private Date readTime;
    private Date handledTime;

    public Integer getReminderId() { return reminderId; }
    public void setReminderId(Integer reminderId) { this.reminderId = reminderId; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Integer getTargetId() { return targetId; }
    public void setTargetId(Integer targetId) { this.targetId = targetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getReadTime() { return readTime; }
    public void setReadTime(Date readTime) { this.readTime = readTime; }
    public Date getHandledTime() { return handledTime; }
    public void setHandledTime(Date handledTime) { this.handledTime = handledTime; }
}
