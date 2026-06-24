package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.OrderStatusLogVO;
import com.example.takeoutsystem.entity.UserOrder;
import com.example.takeoutsystem.entity.UserOrderItem;
import com.example.takeoutsystem.entity.UserOrderItemVO;
import com.example.takeoutsystem.entity.UserOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserOrderMapper {
    int insertOrder(UserOrder order);
    int insertOrderItem(UserOrderItem item);
    List<UserOrderVO> selectOrders(@Param("userId") Integer userId, @Param("status") String status);
    UserOrderVO selectOrderById(@Param("orderId") Integer orderId, @Param("userId") Integer userId);
    List<UserOrderItemVO> selectOrderItems(@Param("orderId") Integer orderId);
    int cancelOrder(@Param("orderId") Integer orderId, @Param("userId") Integer userId, @Param("reason") String reason);
    int updateUrgeInfo(@Param("orderId") Integer orderId);
    Date selectLastRemindTime(@Param("orderId") Integer orderId, @Param("userId") Integer userId);
    int insertStatusLog(@Param("orderId") Integer orderId,
                        @Param("status") Integer status,
                        @Param("statusText") String statusText,
                        @Param("operatorType") String operatorType,
                        @Param("operatorId") Integer operatorId,
                        @Param("remark") String remark);
    List<OrderStatusLogVO> selectStatusLogs(@Param("orderId") Integer orderId);
    int insertComment(@Param("orderId") Integer orderId,
                      @Param("userId") Integer userId,
                      @Param("merchantId") Integer merchantId,
                      @Param("score") Integer score,
                      @Param("content") String content);
    int countCommentByOrderId(@Param("orderId") Integer orderId, @Param("userId") Integer userId);
    int countUserOrders(@Param("userId") Integer userId);
    int countUserCoupons(@Param("userId") Integer userId);
    int countUserReviews(@Param("userId") Integer userId);
}
