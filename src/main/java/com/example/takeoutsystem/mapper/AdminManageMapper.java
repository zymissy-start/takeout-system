package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminManageMapper {

    List<SysUser> listUsersByRole(@Param("roleType") Integer roleType,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    int countByRole(@Param("roleType") Integer roleType, @Param("keyword") String keyword);

    SysUser selectUserDetail(@Param("userId") Integer userId);

    int updateUserStatus(@Param("userId") Integer userId, @Param("status") Integer status);

    Map<String, Object> selectMerchantDetail(@Param("userId") Integer userId);
}
