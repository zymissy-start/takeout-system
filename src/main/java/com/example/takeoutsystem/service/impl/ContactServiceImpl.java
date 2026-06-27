package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.*;
import com.example.takeoutsystem.mapper.ContactMapper;
import com.example.takeoutsystem.service.ContactService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactMapper contactMapper;

    public ContactServiceImpl(ContactMapper contactMapper) {
        this.contactMapper = contactMapper;
    }

    @Override
    public ContactSessionVO createSession(Integer initiatorId, Integer initiatorRole, ContactCreateForm form) {
        if (form.getTargetId() == null || form.getTargetRole() == null) {
            throw new IllegalArgumentException("缺少目标用户信息");
        }
        if (form.getContent() == null || form.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }

        // 权限校验：用户只能联系订单相关的商家或骑手
        if (initiatorRole == 1) {
            if (form.getTargetRole() == 2) {
                // 用户联系商家：检查是否有订单关系
                int count = contactMapper.countOrderByUserAndMerchant(initiatorId, form.getTargetId());
                if (count == 0) {
                    throw new IllegalArgumentException("只能联系有订单关系的商家");
                }
            } else if (form.getTargetRole() == 3) {
                // 用户联系骑手：检查是否有订单关系
                int count = contactMapper.countOrderByUserAndRider(initiatorId, form.getTargetId());
                if (count == 0) {
                    throw new IllegalArgumentException("只能联系有订单关系的骑手");
                }
            } else {
                throw new IllegalArgumentException("用户只能联系商家或骑手");
            }
        }

        ContactSession session = new ContactSession();
        session.setInitiatorId(initiatorId);
        session.setInitiatorRole(initiatorRole);
        session.setTargetId(form.getTargetId());
        session.setTargetRole(form.getTargetRole());
        session.setOrderId(form.getOrderId());
        session.setTitle(form.getTitle() != null ? form.getTitle() : "新会话");
        session.setLastMessage(form.getContent());
        contactMapper.insertSession(session);

        // 插入首条消息
        ContactMessage msg = new ContactMessage();
        msg.setSessionId(session.getSessionId());
        msg.setSenderId(initiatorId);
        msg.setSenderRole(initiatorRole);
        msg.setContent(form.getContent());
        contactMapper.insertMessage(msg);

        return contactMapper.selectSessionDetail(session.getSessionId());
    }

    @Override
    public List<ContactSessionVO> listUserSessions(Integer userId, Integer role) {
        return contactMapper.listSessionsByUser(userId, role);
    }

    @Override
    public List<ContactSessionVO> listAdminSessions(Integer targetRole, String keyword) {
        return contactMapper.listSessionsForAdmin(targetRole, keyword);
    }

    @Override
    public Map<String, Object> getSessionDetail(Integer sessionId, Integer viewerId, Integer viewerRole) {
        ContactSessionVO session = contactMapper.selectSessionDetail(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在");
        }

        // 权限校验：查看者必须是会话的发起方或目标方
        boolean isInitiator = session.getInitiatorId().equals(viewerId) && session.getInitiatorRole().equals(viewerRole);
        boolean isTarget = session.getTargetId().equals(viewerId) && session.getTargetRole().equals(viewerRole);
        if (!isInitiator && !isTarget) {
            throw new IllegalArgumentException("无权查看此会话");
        }

        // 标记对方消息为已读
        contactMapper.markMessagesRead(sessionId, viewerRole);

        List<ContactMessageVO> messages = contactMapper.listMessages(sessionId);

        Map<String, Object> result = new HashMap<>();
        result.put("session", session);
        result.put("messages", messages);
        return result;
    }

    @Override
    public ContactMessageVO sendMessage(Integer sessionId, Integer senderId, Integer senderRole, String content) {
        ContactSessionVO session = contactMapper.selectSessionDetail(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在");
        }
        if (session.getStatus() != null && session.getStatus() == 2) {
            throw new IllegalArgumentException("会话已关闭");
        }

        // 权限校验
        boolean isInitiator = session.getInitiatorId().equals(senderId) && session.getInitiatorRole().equals(senderRole);
        boolean isTarget = session.getTargetId().equals(senderId) && session.getTargetRole().equals(senderRole);
        if (!isInitiator && !isTarget) {
            throw new IllegalArgumentException("无权在此会话中发言");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }

        ContactMessage msg = new ContactMessage();
        msg.setSessionId(sessionId);
        msg.setSenderId(senderId);
        msg.setSenderRole(senderRole);
        msg.setContent(content.trim());
        contactMapper.insertMessage(msg);

        // 更新会话最后消息
        String preview = content.trim();
        if (preview.length() > 500) {
            preview = preview.substring(0, 500);
        }
        contactMapper.touchSession(sessionId, preview);

        // 返回新消息的 VO
        List<ContactMessageVO> messages = contactMapper.listMessages(sessionId);
        if (messages != null && !messages.isEmpty()) {
            return messages.get(messages.size() - 1);
        }
        return null;
    }

    @Override
    public void closeSession(Integer sessionId, Integer closerId, Integer closerRole) {
        ContactSessionVO session = contactMapper.selectSessionDetail(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在");
        }

        boolean isInitiator = session.getInitiatorId().equals(closerId) && session.getInitiatorRole().equals(closerRole);
        boolean isTarget = session.getTargetId().equals(closerId) && session.getTargetRole().equals(closerRole);
        if (!isInitiator && !isTarget) {
            throw new IllegalArgumentException("无权关闭此会话");
        }

        contactMapper.updateSessionStatus(sessionId, 2);
    }
}
