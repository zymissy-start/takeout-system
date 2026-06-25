package com.example.takeoutsystem.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
/**
 * 商家首页统计数据访问接口。
 *
 * 首页四个统计卡片的数据均来自 delivery_order 表，
 * 根据订单状态 status 分别统计待接单、制作中、待骑手接单和已完成订单数。
 */


public interface MerchantDashboardMapper {

    /** 统计待商家接单订单数量，status = 0。 */
    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE merchant_id = #{merchantId}
              AND status = 0
            """)
    Integer countWaitAccept(@Param("merchantId") Integer merchantId);
    /** 统计制作中订单数量，status = 1。 */
    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE merchant_id = #{merchantId}
              AND status = 1
            """)
    Integer countCooking(@Param("merchantId") Integer merchantId);

    /** 统计已出餐、待骑手接单订单数量，status = 2。 */
    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE merchant_id = #{merchantId}
              AND status = 2
            """)
    Integer countWaitRider(@Param("merchantId") Integer merchantId);

    /** 统计已完成订单数量，status = 4。 */
    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE merchant_id = #{merchantId}
              AND status = 4
            """)
    Integer countFinished(@Param("merchantId") Integer merchantId);
}