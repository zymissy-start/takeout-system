package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.RiderDashboardStatistics;

public interface RiderDashboardService {

    RiderDashboardStatistics getStatistics(Integer riderUserId);
}