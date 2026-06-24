package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.RiderOrderDetailVO;
import com.example.takeoutsystem.entity.RiderOrderItemVO;
import com.example.takeoutsystem.entity.RiderOrderVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 骑手订单 Mapper。
 * 订单池会按用户订单要求匹配骑手等级：普通骑手=0，闪电侠骑手=1，单王配送骑手=2。
 */
public interface RiderOrderMapper {

    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                COALESCE(mi.store_name, m.real_name) AS merchantName,
                o.status AS status,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
                DATE_FORMAT(o.kitchen_finish_time, '%Y-%m-%d %H:%i:%s') AS kitchenFinishTime,
                DATE_FORMAT(o.estimated_arrival_time, '%Y-%m-%d %H:%i:%s') AS estimatedArrivalTime,
                o.address AS address,
                o.remark AS remark,
                CASE WHEN o.status IN (2, 3) THEN IFNULL(o.is_urged, 0) ELSE 0 END AS isUrged,
                IFNULL(o.tip_amount, 0) AS tipAmount,
                IFNULL(o.required_rider_level, 0) AS requiredRiderLevel,
                IFNULL(o.required_rider_title, '普通骑手') AS requiredRiderTitle,
                IFNULL(o.rider_urge_count, 0) AS riderUrgeCount,
                DATE_FORMAT(o.rider_urge_time, '%Y-%m-%d %H:%i:%s') AS riderUrgeTime,
                TIMESTAMPDIFF(MINUTE, IFNULL(o.kitchen_finish_time, o.order_time), NOW()) AS waitMinutes,
                COALESCE(GROUP_CONCAT(CONCAT(COALESCE(oi.product_name, p.name), ' × ', oi.quantity) SEPARATOR '，'), '暂无商品明细') AS summary
            FROM delivery_order o
            LEFT JOIN sys_user u ON o.user_id = u.user_id
            LEFT JOIN sys_user m ON o.merchant_id = m.user_id
            LEFT JOIN merchant_info mi ON o.merchant_id = mi.merchant_id
            LEFT JOIN order_item oi ON o.order_id = oi.order_id
            LEFT JOIN product p ON oi.product_id = p.product_id
            WHERE o.status = 2
              AND IFNULL(o.required_rider_level, 0) <= COALESCE((
                    SELECT IFNULL(rider_level, 0)
                    FROM rider_info
                    WHERE user_id = #{riderUserId}
                    LIMIT 1
              ), 0)
            GROUP BY o.order_id, u.real_name, mi.store_name, m.real_name, o.status, o.total_price,
                     o.order_time, o.kitchen_finish_time, o.estimated_arrival_time, o.address,
                     o.remark, o.is_urged, o.tip_amount, o.required_rider_level,
                     o.required_rider_title, o.rider_urge_count, o.rider_urge_time
            ORDER BY IFNULL(o.required_rider_level, 0) DESC, o.is_urged DESC, waitMinutes DESC, o.total_price DESC
            """)
    List<RiderOrderVO> listAvailableOrders(@Param("riderUserId") Integer riderUserId);

    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                COALESCE(mi.store_name, m.real_name) AS merchantName,
                o.status AS status,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
                DATE_FORMAT(o.kitchen_finish_time, '%Y-%m-%d %H:%i:%s') AS kitchenFinishTime,
                DATE_FORMAT(o.estimated_arrival_time, '%Y-%m-%d %H:%i:%s') AS estimatedArrivalTime,
                o.address AS address,
                o.remark AS remark,
                CASE WHEN o.status IN (2, 3) THEN IFNULL(o.is_urged, 0) ELSE 0 END AS isUrged,
                IFNULL(o.tip_amount, 0) AS tipAmount,
                IFNULL(o.required_rider_level, 0) AS requiredRiderLevel,
                IFNULL(o.required_rider_title, '普通骑手') AS requiredRiderTitle,
                IFNULL(o.rider_urge_count, 0) AS riderUrgeCount,
                DATE_FORMAT(o.rider_urge_time, '%Y-%m-%d %H:%i:%s') AS riderUrgeTime,
                TIMESTAMPDIFF(MINUTE, IFNULL(o.kitchen_finish_time, o.order_time), NOW()) AS waitMinutes,
                COALESCE(GROUP_CONCAT(CONCAT(COALESCE(oi.product_name, p.name), ' × ', oi.quantity) SEPARATOR '，'), '暂无商品明细') AS summary
            FROM delivery_order o
            LEFT JOIN sys_user u ON o.user_id = u.user_id
            LEFT JOIN sys_user m ON o.merchant_id = m.user_id
            LEFT JOIN merchant_info mi ON o.merchant_id = mi.merchant_id
            LEFT JOIN order_item oi ON o.order_id = oi.order_id
            LEFT JOIN product p ON oi.product_id = p.product_id
            WHERE o.rider_id = #{riderUserId}
              AND (#{status} IS NULL OR o.status = #{status})
            GROUP BY o.order_id, u.real_name, mi.store_name, m.real_name, o.status, o.total_price,
                     o.order_time, o.kitchen_finish_time, o.estimated_arrival_time, o.address,
                     o.remark, o.is_urged, o.tip_amount, o.required_rider_level,
                     o.required_rider_title, o.rider_urge_count, o.rider_urge_time
            ORDER BY o.order_time DESC
            """)
    List<RiderOrderVO> listMyOrders(@Param("riderUserId") Integer riderUserId,
                                    @Param("status") Integer status);

    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                COALESCE(mi.store_name, m.real_name) AS merchantName,
                o.status AS status,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
                DATE_FORMAT(o.kitchen_finish_time, '%Y-%m-%d %H:%i:%s') AS kitchenFinishTime,
                DATE_FORMAT(o.estimated_arrival_time, '%Y-%m-%d %H:%i:%s') AS estimatedArrivalTime,
                DATE_FORMAT(o.finish_time, '%Y-%m-%d %H:%i:%s') AS finishTime,
                o.address AS address,
                o.remark AS remark,
                CASE WHEN o.status IN (2, 3) THEN IFNULL(o.is_urged, 0) ELSE 0 END AS isUrged
            FROM delivery_order o
            LEFT JOIN sys_user u ON o.user_id = u.user_id
            LEFT JOIN sys_user m ON o.merchant_id = m.user_id
            LEFT JOIN merchant_info mi ON o.merchant_id = mi.merchant_id
            WHERE o.order_id = #{orderId}
              AND (o.rider_id = #{riderUserId} OR o.status = 2)
            """)
    RiderOrderDetailVO getOrderDetail(@Param("riderUserId") Integer riderUserId,
                                      @Param("orderId") Integer orderId);

    @Select("""
            SELECT
                oi.product_id AS productId,
                COALESCE(oi.product_name, p.name) AS productName,
                oi.quantity AS quantity,
                oi.price AS price
            FROM order_item oi
            LEFT JOIN product p ON oi.product_id = p.product_id
            WHERE oi.order_id = #{orderId}
            ORDER BY oi.item_id ASC
            """)
    List<RiderOrderItemVO> listOrderItems(@Param("orderId") Integer orderId);

    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                COALESCE(mi.store_name, m.real_name) AS merchantName,
                o.status AS status,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
                DATE_FORMAT(o.kitchen_finish_time, '%Y-%m-%d %H:%i:%s') AS kitchenFinishTime,
                DATE_FORMAT(o.estimated_arrival_time, '%Y-%m-%d %H:%i:%s') AS estimatedArrivalTime,
                o.address AS address,
                o.remark AS remark,
                o.is_urged AS isUrged,
                IFNULL(o.tip_amount, 0) AS tipAmount,
                IFNULL(o.required_rider_level, 0) AS requiredRiderLevel,
                IFNULL(o.required_rider_title, '普通骑手') AS requiredRiderTitle,
                IFNULL(o.rider_urge_count, 0) AS riderUrgeCount,
                DATE_FORMAT(o.rider_urge_time, '%Y-%m-%d %H:%i:%s') AS riderUrgeTime,
                TIMESTAMPDIFF(MINUTE, o.order_time, NOW()) AS waitMinutes,
                COALESCE(GROUP_CONCAT(CONCAT(COALESCE(oi.product_name, p.name), ' × ', oi.quantity) SEPARATOR '，'), '暂无商品明细') AS summary
            FROM delivery_order o
            LEFT JOIN sys_user u ON o.user_id = u.user_id
            LEFT JOIN sys_user m ON o.merchant_id = m.user_id
            LEFT JOIN merchant_info mi ON o.merchant_id = mi.merchant_id
            LEFT JOIN order_item oi ON o.order_id = oi.order_id
            LEFT JOIN product p ON oi.product_id = p.product_id
            WHERE o.status = 1
            GROUP BY o.order_id, u.real_name, mi.store_name, m.real_name, o.status, o.total_price,
                     o.order_time, o.kitchen_finish_time, o.estimated_arrival_time, o.address,
                     o.remark, o.is_urged, o.tip_amount, o.required_rider_level,
                     o.required_rider_title, o.rider_urge_count, o.rider_urge_time
            ORDER BY o.rider_urge_count DESC, waitMinutes DESC, o.total_price DESC
            LIMIT 5
            """)
    List<RiderOrderVO> listWaitCookingOrders();

    @Update("""
            UPDATE delivery_order
            SET rider_urge_count = IFNULL(rider_urge_count, 0) + 1,
                rider_urge_time = NOW(),
                is_urged = 1
            WHERE order_id = #{orderId}
              AND status = 1
            """)
    int urgeMerchant(@Param("orderId") Integer orderId);

    @Update("""
            UPDATE delivery_order
            SET tip_amount = IFNULL(tip_amount, 0) + #{tipAmount}
            WHERE order_id = #{orderId}
              AND rider_id = #{riderUserId}
              AND status IN (3, 4)
            """)
    int addTip(@Param("riderUserId") Integer riderUserId,
               @Param("orderId") Integer orderId,
               @Param("tipAmount") java.math.BigDecimal tipAmount);

    @Update("""
            UPDATE delivery_order
            SET status = 3,
                rider_id = #{riderUserId},
                rider_name = #{riderName},
                rider_phone = #{riderPhone},
                estimated_arrival_time = DATE_ADD(NOW(), INTERVAL 30 MINUTE)
            WHERE order_id = #{orderId}
              AND status = 2
              AND IFNULL(required_rider_level, 0) <= COALESCE((
                    SELECT IFNULL(rider_level, 0)
                    FROM rider_info
                    WHERE user_id = #{riderUserId}
                    LIMIT 1
              ), 0)
            """)
    int acceptOrder(@Param("riderUserId") Integer riderUserId,
                    @Param("riderName") String riderName,
                    @Param("riderPhone") String riderPhone,
                    @Param("orderId") Integer orderId);

    @Update("""
            UPDATE delivery_order
            SET status = 4,
                finish_time = NOW(),
                is_urged = 0,
                rider_urge_count = 0,
                rider_urge_time = NULL
            WHERE order_id = #{orderId}
              AND rider_id = #{riderUserId}
              AND status = 3
            """)
    int finishOrder(@Param("riderUserId") Integer riderUserId,
                    @Param("orderId") Integer orderId);

    @Update("""
            UPDATE order_reminder
            SET status = 'HANDLED',
                handled_time = NOW()
            WHERE order_id = #{orderId}
              AND status <> 'HANDLED'
            """)
    int handleOrderReminders(@Param("orderId") Integer orderId);

    @Insert("""
            INSERT INTO order_status_log(order_id, status, status_text, operator_type, operator_id, remark)
            VALUES(#{orderId}, #{status}, #{statusText}, 'RIDER', #{riderUserId}, #{remark})
            """)
    int insertStatusLog(@Param("riderUserId") Integer riderUserId,
                        @Param("orderId") Integer orderId,
                        @Param("status") Integer status,
                        @Param("statusText") String statusText,
                        @Param("remark") String remark);

    @Insert("""
            INSERT INTO order_reminder(order_id, user_id, target_type, target_id, content, status)
            SELECT o.order_id, #{riderUserId}, 'MERCHANT', o.merchant_id, #{content}, 'UNREAD'
            FROM delivery_order o
            WHERE o.order_id = #{orderId}
              AND o.status = 1
            """)
    int insertMerchantReminder(@Param("riderUserId") Integer riderUserId,
                               @Param("orderId") Integer orderId,
                               @Param("content") String content);
}
