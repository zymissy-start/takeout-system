package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.RiderInfo;
import com.example.takeoutsystem.entity.RiderOrderDetailVO;
import com.example.takeoutsystem.entity.RiderOrderItemVO;
import com.example.takeoutsystem.entity.RiderOrderVO;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.RiderMapper;
import com.example.takeoutsystem.mapper.RiderOrderMapper;
import com.example.takeoutsystem.service.RiderOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 骑手订单业务实现。
 * 接单池按用户订单等级匹配骑手等级：普通、闪电侠、单王骑手。
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
    public List<RiderOrderVO> listAvailableOrders(Integer riderUserId) {
        if (riderUserId == null) {
            return List.of();
        }
        refreshRiderLevel(riderUserId);
        return riderOrderMapper.listAvailableOrders(riderUserId);
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
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptOrder(SysUser rider, Integer orderId) {
        if (rider == null || orderId == null) {
            return false;
        }
        refreshRiderLevel(rider.getUserId());
        String riderName = rider.getRealName() == null ? rider.getUsername() : rider.getRealName();
        String riderPhone = rider.getPhone() == null ? "暂无电话" : rider.getPhone();
        int updateOrderRows = riderOrderMapper.acceptOrder(rider.getUserId(), riderName, riderPhone, orderId);
        if (updateOrderRows <= 0) {
            return false;
        }
        riderMapper.updateRiderStatus(rider.getUserId(), 1);
        riderOrderMapper.insertStatusLog(rider.getUserId(), orderId, 3, "骑手已接单", "订单进入配送中");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean finishOrder(Integer riderUserId, Integer orderId) {
        if (riderUserId == null || orderId == null) {
            return false;
        }
        int updateOrderRows = riderOrderMapper.finishOrder(riderUserId, orderId);
        if (updateOrderRows <= 0) {
            return false;
        }
        riderMapper.updateRiderStatus(riderUserId, 0);
        riderOrderMapper.handleOrderReminders(orderId);
        refreshRiderLevel(riderUserId);
        riderOrderMapper.insertStatusLog(riderUserId, orderId, 4, "骑手已送达", "配送完成，催单提醒已处理");
        return true;
    }

    @Override
    public List<RiderOrderVO> listWaitCookingOrders() {
        return riderOrderMapper.listWaitCookingOrders();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean urgeMerchant(Integer riderUserId, Integer orderId) {
        if (orderId == null) {
            return false;
        }
        int rows = riderOrderMapper.urgeMerchant(orderId);
        if (rows <= 0) {
            return false;
        }
        if (riderUserId != null) {
            riderOrderMapper.insertMerchantReminder(riderUserId, orderId, "骑手催商家：请尽快出餐");
            riderOrderMapper.insertStatusLog(riderUserId, orderId, 1, "骑手催商家出餐", "骑手提醒商家尽快出餐");
        }
        return true;
    }

    @Override
    public boolean addTip(Integer riderUserId, Integer orderId, BigDecimal tipAmount) {
        if (riderUserId == null || orderId == null || tipAmount == null) {
            return false;
        }
        if (tipAmount.compareTo(BigDecimal.ZERO) <= 0 || tipAmount.compareTo(BigDecimal.valueOf(100)) > 0) {
            return false;
        }
        return riderOrderMapper.addTip(riderUserId, orderId, tipAmount) > 0;
    }

    private void refreshRiderLevel(Integer riderUserId) {
        Integer total = riderMapper.countFinishedOrders(riderUserId);
        if (total == null) total = 0;
        RiderInfo info = riderMapper.findRiderInfoByUserId(riderUserId);
        if (info != null && info.getTotalFinishedCount() != null) {
            total = Math.max(total, info.getTotalFinishedCount());
        }
        int level = resolveRiderLevel(total);
        String title = resolveRiderTitle(level);
        riderMapper.updateRiderLevel(riderUserId, total, level, title);
    }

    private int resolveRiderLevel(int finishedCount) {
        if (finishedCount >= 15) return 2;
        if (finishedCount >= 10) return 1;
        return 0;
    }

    private String resolveRiderTitle(int level) {
        if (level >= 2) return "单王配送骑手";
        if (level == 1) return "闪电侠骑手";
        return "普通骑手";
    }
}
