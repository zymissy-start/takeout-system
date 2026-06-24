package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.UserCommentForm;
import com.example.takeoutsystem.entity.UserOrderCreateForm;
import com.example.takeoutsystem.entity.UserOrderVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserOrderService {
    UserOrderVO createOrder(Integer userId, UserOrderCreateForm form);
    List<UserOrderVO> listOrders(Integer userId, String status);
    UserOrderVO getDetail(Integer userId, Integer orderId);
    void cancel(Integer userId, Integer orderId, String reason);
    Map<String, Object> urge(Integer userId, Integer orderId);
    Map<String, Object> tip(Integer userId, Integer orderId, BigDecimal tipAmount);
    void comment(Integer userId, Integer orderId, UserCommentForm form);
}
