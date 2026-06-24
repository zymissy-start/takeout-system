package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.UserLevelVO;
import com.example.takeoutsystem.entity.UserProfileVO;
import com.example.takeoutsystem.entity.UserStatsVO;

public interface UserProfileService {
    UserProfileVO getMe(Integer userId);
    UserStatsVO getStats(Integer userId);
    UserLevelVO getLevel(Integer userId);
}
