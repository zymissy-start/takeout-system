package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.RiderNavigationVO;
import com.example.takeoutsystem.mapper.RiderNavigationMapper;
import com.example.takeoutsystem.service.RiderNavigationService;
import org.springframework.stereotype.Service;

@Service
public class RiderNavigationServiceImpl implements RiderNavigationService {

    private final RiderNavigationMapper riderNavigationMapper;

    public RiderNavigationServiceImpl(RiderNavigationMapper riderNavigationMapper) {
        this.riderNavigationMapper = riderNavigationMapper;
    }

    @Override
    public RiderNavigationVO getNavigationOrder(Integer orderId, Integer riderId) {
        RiderNavigationVO nav;

        if (orderId != null) {
            nav = riderNavigationMapper.selectByOrderId(orderId);
        } else {
            nav = riderNavigationMapper.selectActiveByRiderId(riderId);
        }

        if (nav == null) {
            throw new IllegalStateException("暂无可导航订单，请先接单或传入正确的 orderId");
        }

        if (nav.getStartLongitude() == null || nav.getStartLatitude() == null) {
            throw new IllegalStateException("商家缺少经纬度，请检查 merchant_info.store_longitude / store_latitude");
        }

        if (nav.getEndLongitude() == null || nav.getEndLatitude() == null) {
            throw new IllegalStateException("用户收货地址缺少经纬度，请检查 delivery_order.receiver_longitude / receiver_latitude");
        }

        return nav;
    }
}