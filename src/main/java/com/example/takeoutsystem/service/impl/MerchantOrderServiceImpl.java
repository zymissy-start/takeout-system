package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.MerchantOrderDetailVO;
import com.example.takeoutsystem.entity.MerchantOrderItemVO;
import com.example.takeoutsystem.entity.MerchantOrderVO;
import com.example.takeoutsystem.mapper.MerchantOrderMapper;
import com.example.takeoutsystem.service.MerchantOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * 商家订单业务逻辑实现类。
 *
 * Service 层负责业务规则校验和 Mapper 方法组装。
 * Controller 不直接访问数据库，而是通过 Service 调用 Mapper，
 * 体现了五层架构中的职责分离。
 */
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
    /**
     * 查询订单列表。
     * status 为空时查询全部订单；status 为 0-4 时按状态筛选。
     * 非法状态值会被置空，防止前端传入异常参数影响查询。
     */
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
    /**
     * 查询订单详情。
     *
     * 订单详情由两部分组成：
     * 1. 订单主信息：来自 delivery_order 表；
     * 2. 商品明细：来自 order_item 和 product 表。
     */
    @Override
    public MerchantOrderDetailVO getOrderDetail(Integer merchantId, Integer orderId) {
        if (orderId == null) {
            return null;
        }

        MerchantOrderDetailVO detail = merchantOrderMapper.getOrderDetail(merchantId, orderId);

        if (detail == null) {
            return null;
        }

        // 查询一对多商品明细，并装配到订单详情对象中。
        List<MerchantOrderItemVO> items = merchantOrderMapper.listOrderItems(orderId);
        detail.setItems(items);

        return detail;
    }
    /**
     * 确认接单。
     * 返回 true 表示数据库成功更新了一行记录。
     */
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

    /**
     * 标记出餐完成。
     */
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

    /**
     * 召唤骑手。
     */
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