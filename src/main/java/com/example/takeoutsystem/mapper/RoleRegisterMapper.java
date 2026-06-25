package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.SysUser;
import org.apache.ibatis.annotations.*;

/**
 * 商家 / 骑手注册 Mapper。
 */
@Mapper
public interface RoleRegisterMapper {

    @Select("""
            SELECT COUNT(*)
            FROM sys_user
            WHERE username = #{username}
            """)
    int countByUsername(@Param("username") String username);

    @Insert("""
            INSERT INTO sys_user (
                username,
                password,
                real_name,
                phone,
                role_type,
                credit_score,
                status,
                create_time
            ) VALUES (
                #{user.username},
                #{user.password},
                #{user.realName},
                #{user.phone},
                #{user.roleType},
                #{user.creditScore},
                #{user.status},
                NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "user.userId", keyColumn = "user_id")
    int insertSysUser(@Param("user") SysUser user);

    @Insert("""
            INSERT INTO merchant_info (
                merchant_id,
                store_name,
                store_logo,
                store_notice,
                rating,
                monthly_sales,
                min_order_amount,
                delivery_fee,
                delivery_time,
                distance_km,
                status
            ) VALUES (
                #{merchantId},
                #{storeName},
                NULL,
                #{storeNotice},
                5.0,
                0,
                0.00,
                3.00,
                30,
                1.00,
                1
            )
            """)
    int insertMerchantInfo(@Param("merchantId") Integer merchantId,
                           @Param("storeName") String storeName,
                           @Param("storeNotice") String storeNotice);
}