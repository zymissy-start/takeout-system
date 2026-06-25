package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantPrintOrderItemVO;
import com.example.takeoutsystem.entity.MerchantPrintOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商家订单打印 Mapper。
 */
@Mapper
public interface MerchantOrderPrintMapper {

    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                IFNULL(mi.store_name, m.real_name) AS merchantName,
                m.phone AS merchantPhone,
                NULL AS merchantAddress,
                CASE o.status
                    WHEN 0 THEN '待商家接单'
                    WHEN 1 THEN '制作中'
                    WHEN 2 THEN '待骑手接单'
                    WHEN 3 THEN '配送中'
                    WHEN 4 THEN '已完成'
                    ELSE '未知状态'
                END AS statusText,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
                DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s') AS printTime,
                o.address AS address,
                o.remark AS remark
            FROM delivery_order o
            LEFT JOIN sys_user u ON o.user_id = u.user_id
            LEFT JOIN sys_user m ON o.merchant_id = m.user_id
            LEFT JOIN merchant_info mi ON o.merchant_id = mi.merchant_id
            WHERE o.order_id = #{orderId}
              AND o.merchant_id = #{merchantId}
            """)
    MerchantPrintOrderVO findPrintOrder(@Param("merchantId") Integer merchantId,
                                        @Param("orderId") Integer orderId);

    @Select("""
            SELECT
                oi.product_id AS productId,
                p.name AS productName,
                oi.quantity AS quantity,
                oi.price AS price,
                oi.quantity * oi.price AS subtotal
            FROM order_item oi
            LEFT JOIN product p ON oi.product_id = p.product_id
            WHERE oi.order_id = #{orderId}
            ORDER BY oi.item_id ASC
            """)
    List<MerchantPrintOrderItemVO> listPrintItems(@Param("orderId") Integer orderId);
}