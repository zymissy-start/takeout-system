package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.CustomerServiceMessage;
import com.example.takeoutsystem.entity.CustomerServiceMessageVO;
import com.example.takeoutsystem.entity.CustomerServiceTicket;
import com.example.takeoutsystem.entity.CustomerServiceTicketVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerServiceMapper {
    int insertTicket(CustomerServiceTicket ticket);

    int insertMessage(CustomerServiceMessage message);

    List<CustomerServiceTicketVO> listTicketsByUser(@Param("userId") Integer userId,
                                                    @Param("status") Integer status);

    List<CustomerServiceTicketVO> listTicketsForAdmin(@Param("status") Integer status,
                                                      @Param("keyword") String keyword);

    CustomerServiceTicketVO selectTicketByUser(@Param("ticketId") Integer ticketId,
                                               @Param("userId") Integer userId);

    CustomerServiceTicketVO selectTicketForAdmin(@Param("ticketId") Integer ticketId);

    List<CustomerServiceMessageVO> listMessages(@Param("ticketId") Integer ticketId);

    int countOrderBelongsToUser(@Param("orderId") Integer orderId,
                                @Param("userId") Integer userId);

    int addTicketAdminIfEmpty(@Param("ticketId") Integer ticketId,
                              @Param("adminId") Integer adminId);

    int updateTicketStatus(@Param("ticketId") Integer ticketId,
                           @Param("status") Integer status,
                           @Param("adminId") Integer adminId);

    int touchTicket(@Param("ticketId") Integer ticketId);

    int markMessagesRead(@Param("ticketId") Integer ticketId,
                         @Param("readerRole") Integer readerRole);
}
