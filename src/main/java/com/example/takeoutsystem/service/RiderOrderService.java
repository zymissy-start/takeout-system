package com.example.takeoutsystem.service;
import java.math.BigDecimal;
import com.example.takeoutsystem.entity.RiderOrderDetailVO;
import com.example.takeoutsystem.entity.RiderOrderVO;
import com.example.takeoutsystem.entity.SysUser;

import java.util.List;

public interface RiderOrderService {

    List<RiderOrderVO> listAvailableOrders(Integer riderUserId);

    List<RiderOrderVO> listMyOrders(Integer riderUserId, Integer status);

    RiderOrderDetailVO getOrderDetail(Integer riderUserId, Integer orderId);

    boolean acceptOrder(SysUser rider, Integer orderId);

    boolean finishOrder(Integer riderUserId, Integer orderId);
    List<RiderOrderVO> listWaitCookingOrders();

    boolean urgeMerchant(Integer riderUserId, Integer orderId);

    boolean addTip(Integer riderUserId, Integer orderId, java.math.BigDecimal tipAmount);
}