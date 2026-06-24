package com.example.takeoutsystem.service.impl;
import java.math.BigDecimal;
import com.example.takeoutsystem.entity.RiderOrderDetailVO;
import com.example.takeoutsystem.entity.RiderOrderItemVO;
import com.example.takeoutsystem.entity.RiderOrderVO;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.RiderMapper;
import com.example.takeoutsystem.mapper.RiderOrderMapper;
import com.example.takeoutsystem.service.RiderOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 骑手订单业务实现。
 * 创新点：
 * 1. 接单后自动更新订单状态。
 * 2. 接单后自动切换骑手为忙碌。
 * 3. 完成配送后自动切换骑手为空闲。
 * 4. 接单时自动生成预计送达时间。
 */
@Service
public class RiderOrderServiceImpl implements RiderOrderService {

    private final RiderOrderMapper riderOrderMapper;
    private final RiderMapper riderMapper;

    public RiderOrderServiceImpl(RiderOrderMapper riderOrderMapper, RiderMapper riderMapper) {
        this.riderOrderMapper = riderOrderMapper;
        this.riderMapper = riderMapper;
    }

    @Override
    public List<RiderOrderVO> listAvailableOrders() {
        return riderOrderMapper.listAvailableOrders();
    }

    @Override
    public List<RiderOrderVO> listMyOrders(Integer riderUserId, Integer status) {
        if (status != null && status != 3 && status != 4) {
            status = null;
        }

        return riderOrderMapper.listMyOrders(riderUserId, status);
    }

    @Override
    public RiderOrderDetailVO getOrderDetail(Integer riderUserId, Integer orderId) {
        if (orderId == null) {
            return null;
        }

        RiderOrderDetailVO detail = riderOrderMapper.getOrderDetail(riderUserId, orderId);

        if (detail == null) {
            return null;
        }

        List<RiderOrderItemVO> items = riderOrderMapper.listOrderItems(orderId);
        detail.setItems(items);

        return detail;
    }

    @Override
    @Transactional
    public boolean acceptOrder(SysUser rider, Integer orderId) {
        if (rider == null || orderId == null) {
            return false;
        }

        String riderName = rider.getRealName() == null ? rider.getUsername() : rider.getRealName();
        String riderPhone = rider.getPhone() == null ? "暂无电话" : rider.getPhone();

        int updateOrderRows = riderOrderMapper.acceptOrder(
                rider.getUserId(),
                riderName,
                riderPhone,
                orderId
        );

        if (updateOrderRows <= 0) {
            return false;
        }

        riderMapper.updateRiderStatus(rider.getUserId(), 1);

        return true;
    }


    @Override
    @Transactional
    public boolean finishOrder(Integer riderUserId, Integer orderId) {
        if (riderUserId == null || orderId == null) {
            return false;
        }

        int updateOrderRows = riderOrderMapper.finishOrder(riderUserId, orderId);

        if (updateOrderRows <= 0) {
            return false;
        }

        riderMapper.updateRiderStatus(riderUserId, 0);

        return true;
    }
    @Override
    public List<RiderOrderVO> listWaitCookingOrders() {
        return riderOrderMapper.listWaitCookingOrders();
    }

    @Override
    public boolean urgeMerchant(Integer orderId) {
        if (orderId == null) {
            return false;
        }

        return riderOrderMapper.urgeMerchant(orderId) > 0;
    }

    @Override
    public boolean addTip(Integer riderUserId, Integer orderId, BigDecimal tipAmount) {
        if (riderUserId == null || orderId == null || tipAmount == null) {
            return false;
        }

        if (tipAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (tipAmount.compareTo(BigDecimal.valueOf(100)) > 0) {
            return false;
        }

        return riderOrderMapper.addTip(riderUserId, orderId, tipAmount) > 0;
    }
}