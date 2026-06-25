package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表数据访问接口。
 */
@Mapper
public interface SysUserMapper {

    SysUser selectById(@Param("userId") Integer userId);

    SysUser selectByUsername(@Param("username") String username);

    /**
     * 商家登录验证（使用注解定义 SQL）。
     */
    @Select("""
            SELECT
                user_id AS userId,
                username,
                password,
                real_name AS realName,
                phone,
                role_type AS roleType,
                credit_score AS creditScore,
                status,
                create_time AS createTime
            FROM sys_user
            WHERE username = #{username}
              AND password = #{password}
              AND role_type = 2
              AND status = 1
            """)
    SysUser findMerchantByUsernameAndPassword(@Param("username") String username,
                                              @Param("password") String password);

    int insert(SysUser user);

    int update(SysUser user);
}