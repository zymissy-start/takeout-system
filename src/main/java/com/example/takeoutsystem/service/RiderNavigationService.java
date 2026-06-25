package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.RiderNavigationVO;

public interface RiderNavigationService {

    RiderNavigationVO getNavigationOrder(Integer orderId, Integer riderId);
}