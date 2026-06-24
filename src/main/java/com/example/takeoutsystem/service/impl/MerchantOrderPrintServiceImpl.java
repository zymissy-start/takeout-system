package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.MerchantPrintOrderItemVO;
import com.example.takeoutsystem.entity.MerchantPrintOrderVO;
import com.example.takeoutsystem.mapper.MerchantOrderPrintMapper;
import com.example.takeoutsystem.service.MerchantOrderPrintService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商家订单打印业务实现。
 */
@Service
public class MerchantOrderPrintServiceImpl implements MerchantOrderPrintService {

    private final MerchantOrderPrintMapper merchantOrderPrintMapper;

    public MerchantOrderPrintServiceImpl(MerchantOrderPrintMapper merchantOrderPrintMapper) {
        this.merchantOrderPrintMapper = merchantOrderPrintMapper;
    }

    @Override
    public MerchantPrintOrderVO getPrintOrder(Integer merchantId, Integer orderId) {
        if (merchantId == null || orderId == null) {
            return null;
        }

        MerchantPrintOrderVO order = merchantOrderPrintMapper.findPrintOrder(merchantId, orderId);

        if (order == null) {
            return null;
        }

        List<MerchantPrintOrderItemVO> items = merchantOrderPrintMapper.listPrintItems(orderId);
        order.setItems(items);

        return order;
    }
}