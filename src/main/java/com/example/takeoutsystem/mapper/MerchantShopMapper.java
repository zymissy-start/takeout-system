package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantShopInfo;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;

/**
 * 商家店铺信息 Mapper。
 */
@Mapper
public interface MerchantShopMapper {

    @Select("""
            SELECT
                mi.merchant_id AS merchantId,
                u.username AS username,
                mi.store_name AS storeName,
                mi.store_logo AS storeLogo,
                mi.store_notice AS storeNotice,
                mi.rating AS rating,
                mi.monthly_sales AS monthlySales,
                mi.min_order_amount AS minOrderAmount,
                mi.delivery_fee AS deliveryFee,
                mi.delivery_time AS deliveryTime,
                mi.distance_km AS distanceKm,
                mi.status AS businessStatus,
                u.phone AS contactPhone,
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
            )
            SELECT
                user_id,
                real_name,
                NULL,
                '欢迎光临本店',
                5.0,
                0,
                0.00,
                3.00,
                30,
                1.00,
                1
            FROM sys_user
            WHERE user_id = #{merchantId}
              AND role_type = 2
            """)
    int insertDefaultShop(@Param("merchantId") Integer merchantId);

    @Update("""
            UPDATE merchant_info
            SET store_name = #{storeName},
                store_logo = #{storeLogo},
                store_notice = #{storeNotice},
                min_order_amount = #{minOrderAmount},
                delivery_fee = #{deliveryFee},
                delivery_time = #{deliveryTime},
                distance_km = #{distanceKm},
                status = #{businessStatus}
            WHERE merchant_id = #{merchantId}
            """)
    int updateShop(@Param("merchantId") Integer merchantId,
                   @Param("storeName") String storeName,
                   @Param("storeLogo") String storeLogo,
                   @Param("storeNotice") String storeNotice,
                   @Param("minOrderAmount") BigDecimal minOrderAmount,
                   @Param("deliveryFee") BigDecimal deliveryFee,
                   @Param("deliveryTime") Integer deliveryTime,
                   @Param("distanceKm") BigDecimal distanceKm,
                   @Param("businessStatus") Integer businessStatus);

    @Update("""
            UPDATE sys_user
            SET real_name = #{storeName},
                phone = #{contactPhone}
            WHERE user_id = #{merchantId}
              AND role_type = 2
            """)
    int updateSysUserMerchantInfo(@Param("merchantId") Integer merchantId,
                                  @Param("storeName") String storeName,
                                  @Param("contactPhone") String contactPhone);
}