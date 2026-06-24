package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantShopInfo;
import org.apache.ibatis.annotations.*;

/**
 * 商家店铺信息 Mapper。
 */
@Mapper
public interface MerchantShopMapper {

    @Select("""
            SELECT
                mi.merchant_id AS merchantId,
                u.username AS username,
                mi.shop_name AS shopName,
                mi.contact_phone AS contactPhone,
                mi.shop_address AS shopAddress,
                mi.shop_notice AS shopNotice,
                mi.business_hours AS businessHours,
                mi.delivery_description AS deliveryDescription,
                mi.business_status AS businessStatus,
                u.status AS accountStatus
            FROM merchant_info mi
            INNER JOIN sys_user u ON mi.merchant_id = u.user_id
            WHERE mi.merchant_id = #{merchantId}
              AND u.role_type = 2
            """)
    MerchantShopInfo findShopByMerchantId(@Param("merchantId") Integer merchantId);

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
            )
            SELECT
                user_id,
                real_name,
                IFNULL(phone, '未绑定手机号'),
                '未设置店铺地址',
                '欢迎光临本店',
                '09:00-22:00',
                '商家接单后会尽快出餐',
                1
            FROM sys_user
            WHERE user_id = #{merchantId}
              AND role_type = 2
            """)
    int insertDefaultShop(@Param("merchantId") Integer merchantId);

    @Update("""
            UPDATE merchant_info
            SET shop_name = #{shopName},
                contact_phone = #{contactPhone},
                shop_address = #{shopAddress},
                shop_notice = #{shopNotice},
                business_hours = #{businessHours},
                delivery_description = #{deliveryDescription},
                business_status = #{businessStatus}
            WHERE merchant_id = #{merchantId}
            """)
    int updateShop(@Param("merchantId") Integer merchantId,
                   @Param("shopName") String shopName,
                   @Param("contactPhone") String contactPhone,
                   @Param("shopAddress") String shopAddress,
                   @Param("shopNotice") String shopNotice,
                   @Param("businessHours") String businessHours,
                   @Param("deliveryDescription") String deliveryDescription,
                   @Param("businessStatus") Integer businessStatus);

    @Update("""
            UPDATE sys_user
            SET real_name = #{shopName},
                phone = #{contactPhone}
            WHERE user_id = #{merchantId}
              AND role_type = 2
            """)
    int updateSysUserMerchantName(@Param("merchantId") Integer merchantId,
                                  @Param("shopName") String shopName,
                                  @Param("contactPhone") String contactPhone);
}