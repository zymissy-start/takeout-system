package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.MerchantOrderDetailVO;
import com.example.takeoutsystem.entity.MerchantOrderVO;

import java.util.List;

public interface MerchantOrderService {

    List<MerchantOrderVO> listRecentOrders(Integer merchantId, Integer size);

    List<MerchantOrderVO> listOrders(Integer merchantId, Integer status);

    MerchantOrderDetailVO getOrderDetail(Integer merchantId, Integer orderId);

    boolean acceptOrder(Integer merchantId, Integer orderId);

    boolean finishCooking(Integer merchantId, Integer orderId);

    boolean callRider(Integer merchantId, Integer orderId);
}