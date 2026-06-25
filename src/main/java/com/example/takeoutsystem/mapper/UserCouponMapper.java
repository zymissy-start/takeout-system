package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.UserCouponVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCouponMapper {
    int expireUserCoupons(@Param("userId") Integer userId);

    List<UserCouponVO> selectCouponCenter(@Param("userId") Integer userId);

    List<UserCouponVO> selectMyCoupons(@Param("userId") Integer userId,
                                       @Param("status") Integer status);

    UserCouponVO selectCouponForReceive(@Param("couponId") Integer couponId);

    int countUserReceivedCoupon(@Param("userId") Integer userId,
                                @Param("couponId") Integer couponId);

    int decreaseCouponStock(@Param("couponId") Integer couponId);

    int insertUserCoupon(@Param("userId") Integer userId,
                         @Param("couponId") Integer couponId);
}