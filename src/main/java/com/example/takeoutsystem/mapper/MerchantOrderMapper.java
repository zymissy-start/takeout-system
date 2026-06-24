package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantOrderDetailVO;
import com.example.takeoutsystem.entity.MerchantOrderItemVO;
import com.example.takeoutsystem.entity.MerchantOrderVO;
import org.apache.ibatis.annotations.Insert;
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
                CASE WHEN o.status IN (0, 1, 2, 3) THEN IFNULL(o.is_urged, 0) ELSE 0 END AS isUrged,
                CASE WHEN o.status IN (0, 1, 2, 3) THEN IFNULL(o.rider_urge_count, 0) ELSE 0 END AS riderUrgeCount,
                DATE_FORMAT(o.rider_urge_time, '%Y-%m-%d %H:%i:%s') AS riderUrgeTime,
                IFNULL(o.required_rider_level, 0) AS requiredRiderLevel,
                IFNULL(o.required_rider_title, '普通') AS requiredRiderTitle,
                (
                    SELECT COUNT(*)
                    FROM order_reminder r
                    WHERE r.order_id = o.order_id
                      AND r.target_type = 'MERCHANT'
                      AND r.target_id = #{merchantId}
                      AND r.status <> 'HANDLED'
                      AND o.status IN (0, 1, 2, 3)
                ) AS reminderCount,
                (
                    SELECT DATE_FORMAT(MAX(r.create_time), '%Y-%m-%d %H:%i:%s')
                    FROM order_reminder r
                    WHERE r.order_id = o.order_id
                      AND r.target_type = 'MERCHANT'
                      AND r.target_id = #{merchantId}
                      AND r.status <> 'HANDLED'
                      AND o.status IN (0, 1, 2, 3)
                ) AS latestReminderTime,
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
                o.order_time,
                o.is_urged,
                o.rider_urge_count,
                o.rider_urge_time,
                o.required_rider_level,
                o.required_rider_title
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
                CASE WHEN o.status IN (0, 1, 2, 3) THEN IFNULL(o.is_urged, 0) ELSE 0 END AS isUrged,
                CASE WHEN o.status IN (0, 1, 2, 3) THEN IFNULL(o.rider_urge_count, 0) ELSE 0 END AS riderUrgeCount,
                DATE_FORMAT(o.rider_urge_time, '%Y-%m-%d %H:%i:%s') AS riderUrgeTime,
                IFNULL(o.required_rider_level, 0) AS requiredRiderLevel,
                IFNULL(o.required_rider_title, '普通') AS requiredRiderTitle,
                (
                    SELECT COUNT(*)
                    FROM order_reminder r
                    WHERE r.order_id = o.order_id
                      AND r.target_type = 'MERCHANT'
                      AND r.target_id = #{merchantId}
                      AND r.status <> 'HANDLED'
                      AND o.status IN (0, 1, 2, 3)
                ) AS reminderCount,
                (
                    SELECT DATE_FORMAT(MAX(r.create_time), '%Y-%m-%d %H:%i:%s')
                    FROM order_reminder r
                    WHERE r.order_id = o.order_id
                      AND r.target_type = 'MERCHANT'
                      AND r.target_id = #{merchantId}
                      AND r.status <> 'HANDLED'
                      AND o.status IN (0, 1, 2, 3)
                ) AS latestReminderTime,
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
                o.order_time,
                o.is_urged,
                o.rider_urge_count,
                o.rider_urge_time,
                o.required_rider_level,
                o.required_rider_title
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
                o.is_urged AS isUrged,
                CASE WHEN o.status IN (0, 1, 2, 3) THEN IFNULL(o.rider_urge_count, 0) ELSE 0 END AS riderUrgeCount,
                DATE_FORMAT(o.rider_urge_time, '%Y-%m-%d %H:%i:%s') AS riderUrgeTime
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

    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
            WHERE order_id = #{orderId}
              AND merchant_id = #{merchantId}
              AND status = 2
            """)
    int countWaitRiderOrder(@Param("merchantId") Integer merchantId,
                            @Param("orderId") Integer orderId);

    @Update("""
            UPDATE order_reminder
            SET status = 'HANDLED',
                handled_time = NOW()
            WHERE target_type = 'MERCHANT'
              AND target_id = #{merchantId}
              AND order_id = #{orderId}
              AND status <> 'HANDLED'
            """)
    int handleMerchantReminders(@Param("merchantId") Integer merchantId,
                                @Param("orderId") Integer orderId);

    @Insert("""
            INSERT INTO order_status_log(order_id, status, status_text, operator_type, operator_id, remark)
            VALUES(#{orderId}, #{status}, #{statusText}, 'MERCHANT', #{merchantId}, #{remark})
            """)
    int insertStatusLog(@Param("merchantId") Integer merchantId,
                        @Param("orderId") Integer orderId,
                        @Param("status") Integer status,
                        @Param("statusText") String statusText,
                        @Param("remark") String remark);

    default int callRider(Integer merchantId, Integer orderId) {
        return countWaitRiderOrder(merchantId, orderId);
    }
}