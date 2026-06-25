package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.CustomerServiceCreateForm;
import com.example.takeoutsystem.entity.CustomerServiceReplyForm;
import com.example.takeoutsystem.entity.CustomerServiceTicketVO;

import java.util.List;
import java.util.Map;

public interface CustomerServiceService {
    CustomerServiceTicketVO createTicket(Integer userId, CustomerServiceCreateForm form);

    List<CustomerServiceTicketVO> listUserTickets(Integer userId, Integer status);

    Map<String, Object> getUserTicketDetail(Integer userId, Integer ticketId);

    Map<String, Object> replyByUser(Integer userId, Integer ticketId, CustomerServiceReplyForm form);

    void closeByUser(Integer userId, Integer ticketId);

    List<CustomerServiceTicketVO> listAdminTickets(Integer status, String keyword);

    Map<String, Object> getAdminTicketDetail(Integer adminId, Integer ticketId);

    Map<String, Object> replyByAdmin(Integer adminId, Integer ticketId, CustomerServiceReplyForm form);

    void updateStatusByAdmin(Integer adminId, Integer ticketId, Integer status);
}
