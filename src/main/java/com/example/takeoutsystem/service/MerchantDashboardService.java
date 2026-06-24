package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.MerchantDashboardStatistics;

public interface MerchantDashboardService {

    MerchantDashboardStatistics getStatistics(Integer merchantId);
}