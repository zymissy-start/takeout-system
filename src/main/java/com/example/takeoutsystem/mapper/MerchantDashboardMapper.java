package com.example.takeoutsystem.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface MerchantDashboardMapper {

    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE merchant_id = #{merchantId}
              AND status = 0
            """)
    Integer countWaitAccept(@Param("merchantId") Integer merchantId);

    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE merchant_id = #{merchantId}
              AND status = 1
            """)
    Integer countCooking(@Param("merchantId") Integer merchantId);

    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE merchant_id = #{merchantId}
              AND status = 2
            """)
    Integer countWaitRider(@Param("merchantId") Integer merchantId);

    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE merchant_id = #{merchantId}
              AND status = 4
            """)
    Integer countFinished(@Param("merchantId") Integer merchantId);
}