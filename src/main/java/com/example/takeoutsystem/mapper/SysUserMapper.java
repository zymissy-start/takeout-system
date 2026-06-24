package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.SysUser;
<<<<<<< HEAD
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表数据访问接口。
 *
 * Mapper 层是系统直接访问数据库的入口。
 * 这里通过 SQL 判断账号、密码、角色类型和账号状态，
 * 保证只有 role_type = 2 的正常商家账号可以登录商家端。
 */
public interface SysUserMapper {

    /**
     * 根据账号和密码查询商家用户。
     *
     * role_type = 2：表示商家角色。
     * status = 1：表示账号处于正常状态。
     *
     * 字段别名用于把数据库下划线命名映射为 Java 驼峰属性。
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
}
=======
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
>>>>>>> origin/feature-user-rider-merchant
