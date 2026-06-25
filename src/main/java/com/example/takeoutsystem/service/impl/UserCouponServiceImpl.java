package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.UserCouponVO;
import com.example.takeoutsystem.mapper.UserCouponMapper;
import com.example.takeoutsystem.service.UserCouponService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserCouponServiceImpl implements UserCouponService {
    private final UserCouponMapper userCouponMapper;

    public UserCouponServiceImpl(UserCouponMapper userCouponMapper) {
        this.userCouponMapper = userCouponMapper;
    }

    @Override
    public List<UserCouponVO> listCouponCenter(Integer userId) {
        userCouponMapper.expireUserCoupons(userId);
        return userCouponMapper.selectCouponCenter(userId);
    }

    @Override
    public List<UserCouponVO> listMyCoupons(Integer userId, Integer status) {
        userCouponMapper.expireUserCoupons(userId);
        return userCouponMapper.selectMyCoupons(userId, status);
    }

    /**
     * 高并发领券核心：
     * 1. user_coupon 上有 uk_user_coupon_once(user_id, coupon_id)，防止同一用户重复领券。
     * 2. coupon 库存使用一条带 remaining_stock > 0 条件的 UPDATE 原子扣减。
     * 3. INSERT 和扣库存处在同一个事务中，任何一步失败都会回滚。
     * 这种做法依赖 MySQL InnoDB 行锁，适合当前项目不引入 Redis 的课程设计场景。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> receiveCoupon(Integer userId, Integer couponId) {
        if (couponId == null || couponId <= 0) {
            throw new IllegalArgumentException("优惠券ID不能为空");
        }

        UserCouponVO coupon = userCouponMapper.selectCouponForReceive(couponId);

        if (coupon == null) {
            throw new IllegalArgumentException("优惠券不存在");
        }

        if (coupon.getStatus() == null || coupon.getStatus() != 1) {
            throw new IllegalArgumentException("优惠券已下架");
        }

        if (coupon.getPerUserLimit() != null
                && coupon.getPerUserLimit() <= userCouponMapper.countUserReceivedCoupon(userId, couponId)) {
            throw new IllegalArgumentException("你已经领取过该优惠券");
        }

        int updated = userCouponMapper.decreaseCouponStock(couponId);

        if (updated <= 0) {
            throw new IllegalArgumentException("优惠券已抢完或不在领取时间内");
        }

        try {
            userCouponMapper.insertUserCoupon(userId, couponId);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("你已经领取过该优惠券");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("couponId", couponId);
        result.put("message", "领取成功");
        return result;
    }
}