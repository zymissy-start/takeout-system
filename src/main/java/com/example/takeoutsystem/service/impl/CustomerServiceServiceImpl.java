package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.CustomerServiceCreateForm;
import com.example.takeoutsystem.entity.CustomerServiceMessage;
import com.example.takeoutsystem.entity.CustomerServiceReplyForm;
import com.example.takeoutsystem.entity.CustomerServiceTicket;
import com.example.takeoutsystem.entity.CustomerServiceTicketVO;
import com.example.takeoutsystem.mapper.CustomerServiceMapper;
import com.example.takeoutsystem.service.CustomerServiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceServiceImpl implements CustomerServiceService {
    private static final int ROLE_USER = 1;
    private static final int ROLE_ADMIN = 4;

    private final CustomerServiceMapper customerServiceMapper;

    public CustomerServiceServiceImpl(CustomerServiceMapper customerServiceMapper) {
        this.customerServiceMapper = customerServiceMapper;
    }

    @Override
    @Transactional
    public CustomerServiceTicketVO createTicket(Integer userId, CustomerServiceCreateForm form) {
        if (form == null) throw new IllegalArgumentException("请填写客服问题");
        String type = clean(form.getType());
        String title = clean(form.getTitle());
        String content = clean(form.getContent());

        if (type.isEmpty()) type = "其他问题";
        if (title.isEmpty()) throw new IllegalArgumentException("请填写工单标题");
        if (content.isEmpty()) throw new IllegalArgumentException("请填写问题描述");
        if (title.length() > 100) throw new IllegalArgumentException("工单标题不能超过100个字");
        if (content.length() > 2000) throw new IllegalArgumentException("消息内容不能超过2000个字");

        Integer orderId = form.getOrderId();
        if (orderId != null && customerServiceMapper.countOrderBelongsToUser(orderId, userId) == 0) {
            throw new IllegalArgumentException("只能关联自己的订单");
        }

        CustomerServiceTicket ticket = new CustomerServiceTicket();
        ticket.setUserId(userId);
        ticket.setOrderId(orderId);
        ticket.setType(type);
        ticket.setTitle(title);
        ticket.setStatus(0);
        ticket.setPriority(0);
        customerServiceMapper.insertTicket(ticket);

        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setTicketId(ticket.getTicketId());
        message.setSenderId(userId);
        message.setSenderRole(ROLE_USER);
        message.setContent(content);
        message.setContentType("TEXT");
        message.setIsRead(0);
        customerServiceMapper.insertMessage(message);
        customerServiceMapper.touchTicket(ticket.getTicketId());

        return customerServiceMapper.selectTicketByUser(ticket.getTicketId(), userId);
    }

    @Override
    public List<CustomerServiceTicketVO> listUserTickets(Integer userId, Integer status) {
        return customerServiceMapper.listTicketsByUser(userId, normalizeStatus(status, true));
    }

    @Override
    public Map<String, Object> getUserTicketDetail(Integer userId, Integer ticketId) {
        CustomerServiceTicketVO ticket = customerServiceMapper.selectTicketByUser(ticketId, userId);
        if (ticket == null) throw new IllegalArgumentException("工单不存在或无权访问");
        customerServiceMapper.markMessagesRead(ticketId, ROLE_USER);
        return detail(ticket, ticketId);
    }

    @Override
    @Transactional
    public Map<String, Object> replyByUser(Integer userId, Integer ticketId, CustomerServiceReplyForm form) {
        CustomerServiceTicketVO ticket = customerServiceMapper.selectTicketByUser(ticketId, userId);
        if (ticket == null) throw new IllegalArgumentException("工单不存在或无权访问");
        if (ticket.getStatus() != null && ticket.getStatus() == 3) throw new IllegalArgumentException("工单已关闭，不能继续回复");
        addMessage(ticketId, userId, ROLE_USER, form);
        customerServiceMapper.touchTicket(ticketId);
        return getUserTicketDetail(userId, ticketId);
    }

    @Override
    @Transactional
    public void closeByUser(Integer userId, Integer ticketId) {
        CustomerServiceTicketVO ticket = customerServiceMapper.selectTicketByUser(ticketId, userId);
        if (ticket == null) throw new IllegalArgumentException("工单不存在或无权访问");
        customerServiceMapper.updateTicketStatus(ticketId, 3, ticket.getAdminId());
    }

    @Override
    public List<CustomerServiceTicketVO> listAdminTickets(Integer status, String keyword) {
        return customerServiceMapper.listTicketsForAdmin(normalizeStatus(status, true), clean(keyword));
    }

    @Override
    @Transactional
    public Map<String, Object> getAdminTicketDetail(Integer adminId, Integer ticketId) {
        CustomerServiceTicketVO ticket = customerServiceMapper.selectTicketForAdmin(ticketId);
        if (ticket == null) throw new IllegalArgumentException("工单不存在");
        customerServiceMapper.addTicketAdminIfEmpty(ticketId, adminId);
        customerServiceMapper.markMessagesRead(ticketId, ROLE_ADMIN);
        ticket = customerServiceMapper.selectTicketForAdmin(ticketId);
        return detail(ticket, ticketId);
    }

    @Override
    @Transactional
    public Map<String, Object> replyByAdmin(Integer adminId, Integer ticketId, CustomerServiceReplyForm form) {
        CustomerServiceTicketVO ticket = customerServiceMapper.selectTicketForAdmin(ticketId);
        if (ticket == null) throw new IllegalArgumentException("工单不存在");
        if (ticket.getStatus() != null && ticket.getStatus() == 3) throw new IllegalArgumentException("工单已关闭，不能继续回复");
        customerServiceMapper.addTicketAdminIfEmpty(ticketId, adminId);
        addMessage(ticketId, adminId, ROLE_ADMIN, form);
        customerServiceMapper.updateTicketStatus(ticketId, 1, adminId);
        return getAdminTicketDetail(adminId, ticketId);
    }

    @Override
    @Transactional
    public void updateStatusByAdmin(Integer adminId, Integer ticketId, Integer status) {
        Integer targetStatus = normalizeStatus(status, false);
        CustomerServiceTicketVO ticket = customerServiceMapper.selectTicketForAdmin(ticketId);
        if (ticket == null) throw new IllegalArgumentException("工单不存在");
        customerServiceMapper.updateTicketStatus(ticketId, targetStatus, adminId);
    }

    private void addMessage(Integer ticketId, Integer senderId, Integer senderRole, CustomerServiceReplyForm form) {
        if (form == null) throw new IllegalArgumentException("请填写回复内容");
        String content = clean(form.getContent());
        if (content.isEmpty()) throw new IllegalArgumentException("请填写回复内容");
        if (content.length() > 2000) throw new IllegalArgumentException("消息内容不能超过2000个字");

        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setTicketId(ticketId);
        message.setSenderId(senderId);
        message.setSenderRole(senderRole);
        message.setContent(content);
        message.setContentType("TEXT");
        message.setIsRead(0);
        customerServiceMapper.insertMessage(message);
    }

    private Map<String, Object> detail(CustomerServiceTicketVO ticket, Integer ticketId) {
        Map<String, Object> data = new HashMap<>();
        data.put("ticket", ticket);
        data.put("messages", customerServiceMapper.listMessages(ticketId));
        return data;
    }

    private Integer normalizeStatus(Integer status, boolean allowNull) {
        if (status == null) return allowNull ? null : 0;
        if (status < 0) return allowNull ? null : 0;
        if (status > 3) throw new IllegalArgumentException("工单状态不正确");
        return status;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
