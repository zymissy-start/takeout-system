package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.OrderReminderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserReminderMapper {
    int insertReminder(@Param("orderId") Integer orderId,
                       @Param("userId") Integer userId,
                       @Param("targetType") String targetType,
                       @Param("targetId") Integer targetId,
                       @Param("content") String content);

    List<OrderReminderVO> selectByOrderId(@Param("orderId") Integer orderId);
}
