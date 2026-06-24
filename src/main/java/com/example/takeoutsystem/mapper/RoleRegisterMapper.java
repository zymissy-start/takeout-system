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
                shop_name,
                contact_phone,
                shop_address,
                shop_notice,
                business_hours,
                delivery_description,
                business_status
            ) VALUES (
                #{merchantId},
                #{shopName},
                #{contactPhone},
                #{shopAddress},
                #{shopNotice},
                #{businessHours},
                #{deliveryDescription},
                1
            )
            """)
    int insertMerchantInfo(@Param("merchantId") Integer merchantId,
                           @Param("shopName") String shopName,
                           @Param("contactPhone") String contactPhone,
                           @Param("shopAddress") String shopAddress,
                           @Param("shopNotice") String shopNotice,
                           @Param("businessHours") String businessHours,
                           @Param("deliveryDescription") String deliveryDescription);
}