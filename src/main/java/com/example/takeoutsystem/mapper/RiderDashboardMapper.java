package com.example.takeoutsystem.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 骑手工作台统计 Mapper。
 * 负责统计今日完成、配送中、可接订单、打赏金额等数据。
 */
public interface RiderDashboardMapper {

    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE rider_id = #{riderUserId}
              AND status = 4
              AND DATE(finish_time) = CURDATE()
            """)
    Integer countTodayFinished(@Param("riderUserId") Integer riderUserId);

    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE rider_id = #{riderUserId}
              AND status = 3
            """)
    Integer countDelivering(@Param("riderUserId") Integer riderUserId);

    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE status = 2
            """)
    Integer countAvailableOrders();

    @Select("""
            SELECT IFNULL(SUM(tip_amount), 0)
            FROM delivery_order
            WHERE rider_id = #{riderUserId}
              AND DATE(IFNULL(finish_time, NOW())) = CURDATE()
            """)
    BigDecimal sumTodayTipAmount(@Param("riderUserId") Integer riderUserId);

    @Select("""
            SELECT status
            FROM rider_info
            WHERE user_id = #{riderUserId}
            """)
    Integer getRiderStatus(@Param("riderUserId") Integer riderUserId);

    @Select("""
            SELECT avg_speed
            FROM rider_info
            WHERE user_id = #{riderUserId}
            """)
    BigDecimal getAvgSpeed(@Param("riderUserId") Integer riderUserId);
}