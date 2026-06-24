package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantOrderDetailVO;
import com.example.takeoutsystem.entity.MerchantOrderItemVO;
import com.example.takeoutsystem.entity.MerchantOrderVO;
<<<<<<< HEAD
=======
import org.apache.ibatis.annotations.Insert;
>>>>>>> origin/feature-user-rider-merchant
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
<<<<<<< HEAD
/**
 * 商家订单 Mapper。
 *
 * 该接口负责订单列表、订单详情、订单商品明细查询，
 * 以及接单、出餐、召唤骑手等订单状态更新操作。
 */
public interface MerchantOrderMapper {
    /**
     * 查询商家订单列表。
     *
     * 使用 delivery_order、sys_user、order_item、product 四表关联，
     * 将订单基本信息、下单用户姓名和商品明细汇总成一条订单展示记录。
     * GROUP_CONCAT 用于把多个商品明细合并为一段文字，如：汉堡 × 1，可乐 × 1。
     */
=======

public interface MerchantOrderMapper {

>>>>>>> origin/feature-user-rider-merchant
    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                o.status AS status,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
<<<<<<< HEAD
=======
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
>>>>>>> origin/feature-user-rider-merchant
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
<<<<<<< HEAD
                o.order_time
=======
                o.order_time,
                o.is_urged,
                o.rider_urge_count,
                o.rider_urge_time,
                o.required_rider_level,
                o.required_rider_title
>>>>>>> origin/feature-user-rider-merchant
            ORDER BY o.order_time DESC
            LIMIT #{size}
            """)
    List<MerchantOrderVO> listRecentOrders(@Param("merchantId") Integer merchantId,
                                           @Param("size") Integer size);
<<<<<<< HEAD
=======

>>>>>>> origin/feature-user-rider-merchant
    @Select("""
            SELECT
                o.order_id AS orderId,
                u.real_name AS userName,
                o.status AS status,
                o.total_price AS totalPrice,
                DATE_FORMAT(o.order_time, '%Y-%m-%d %H:%i:%s') AS orderTime,
<<<<<<< HEAD
=======
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
>>>>>>> origin/feature-user-rider-merchant
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
<<<<<<< HEAD
                o.order_time
=======
                o.order_time,
                o.is_urged,
                o.rider_urge_count,
                o.rider_urge_time,
                o.required_rider_level,
                o.required_rider_title
>>>>>>> origin/feature-user-rider-merchant
            ORDER BY o.order_time DESC
            """)
    List<MerchantOrderVO> listOrders(@Param("merchantId") Integer merchantId,
                                     @Param("status") Integer status);

<<<<<<< HEAD
    /**
     * 查询订单详情。
     *
     * 订单详情页需要展示收货地址、备注、骑手信息、各流程时间等完整信息，
     * 因此这里直接从 delivery_order 表读取订单生命周期字段。
     */

=======
>>>>>>> origin/feature-user-rider-merchant
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
<<<<<<< HEAD
                o.is_urged AS isUrged
=======
                o.is_urged AS isUrged,
                CASE WHEN o.status IN (0, 1, 2, 3) THEN IFNULL(o.rider_urge_count, 0) ELSE 0 END AS riderUrgeCount,
                DATE_FORMAT(o.rider_urge_time, '%Y-%m-%d %H:%i:%s') AS riderUrgeTime
>>>>>>> origin/feature-user-rider-merchant
            FROM delivery_order o
            LEFT JOIN sys_user u ON o.user_id = u.user_id
            WHERE o.order_id = #{orderId}
              AND o.merchant_id = #{merchantId}
            """)
    MerchantOrderDetailVO getOrderDetail(@Param("merchantId") Integer merchantId,
                                         @Param("orderId") Integer orderId);

<<<<<<< HEAD
    /**
     * 查询订单商品明细。
     *
     * 一个订单可能包含多个商品，因此订单主表和订单详情表是一对多关系。
     */

=======
>>>>>>> origin/feature-user-rider-merchant
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
<<<<<<< HEAD
    /**
     * 商家确认接单。
     *
     * 只有 status = 0 的待接单订单才允许更新为 status = 1。
     * 同时记录 merchant_confirm_time，方便后续订单流程追踪。
     */
=======
>>>>>>> origin/feature-user-rider-merchant

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
<<<<<<< HEAD
    /**
     * 商家出餐完成。
     *
     * 只有已经接单的订单 status = 1 才可以进入待骑手接单状态 status = 2。
     */
=======
>>>>>>> origin/feature-user-rider-merchant

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
<<<<<<< HEAD
    /**
     * 召唤骑手。
     *
     * 课程设计中采用固定测试骑手演示配送流程，
     * 更新订单为配送中 status = 3，并写入骑手姓名、电话和预计送达时间。
     */
    @Update("""
            UPDATE delivery_order
            SET status = 3,
                rider_id = 4,
                rider_name = '极速骑手',
                rider_phone = '13800001111',
                estimated_arrival_time = DATE_ADD(NOW(), INTERVAL 30 MINUTE)
=======

    @Select("""
            SELECT COUNT(*)
            FROM delivery_order
>>>>>>> origin/feature-user-rider-merchant
            WHERE order_id = #{orderId}
              AND merchant_id = #{merchantId}
              AND status = 2
            """)
<<<<<<< HEAD
    int callRider(@Param("merchantId") Integer merchantId,
                  @Param("orderId") Integer orderId);
=======
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
>>>>>>> origin/feature-user-rider-merchant
}