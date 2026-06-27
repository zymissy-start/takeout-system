package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.ContactMessage;
import com.example.takeoutsystem.entity.ContactMessageVO;
import com.example.takeoutsystem.entity.ContactSession;
import com.example.takeoutsystem.entity.ContactSessionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContactMapper {

    int insertSession(ContactSession session);

    int insertMessage(ContactMessage message);

    List<ContactSessionVO> listSessionsByUser(@Param("userId") Integer userId, @Param("role") Integer role);

    List<ContactSessionVO> listSessionsForAdmin(@Param("targetRole") Integer targetRole, @Param("keyword") String keyword);

    ContactSessionVO selectSessionDetail(@Param("sessionId") Integer sessionId);

    List<ContactMessageVO> listMessages(@Param("sessionId") Integer sessionId);

    int markMessagesRead(@Param("sessionId") Integer sessionId, @Param("readerRole") Integer readerRole);

    int updateSessionStatus(@Param("sessionId") Integer sessionId, @Param("status") Integer status);

    int touchSession(@Param("sessionId") Integer sessionId, @Param("lastMessage") String lastMessage);

    int countOrderByUserAndMerchant(@Param("userId") Integer userId, @Param("merchantId") Integer merchantId);

    int countOrderByUserAndRider(@Param("userId") Integer userId, @Param("riderId") Integer riderId);
}
