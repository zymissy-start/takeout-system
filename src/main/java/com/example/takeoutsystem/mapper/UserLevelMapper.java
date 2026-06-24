package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.UserLevelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserLevelMapper {
    UserLevelVO selectCurrentLevel(@Param("userId") Integer userId);
    UserLevelVO selectLevelByGrowth(@Param("growthValue") Integer growthValue);
    UserLevelVO selectNextLevel(@Param("growthValue") Integer growthValue);
    int updateUserLevel(@Param("userId") Integer userId, @Param("levelId") Integer levelId, @Param("growthValue") Integer growthValue);
    int insertGrowthLog(@Param("userId") Integer userId,
                        @Param("orderId") Integer orderId,
                        @Param("changeValue") Integer changeValue,
                        @Param("reason") String reason);
    int countGrowthLog(@Param("userId") Integer userId, @Param("orderId") Integer orderId, @Param("reason") String reason);
}
