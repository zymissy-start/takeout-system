package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.RiderDashboardStatistics;
import com.example.takeoutsystem.mapper.RiderDashboardMapper;
import com.example.takeoutsystem.service.RiderDashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 骑手工作台业务实现。
 * 创新点：
 * 1. 根据今日完成单数自动计算骑手等级。
 * 2. 5 单以上升级为闪电侠骑手。
 * 3. 10 单以上升级为单王骑手。
 * 4. 收入 = 基础配送费 + 等级奖金 + 用户打赏。
 */
@Service
public class RiderDashboardServiceImpl implements RiderDashboardService {

    private final RiderDashboardMapper riderDashboardMapper;

    public RiderDashboardServiceImpl(RiderDashboardMapper riderDashboardMapper) {
        this.riderDashboardMapper = riderDashboardMapper;
    }

    @Override
    public RiderDashboardStatistics getStatistics(Integer riderUserId) {
        RiderDashboardStatistics statistics = new RiderDashboardStatistics();

        Integer finishedCount = riderDashboardMapper.countTodayFinished(riderUserId);
        Integer deliveringCount = riderDashboardMapper.countDelivering(riderUserId);
        Integer availableCount = riderDashboardMapper.countAvailableOrders();
        Integer riderStatus = riderDashboardMapper.getRiderStatus(riderUserId);
        BigDecimal avgSpeed = riderDashboardMapper.getAvgSpeed(riderUserId);
        BigDecimal tipAmount = riderDashboardMapper.sumTodayTipAmount(riderUserId);

        if (finishedCount == null) finishedCount = 0;
        if (deliveringCount == null) deliveringCount = 0;
        if (availableCount == null) availableCount = 0;
        if (riderStatus == null) riderStatus = 0;
        if (avgSpeed == null) avgSpeed = BigDecimal.ZERO;
        if (tipAmount == null) tipAmount = BigDecimal.ZERO;

        BigDecimal baseIncome = BigDecimal.valueOf(finishedCount).multiply(BigDecimal.valueOf(5));
        BigDecimal bonusAmount;
        String riderTitle;
        String riderTitleDesc;
        String nextTarget;
        Integer progressPercent;

        if (finishedCount >= 10) {
            riderTitle = "单王骑手";
            riderTitleDesc = "今日已完成 10 单以上，获得单王额外奖励";
            bonusAmount = BigDecimal.valueOf(finishedCount).multiply(BigDecimal.valueOf(2))
                    .add(BigDecimal.valueOf(30));
            nextTarget = "已达成今日最高等级，继续保持接单效率";
            progressPercent = 100;
        } else if (finishedCount >= 5) {
            riderTitle = "闪电侠骑手";
            riderTitleDesc = "今日已完成 5 单以上，获得闪电侠奖励";
            bonusAmount = BigDecimal.valueOf(finishedCount).multiply(BigDecimal.ONE)
                    .add(BigDecimal.valueOf(10));
            nextTarget = "距离单王骑手还差 " + (10 - finishedCount) + " 单";
            progressPercent = Math.min(99, finishedCount * 10);
        } else {
            riderTitle = "成长骑手";
            riderTitleDesc = "完成 5 单可升级为闪电侠骑手";
            bonusAmount = BigDecimal.ZERO;
            nextTarget = "距离闪电侠骑手还差 " + (5 - finishedCount) + " 单";
            progressPercent = Math.min(99, finishedCount * 20);
        }

        BigDecimal estimatedIncome = baseIncome.add(bonusAmount).add(tipAmount);

        statistics.setTodayFinishedCount(finishedCount);
        statistics.setDeliveringCount(deliveringCount);
        statistics.setAvailableOrderCount(availableCount);
        statistics.setBaseIncome(baseIncome);
        statistics.setBonusAmount(bonusAmount);
        statistics.setTipAmount(tipAmount);
        statistics.setEstimatedIncome(estimatedIncome);
        statistics.setRiderStatus(riderStatus);
        statistics.setAvgSpeed(avgSpeed);
        statistics.setRiderTitle(riderTitle);
        statistics.setRiderTitleDesc(riderTitleDesc);
        statistics.setNextTarget(nextTarget);
        statistics.setProgressPercent(progressPercent);

        return statistics;
    }
}