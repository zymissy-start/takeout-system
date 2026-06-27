package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantShopInfo;
import com.example.takeoutsystem.entity.MerchantReviewVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

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
                mi.store_address AS storeAddress,
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
                store_address = #{storeAddress},
                min_order_amount = #{minOrderAmount},
                status = #{businessStatus}
            WHERE merchant_id = #{merchantId}
            """)
    int updateShop(@Param("merchantId") Integer merchantId,
                   @Param("storeName") String storeName,
                   @Param("storeLogo") String storeLogo,
                   @Param("storeNotice") String storeNotice,
                   @Param("storeAddress") String storeAddress,
                   @Param("minOrderAmount") BigDecimal minOrderAmount,
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

    @Select("""
            SELECT
                r.review_id AS reviewId,
                r.order_id AS orderId,
                u.real_name AS userName,
                r.score AS score,
                r.content AS content,
                DATE_FORMAT(r.create_time, '%Y-%m-%d %H:%i:%s') AS createTime
            FROM product_review r
            LEFT JOIN sys_user u ON r.user_id = u.user_id
            WHERE r.merchant_id = #{merchantId}
            ORDER BY r.create_time DESC
            LIMIT 50
            """)
    List<MerchantReviewVO> listReviews(@Param("merchantId") Integer merchantId);
}