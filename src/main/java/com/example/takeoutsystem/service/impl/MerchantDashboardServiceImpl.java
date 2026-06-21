package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.MerchantDashboardStatistics;
import com.example.takeoutsystem.mapper.MerchantDashboardMapper;
import com.example.takeoutsystem.service.MerchantDashboardService;
import org.springframework.stereotype.Service;

@Service
public class MerchantDashboardServiceImpl implements MerchantDashboardService {

    private final MerchantDashboardMapper merchantDashboardMapper;

    public MerchantDashboardServiceImpl(MerchantDashboardMapper merchantDashboardMapper) {
        this.merchantDashboardMapper = merchantDashboardMapper;
    }

    @Override
    public MerchantDashboardStatistics getStatistics(Integer merchantId) {
        MerchantDashboardStatistics statistics = new MerchantDashboardStatistics();

        statistics.setWaitAcceptCount(merchantDashboardMapper.countWaitAccept(merchantId));
        statistics.setCookingCount(merchantDashboardMapper.countCooking(merchantId));
        statistics.setWaitRiderCount(merchantDashboardMapper.countWaitRider(merchantId));
        statistics.setFinishedCount(merchantDashboardMapper.countFinished(merchantId));

        return statistics;
    }
}