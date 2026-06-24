package com.example.takeoutsystem.service;
import java.math.BigDecimal;
import com.example.takeoutsystem.entity.RiderOrderDetailVO;
import com.example.takeoutsystem.entity.RiderOrderVO;
import com.example.takeoutsystem.entity.SysUser;

import java.util.List;

public interface RiderOrderService {

<<<<<<< HEAD
    List<RiderOrderVO> listAvailableOrders();
=======
    List<RiderOrderVO> listAvailableOrders(Integer riderUserId);
>>>>>>> origin/feature-user-rider-merchant

    List<RiderOrderVO> listMyOrders(Integer riderUserId, Integer status);

    RiderOrderDetailVO getOrderDetail(Integer riderUserId, Integer orderId);

    boolean acceptOrder(SysUser rider, Integer orderId);

    boolean finishOrder(Integer riderUserId, Integer orderId);
    List<RiderOrderVO> listWaitCookingOrders();

<<<<<<< HEAD
    boolean urgeMerchant(Integer orderId);
=======
    boolean urgeMerchant(Integer riderUserId, Integer orderId);
>>>>>>> origin/feature-user-rider-merchant

    boolean addTip(Integer riderUserId, Integer orderId, java.math.BigDecimal tipAmount);
}