package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.UserLevelVO;
import com.example.takeoutsystem.entity.UserProfileVO;
import com.example.takeoutsystem.entity.UserStatsVO;
import com.example.takeoutsystem.mapper.UserAddressMapper;
import com.example.takeoutsystem.mapper.UserOrderMapper;
import com.example.takeoutsystem.mapper.UserProfileMapper;
import com.example.takeoutsystem.service.UserLevelService;
import com.example.takeoutsystem.service.UserProfileService;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileMapper userProfileMapper;
    private final UserOrderMapper userOrderMapper;
    private final UserAddressMapper userAddressMapper;
    private final UserLevelService userLevelService;

    public UserProfileServiceImpl(UserProfileMapper userProfileMapper,
                                  UserOrderMapper userOrderMapper,
                                  UserAddressMapper userAddressMapper,
                                  UserLevelService userLevelService) {
        this.userProfileMapper = userProfileMapper;
        this.userOrderMapper = userOrderMapper;
        this.userAddressMapper = userAddressMapper;
        this.userLevelService = userLevelService;
    }

    @Override
    public UserProfileVO getMe(Integer userId) {
        UserProfileVO user = userProfileMapper.selectUserProfile(userId);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        return user;
    }

    @Override
    public UserStatsVO getStats(Integer userId) {
        UserProfileVO me = getMe(userId);
        UserStatsVO stats = new UserStatsVO();
        stats.setOrderCount(userOrderMapper.countUserOrders(userId));
        stats.setCouponCount(userOrderMapper.countUserCoupons(userId));
        stats.setAddressCount(userAddressMapper.countByUserId(userId));
        stats.setReviewCount(userOrderMapper.countUserReviews(userId));
        stats.setGrowthValue(me.getGrowthValue());
        stats.setLevelName(me.getLevelName());
        stats.setCreditScore(me.getCreditScore());
        return stats;
    }

    @Override
    public UserLevelVO getLevel(Integer userId) {
        return userLevelService.getCurrentLevel(userId);
    }
}
