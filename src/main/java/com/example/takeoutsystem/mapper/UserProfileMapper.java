package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.UserProfileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {
    UserProfileVO selectUserProfile(@Param("userId") Integer userId);
}
