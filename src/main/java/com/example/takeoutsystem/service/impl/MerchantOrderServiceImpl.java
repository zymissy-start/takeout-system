package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.MerchantOrderDetailVO;
import com.example.takeoutsystem.entity.MerchantOrderItemVO;
import com.example.takeoutsystem.entity.MerchantOrderVO;
import com.example.takeoutsystem.mapper.MerchantOrderMapper;
import com.example.takeoutsystem.service.MerchantOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptOrder(Integer merchantId, Integer orderId) {
        if (orderId == null) {
            return false;
        }
        int rows = merchantOrderMapper.acceptOrder(merchantId, orderId);
        if (rows <= 0) {
            return false;
        }
        merchantOrderMapper.handleMerchantReminders(merchantId, orderId);
        merchantOrderMapper.insertStatusLog(merchantId, orderId, 1, "商家已接单", "商家确认接单，开始制作");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean finishCooking(Integer merchantId, Integer orderId) {
        if (orderId == null) {
            return false;
        }
        int rows = merchantOrderMapper.finishCooking(merchantId, orderId);
        if (rows <= 0) {
            return false;
        }
        merchantOrderMapper.handleMerchantReminders(merchantId, orderId);
        merchantOrderMapper.insertStatusLog(merchantId, orderId, 2, "商家已出餐", "订单进入骑手接单池，等待匹配骑手");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean callRider(Integer merchantId, Integer orderId) {
        if (orderId == null) {
            return false;
        }
        if (merchantOrderMapper.countWaitRiderOrder(merchantId, orderId) <= 0) {
            return false;
        }
        merchantOrderMapper.insertStatusLog(merchantId, orderId, 2, "商家已召唤骑手", "订单已在骑手接单池中，按用户等级匹配骑手");
        return true;
    }
}