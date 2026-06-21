package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.MerchantOrderDetailVO;
import com.example.takeoutsystem.entity.MerchantOrderItemVO;
import com.example.takeoutsystem.entity.MerchantOrderVO;
import com.example.takeoutsystem.mapper.MerchantOrderMapper;
import com.example.takeoutsystem.service.MerchantOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantOrderServiceImpl implements MerchantOrderService {

    private final MerchantOrderMapper merchantOrderMapper;

    public MerchantOrderServiceImpl(MerchantOrderMapper merchantOrderMapper) {
        this.merchantOrderMapper = merchantOrderMapper;
    }

    @Override
    public List<MerchantOrderVO> listRecentOrders(Integer merchantId, Integer size) {
        if (size == null || size <= 0) {
            size = 5;
        }

        if (size > 50) {
            size = 50;
        }

        return merchantOrderMapper.listRecentOrders(merchantId, size);
    }

    @Override
    public List<MerchantOrderVO> listOrders(Integer merchantId, Integer status) {
        if (status != null && status < 0) {
            status = null;
        }

        if (status != null && status > 4) {
            status = null;
        }

        return merchantOrderMapper.listOrders(merchantId, status);
    }

    @Override
    public MerchantOrderDetailVO getOrderDetail(Integer merchantId, Integer orderId) {
        if (orderId == null) {
            return null;
        }

        MerchantOrderDetailVO detail = merchantOrderMapper.getOrderDetail(merchantId, orderId);

        if (detail == null) {
            return null;
        }

        List<MerchantOrderItemVO> items = merchantOrderMapper.listOrderItems(orderId);
        detail.setItems(items);

        return detail;
    }

    @Override
    public boolean acceptOrder(Integer merchantId, Integer orderId) {
        if (orderId == null) {
            return false;
        }

        return merchantOrderMapper.acceptOrder(merchantId, orderId) > 0;
    }

    @Override
    public boolean finishCooking(Integer merchantId, Integer orderId) {
        if (orderId == null) {
            return false;
        }

        return merchantOrderMapper.finishCooking(merchantId, orderId) > 0;
    }

    @Override
    public boolean callRider(Integer merchantId, Integer orderId) {
        if (orderId == null) {
            return false;
        }

        return merchantOrderMapper.callRider(merchantId, orderId) > 0;
    }
}