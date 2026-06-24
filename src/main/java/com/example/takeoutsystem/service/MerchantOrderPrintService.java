package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.MerchantPrintOrderVO;

/**
 * 商家订单打印业务接口。
 */
public interface MerchantOrderPrintService {

    MerchantPrintOrderVO getPrintOrder(Integer merchantId, Integer orderId);
}