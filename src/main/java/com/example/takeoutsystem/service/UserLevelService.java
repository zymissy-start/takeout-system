package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.UserLevelVO;

public interface UserLevelService {
    UserLevelVO getCurrentLevel(Integer userId);
    int getReminderCooldownSeconds(Integer userId);
    void addGrowthForCompletedOrder(Integer userId, Integer orderId, int orderAmountIntegerPart);
    void addGrowthForReview(Integer userId, Integer orderId);
}
