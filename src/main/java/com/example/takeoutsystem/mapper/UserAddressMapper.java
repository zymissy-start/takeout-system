package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserAddressMapper {
    List<UserAddress> selectByUserId(@Param("userId") Integer userId);
    UserAddress selectByIdAndUserId(@Param("addressId") Integer addressId, @Param("userId") Integer userId);
    int insert(UserAddress address);
    int update(UserAddress address);
    int deleteByIdAndUserId(@Param("addressId") Integer addressId, @Param("userId") Integer userId);
    int clearDefault(@Param("userId") Integer userId);
    int setDefault(@Param("addressId") Integer addressId, @Param("userId") Integer userId);
    int countByUserId(@Param("userId") Integer userId);
}
