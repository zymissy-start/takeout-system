package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.UserLevelVO;
import com.example.takeoutsystem.mapper.UserLevelMapper;
import com.example.takeoutsystem.service.UserLevelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserLevelServiceImpl implements UserLevelService {
    private final UserLevelMapper userLevelMapper;

    public UserLevelServiceImpl(UserLevelMapper userLevelMapper) {
        this.userLevelMapper = userLevelMapper;
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

    private void updateGrowth(Integer userId, Integer orderId, int change, String reason) {
        UserLevelVO current = getCurrentLevel(userId);
        int newGrowth = (current.getGrowthValue() == null ? 0 : current.getGrowthValue()) + change;
        UserLevelVO newLevel = userLevelMapper.selectLevelByGrowth(newGrowth);
        Integer newLevelId = newLevel == null ? current.getLevelId() : newLevel.getLevelId();
        userLevelMapper.updateUserLevel(userId, newLevelId, newGrowth);
        userLevelMapper.insertGrowthLog(userId, orderId, change, reason);
    }
}
