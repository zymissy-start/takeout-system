package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantOrderDetailVO;
import com.example.takeoutsystem.entity.MerchantOrderItemVO;
import com.example.takeoutsystem.entity.MerchantOrderVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface MerchantOrderMapper {

    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                o.status AS status,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
                COALESCE(
                    GROUP_CONCAT(CONCAT(p.name, ' × ', oi.quantity) SEPARATOR '，'),
                    '暂无商品明细'
                ) AS summary
            FROM delivery_order o
            LEFT JOIN sys_user u ON o.user_id = u.user_id
            LEFT JOIN order_item oi ON o.order_id = oi.order_id
            LEFT JOIN product p ON oi.product_id = p.product_id
            WHERE o.merchant_id = #{merchantId}
            GROUP BY
                o.order_id,
                u.real_name,
                o.status,
                o.total_price,
                o.order_time
            ORDER BY o.order_time DESC
            LIMIT #{size}
            """)
    List<MerchantOrderVO> listRecentOrders(@Param("merchantId") Integer merchantId,
                                           @Param("size") Integer size);

    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                o.status AS status,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
                COALESCE(
                    GROUP_CONCAT(CONCAT(p.name, ' × ', oi.quantity) SEPARATOR '，'),
                    '暂无商品明细'
                ) AS summary
            FROM delivery_order o
            LEFT JOIN sys_user u ON o.user_id = u.user_id
            LEFT JOIN order_item oi ON o.order_id = oi.order_id
            LEFT JOIN product p ON oi.product_id = p.product_id
            WHERE o.merchant_id = #{merchantId}
              AND (#{status} IS NULL OR o.status = #{status})
            GROUP BY
                o.order_id,
                u.real_name,
                o.status,
                o.total_price,
                o.order_time
            ORDER BY o.order_time DESC
            """)
    List<MerchantOrderVO> listOrders(@Param("merchantId") Integer merchantId,
                                     @Param("status") Integer status);

    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                o.status AS status,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
                DATE_FORMAT(o.merchant_confirm_time, '%Y-%m-%d %H:%i:%s') AS merchantConfirmTime,
                DATE_FORMAT(o.kitchen_finish_time, '%Y-%m-%d %H:%i:%s') AS kitchenFinishTime,
                DATE_FORMAT(o.estimated_arrival_time, '%Y-%m-%d %H:%i:%s') AS estimatedArrivalTime,
                DATE_FORMAT(o.finish_time, '%Y-%m-%d %H:%i:%s') AS finishTime,
                o.rider_name AS riderName,
                o.rider_phone AS riderPhone,
                o.address AS address,
                o.remark AS remark,
                o.is_urged AS isUrged
            FROM delivery_order o
            LEFT JOIN sys_user u ON o.user_id = u.user_id
            WHERE o.order_id = #{orderId}
              AND o.merchant_id = #{merchantId}
            """)
    MerchantOrderDetailVO getOrderDetail(@Param("merchantId") Integer merchantId,
                                         @Param("orderId") Integer orderId);

    @Select("""
            SELECT
                oi.product_id AS productId,
                p.name AS productName,
                oi.quantity AS quantity,
                oi.price AS price
            FROM order_item oi
            LEFT JOIN product p ON oi.product_id = p.product_id
            WHERE oi.order_id = #{orderId}
            ORDER BY oi.item_id ASC
            """)
    List<MerchantOrderItemVO> listOrderItems(@Param("orderId") Integer orderId);

    @Update("""
            UPDATE delivery_order
            SET status = 1,
                merchant_confirm_time = NOW()
            WHERE order_id = #{orderId}
              AND merchant_id = #{merchantId}
              AND status = 0
            """)
    int acceptOrder(@Param("merchantId") Integer merchantId,
                    @Param("orderId") Integer orderId);

    @Update("""
            UPDATE delivery_order
            SET status = 2,
                kitchen_finish_time = NOW()
            WHERE order_id = #{orderId}
              AND merchant_id = #{merchantId}
              AND status = 1
            """)
    int finishCooking(@Param("merchantId") Integer merchantId,
                      @Param("orderId") Integer orderId);

    @Update("""
            UPDATE delivery_order
            SET status = 3,
                rider_id = 4,
                rider_name = '极速骑手',
                rider_phone = '13800001111',
                estimated_arrival_time = DATE_ADD(NOW(), INTERVAL 30 MINUTE)
            WHERE order_id = #{orderId}
              AND merchant_id = #{merchantId}
              AND status = 2
            """)
    int callRider(@Param("merchantId") Integer merchantId,
                  @Param("orderId") Integer orderId);
}