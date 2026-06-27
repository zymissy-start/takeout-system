package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.ContactCreateForm;
import com.example.takeoutsystem.entity.ContactMessageVO;
import com.example.takeoutsystem.entity.ContactSessionVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ContactService {

    ContactSessionVO createSession(Integer initiatorId, Integer initiatorRole, ContactCreateForm form);

    List<ContactSessionVO> listUserSessions(Integer userId, Integer role);

    List<ContactSessionVO> listAdminSessions(Integer targetRole, String keyword);

    Map<String, Object> getSessionDetail(Integer sessionId, Integer viewerId, Integer viewerRole);

    ContactMessageVO sendMessage(Integer sessionId, Integer senderId, Integer senderRole, String content);

    void closeSession(Integer sessionId, Integer closerId, Integer closerRole);
}
