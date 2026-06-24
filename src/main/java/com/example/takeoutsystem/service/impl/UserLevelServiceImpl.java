package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.UserLevelVO;
import com.example.takeoutsystem.mapper.UserLevelMapper;
import com.example.takeoutsystem.mapper.UserOrderMapper;
import com.example.takeoutsystem.service.UserLevelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserLevelServiceImpl implements UserLevelService {
    private final UserLevelMapper userLevelMapper;
    private final UserOrderMapper userOrderMapper;

    public UserLevelServiceImpl(UserLevelMapper userLevelMapper, UserOrderMapper userOrderMapper) {
        this.userLevelMapper = userLevelMapper;
        this.userOrderMapper = userOrderMapper;
    }

    @Override
    public UserLevelVO getCurrentLevel(Integer userId) {
        UserLevelVO current = userLevelMapper.selectCurrentLevel(userId);
        if (current == null) {
            current = new UserLevelVO();
            current.setLevelId(1);
            current.setLevelName("Lv1 普通用户");
            current.setMinGrowth(0);
            current.setMaxGrowth(99);
            current.setGrowthValue(0);
            current.setDeliveryDiscountRate(BigDecimal.ONE);
            current.setRemindCooldownSeconds(180);
            current.setPriorityFlag(0);
        }
        Integer growth = current.getGrowthValue() == null ? 0 : current.getGrowthValue();
        UserLevelVO next = userLevelMapper.selectNextLevel(growth);
        if (next != null) {
            current.setNextLevelName(next.getLevelName());
            current.setNextMinGrowth(next.getMinGrowth());
            current.setNextNeedGrowth(Math.max(0, next.getMinGrowth() - growth));
        } else {
            current.setNextLevelName("已达最高等级");
            current.setNextMinGrowth(current.getMaxGrowth());
            current.setNextNeedGrowth(0);
        }
        int min = current.getMinGrowth() == null ? 0 : current.getMinGrowth();
        int max = current.getMaxGrowth() == null ? Math.max(min + 1, growth) : current.getMaxGrowth();
        int percent = max <= min ? 100 : (int) Math.min(100, Math.max(0, ((growth - min) * 100.0 / (max - min + 1))));
        current.setProgressPercent(percent);
        applyOrderLevel(userId, current);
        return current;
    }

    @Override
    public int getReminderCooldownSeconds(Integer userId) {
        UserLevelVO level = getCurrentLevel(userId);
        return level.getRemindCooldownSeconds() == null ? 180 : level.getRemindCooldownSeconds();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGrowthForCompletedOrder(Integer userId, Integer orderId, int orderAmountIntegerPart) {
        if (userLevelMapper.countGrowthLog(userId, orderId, "ORDER_FINISH") > 0) return;
        int change = 10 + Math.max(0, orderAmountIntegerPart / 10);
        updateGrowth(userId, orderId, change, "ORDER_FINISH");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGrowthForReview(Integer userId, Integer orderId) {
        if (userLevelMapper.countGrowthLog(userId, orderId, "ORDER_REVIEW") > 0) return;
        updateGrowth(userId, orderId, 5, "ORDER_REVIEW");
    }

    private void applyOrderLevel(Integer userId, UserLevelVO current) {
        Integer count = userOrderMapper.countUserOrders(userId);
        int orderCount = count == null ? 0 : count;
        current.setOrderCount(orderCount);
        if (orderCount >= 15) {
            current.setOrderLevel(2);
            current.setOrderTitle("尊享用户");
            current.setMatchedRiderTitle("单王配送骑手");
            current.setNextOrderTitle("已达最高用户等级");
            current.setNextNeedOrders(0);
        } else if (orderCount >= 10) {
            current.setOrderLevel(1);
            current.setOrderTitle("优先用户");
            current.setMatchedRiderTitle("闪电侠骑手");
            current.setNextOrderTitle("尊享用户");
            current.setNextNeedOrders(15 - orderCount);
        } else {
            current.setOrderLevel(0);
            current.setOrderTitle("普通用户");
            current.setMatchedRiderTitle("普通骑手");
            current.setNextOrderTitle("优先用户");
            current.setNextNeedOrders(10 - orderCount);
        }
    }

    private void updateGrowth(Integer userId, Integer orderId, int change, String reason) {
        UserLevelVO current = getCurrentLevel(userId);
        int newGrowth = (current.getGrowthValue() == null ? 0 : current.getGrowthValue()) + change;
        UserLevelVO newLevel = userLevelMapper.selectLevelByGrowth(newGrowth);
        Integer newLevelId = newLevel == null ? current.getLevelId() : newLevel.getLevelId();
        userLevelMapper.updateUserLevel(userId, newLevelId, newGrowth);
        userLevelMapper.insertGrowthLog(userId, orderId, change, reason);
    }
}
