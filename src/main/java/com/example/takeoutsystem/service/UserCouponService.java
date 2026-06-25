package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.UserCouponVO;

import java.util.List;
import java.util.Map;

public interface UserCouponService {
    List<UserCouponVO> listCouponCenter(Integer userId);

    List<UserCouponVO> listMyCoupons(Integer userId, Integer status);

    Map<String, Object> receiveCoupon(Integer userId, Integer couponId);
}