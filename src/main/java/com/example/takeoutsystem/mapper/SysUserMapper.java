package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {

    SysUser selectById(@Param("userId") Integer userId);

    SysUser selectByUsername(@Param("username") String username);

    SysUser findMerchantByUsernameAndPassword(@Param("username") String username,
                                              @Param("password") String password);

    int insert(SysUser user);

    int update(SysUser user);
}
