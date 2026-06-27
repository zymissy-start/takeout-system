package com.example.takeoutsystem.entity;

import java.time.LocalDateTime;

public class ContactMessage {
    private Integer msgId;
    private Integer sessionId;
    private Integer senderId;
    private Integer senderRole;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;

    public Integer getMsgId() { return msgId; }
    public void setMsgId(Integer msgId) { this.msgId = msgId; }
    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }
    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }
    public Integer getSenderRole() { return senderRole; }
    public void setSenderRole(Integer senderRole) { this.senderRole = senderRole; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
