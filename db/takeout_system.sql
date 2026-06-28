/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : takeout_system

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 28/06/2026 12:22:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for contact_msg
-- ----------------------------
DROP TABLE IF EXISTS `contact_msg`;
CREATE TABLE `contact_msg`  (
  `msg_id` int(0) NOT NULL AUTO_INCREMENT,
  `session_id` int(0) NOT NULL,
  `sender_id` int(0) NOT NULL,
  `sender_role` tinyint(0) NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_read` tinyint(0) NULL DEFAULT 0,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`msg_id`) USING BTREE,
  INDEX `idx_session`(`session_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contact_msg
-- ----------------------------
INSERT INTO `contact_msg` VALUES (1, 1, 1, 4, '你好', 1, '2026-06-25 20:38:37');
INSERT INTO `contact_msg` VALUES (2, 2, 1, 4, '你好', 0, '2026-06-26 00:14:56');
INSERT INTO `contact_msg` VALUES (3, 3, 1, 4, '你好', 1, '2026-06-26 00:15:11');
INSERT INTO `contact_msg` VALUES (4, 3, 1, 4, '1', 1, '2026-06-26 00:31:17');
INSERT INTO `contact_msg` VALUES (5, 4, 2, 1, '测试', 1, '2026-06-26 00:40:28');
INSERT INTO `contact_msg` VALUES (6, 5, 1, 4, '2', 1, '2026-06-28 00:57:47');
INSERT INTO `contact_msg` VALUES (7, 5, 3, 2, 'sa sad sadsa', 0, '2026-06-28 00:57:57');
INSERT INTO `contact_msg` VALUES (8, 4, 3, 2, 'aasd a sds', 0, '2026-06-28 00:58:15');
INSERT INTO `contact_msg` VALUES (9, 1, 3, 2, 'dsds', 0, '2026-06-28 00:58:17');

-- ----------------------------
-- Table structure for contact_session
-- ----------------------------
DROP TABLE IF EXISTS `contact_session`;
CREATE TABLE `contact_session`  (
  `session_id` int(0) NOT NULL AUTO_INCREMENT,
  `initiator_id` int(0) NOT NULL,
  `initiator_role` tinyint(0) NOT NULL,
  `target_id` int(0) NOT NULL,
  `target_role` tinyint(0) NOT NULL,
  `order_id` int(0) NULL DEFAULT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` tinyint(0) NULL DEFAULT 0,
  `last_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`session_id`) USING BTREE,
  INDEX `idx_initiator`(`initiator_id`, `initiator_role`) USING BTREE,
  INDEX `idx_target`(`target_id`, `target_role`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contact_session
-- ----------------------------
INSERT INTO `contact_session` VALUES (1, 1, 4, 3, 2, NULL, '联系商家：汉堡王商家', 1, 'dsds', '2026-06-25 20:38:37', '2026-06-28 00:58:17');
INSERT INTO `contact_session` VALUES (2, 1, 4, 7, 3, NULL, '联系骑手：李师傅骑手', 0, '你好', '2026-06-26 00:14:55', '2026-06-26 00:14:55');
INSERT INTO `contact_session` VALUES (3, 1, 4, 4, 3, NULL, '联系骑手：极速骑手', 1, '1', '2026-06-26 00:15:11', '2026-06-26 00:31:17');
INSERT INTO `contact_session` VALUES (4, 2, 1, 3, 2, 12, '联系商家：汉堡王校园店', 1, 'aasd a sds', '2026-06-26 00:40:28', '2026-06-28 00:58:15');
INSERT INTO `contact_session` VALUES (5, 1, 4, 3, 2, NULL, '联系商家：汉堡王商家', 1, 'sa sad sadsa', '2026-06-28 00:57:47', '2026-06-28 00:57:57');

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `coupon_id` int(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(10, 2) NOT NULL,
  `min_amount` decimal(10, 2) NULL DEFAULT 0.00,
  `start_time` datetime(0) NOT NULL,
  `end_time` datetime(0) NOT NULL,
  `status` tinyint(0) NULL DEFAULT 1,
  `total_stock` int(0) NOT NULL DEFAULT 0 COMMENT '总库存',
  `remaining_stock` int(0) NOT NULL DEFAULT 0 COMMENT '剩余库存，高并发领券按此字段原子扣减',
  `receive_count` int(0) NOT NULL DEFAULT 0 COMMENT '已领取数量',
  `per_user_limit` int(0) NOT NULL DEFAULT 1 COMMENT '每个用户限领数量',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`coupon_id`) USING BTREE,
  INDEX `idx_coupon_status_time_stock`(`status`, `start_time`, `end_time`, `remaining_stock`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (1, '新用户满30减5', 5.00, 30.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 1000, 999, 1, 1, '2026-06-25 17:46:52', '2026-06-26 00:17:27');
INSERT INTO `coupon` VALUES (2, '午餐满50减10', 10.00, 50.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 500, 499, 1, 1, '2026-06-25 17:46:52', '2026-06-26 00:17:26');
INSERT INTO `coupon` VALUES (3, '限时满80减18', 18.00, 80.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 200, 199, 1, 1, '2026-06-25 17:46:52', '2026-06-26 00:17:22');

-- ----------------------------
-- Table structure for customer_service_message
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_message`;
CREATE TABLE `customer_service_message`  (
  `message_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '客服消息ID',
  `ticket_id` int(0) NOT NULL COMMENT '工单ID',
  `sender_id` int(0) NOT NULL COMMENT '发送人ID',
  `sender_role` tinyint(0) NOT NULL COMMENT '1-用户, 4-平台管理员/客服',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `content_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'TEXT' COMMENT '消息类型',
  `is_read` tinyint(0) NOT NULL DEFAULT 0 COMMENT '0-未读, 1-已读',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发送时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_csm_ticket_time`(`ticket_id`, `create_time`) USING BTREE,
  INDEX `idx_csm_unread`(`ticket_id`, `sender_role`, `is_read`) USING BTREE,
  INDEX `fk_csm_sender`(`sender_id`) USING BTREE,
  CONSTRAINT `fk_csm_sender` FOREIGN KEY (`sender_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_csm_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `customer_service_ticket` (`ticket_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_service_message
-- ----------------------------
INSERT INTO `customer_service_message` VALUES (1, 1, 2, 1, '测试测试', 'TEXT', 1, '2026-06-25 18:21:59');
INSERT INTO `customer_service_message` VALUES (2, 1, 2, 1, '我', 'TEXT', 1, '2026-06-25 18:22:05');
INSERT INTO `customer_service_message` VALUES (3, 2, 2, 1, '啊', 'TEXT', 1, '2026-06-25 18:22:19');
INSERT INTO `customer_service_message` VALUES (4, 1, 1, 4, '你好', 'TEXT', 1, '2026-06-25 18:22:40');

-- ----------------------------
-- Table structure for customer_service_ticket
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_ticket`;
CREATE TABLE `customer_service_ticket`  (
  `ticket_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '客服工单ID',
  `user_id` int(0) NOT NULL COMMENT '发起用户ID',
  `order_id` int(0) NULL DEFAULT NULL COMMENT '关联订单ID，可为空',
  `admin_id` int(0) NULL DEFAULT NULL COMMENT '处理管理员ID',
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '其他问题' COMMENT '问题类型',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题标题',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '0-待处理, 1-处理中, 2-已解决, 3-已关闭',
  `priority` tinyint(0) NOT NULL DEFAULT 0 COMMENT '0-普通, 1-较高, 2-紧急',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `close_time` datetime(0) NULL DEFAULT NULL COMMENT '关闭时间',
  PRIMARY KEY (`ticket_id`) USING BTREE,
  INDEX `idx_cst_user_status`(`user_id`, `status`) USING BTREE,
  INDEX `idx_cst_admin_status`(`admin_id`, `status`) USING BTREE,
  INDEX `idx_cst_order`(`order_id`) USING BTREE,
  CONSTRAINT `fk_cst_admin` FOREIGN KEY (`admin_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_cst_order` FOREIGN KEY (`order_id`) REFERENCES `delivery_order` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_cst_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服工单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_service_ticket
-- ----------------------------
INSERT INTO `customer_service_ticket` VALUES (1, 2, NULL, 1, '订单问题', '我的钱没有退', 3, 0, '2026-06-25 18:21:59', '2026-06-25 18:23:25', '2026-06-25 18:23:25');
INSERT INTO `customer_service_ticket` VALUES (2, 2, NULL, 1, '订单问题', '测试', 1, 0, '2026-06-25 18:22:19', '2026-06-28 00:41:53', NULL);

-- ----------------------------
-- Table structure for delivery_order
-- ----------------------------
DROP TABLE IF EXISTS `delivery_order`;
CREATE TABLE `delivery_order`  (
  `order_id` int(0) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单编号',
  `user_id` int(0) NOT NULL COMMENT '下单用户ID',
  `merchant_id` int(0) NOT NULL COMMENT '接单商家ID',
  `rider_id` int(0) NULL DEFAULT NULL COMMENT '接单骑手ID',
  `address_id` int(0) NULL DEFAULT NULL COMMENT '收货地址ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货详细地址',
  `receiver_latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '收货纬度',
  `receiver_longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '收货经度',
  `total_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `product_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品金额',
  `delivery_fee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '配送费',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `actual_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '实际金额（应付金额）',
  `pay_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '实付金额',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '0-待商家接单, 1-商家已接单, 2-已出餐(待召唤骑手), 3-骑手配送中, 4-已完成',
  `pay_status` tinyint(0) NULL DEFAULT 0 COMMENT '0未支付 1已支付 2已退款',
  `order_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '下单时间',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `merchant_confirm_time` datetime(0) NULL DEFAULT NULL COMMENT '商家接单时间',
  `kitchen_finish_time` datetime(0) NULL DEFAULT NULL COMMENT '出餐时间',
  `estimated_arrival_time` datetime(0) NULL DEFAULT NULL COMMENT '骑手预计送达时间',
  `finish_time` datetime(0) NULL DEFAULT NULL COMMENT '送达完成时间',
  `cancel_time` datetime(0) NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
  `rider_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '骑手电话（冗余，方便展示）',
  `rider_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '骑手姓名（冗余，方便展示）',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户收货地址',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单备注',
  `is_urged` tinyint(0) NULL DEFAULT 0 COMMENT '0-未催单, 1-已催单',
  `remind_count` int(0) NULL DEFAULT 0 COMMENT '催单次数',
  `last_remind_time` datetime(0) NULL DEFAULT NULL COMMENT '最后催单时间',
  `tip_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '用户给骑手打赏金额',
  `required_rider_level` tinyint(0) NULL DEFAULT 0 COMMENT '订单要求骑手等级：0普通 1闪电侠 2单王配送',
  `required_rider_title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '普通' COMMENT '订单要求骑手称号',
  `rider_urge_count` int(0) NULL DEFAULT 0 COMMENT '骑手催商家次数',
  `rider_urge_time` datetime(0) NULL DEFAULT NULL COMMENT '骑手最近催商家时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no`) USING BTREE,
  INDEX `idx_order_user_status_time`(`user_id`, `status`, `order_time`) USING BTREE,
  INDEX `idx_order_merchant_status_time`(`merchant_id`, `status`, `order_time`) USING BTREE,
  INDEX `idx_delivery_order_user_status`(`user_id`, `status`) USING BTREE,
  INDEX `idx_delivery_order_rider_status`(`rider_id`, `status`) USING BTREE,
  INDEX `idx_order_required_rider`(`status`, `required_rider_level`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of delivery_order
-- ----------------------------
INSERT INTO `delivery_order` VALUES (1, NULL, 2, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 43.00, 0.00, 0.00, 0.00, 0.00, 0.00, -1, 0, '2026-06-21 22:38:37', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '西苑校区 6 号宿舍楼', 'no ice', 1, 0, NULL, 0.00, 0, '普通', 0, NULL);
INSERT INTO `delivery_order` VALUES (4, 'OD20260624145905849849', 2, 6, NULL, 2, '测试', '17554896666', '地图选点位置', 34.663712, 112.372509, 23.00, 20.00, 3.00, 0.00, 23.00, 0.00, -1, 1, '2026-06-24 14:59:05', NULL, NULL, NULL, NULL, NULL, '2026-06-24 14:59:12', '用户主动取消', NULL, NULL, '地图选点位置', '', 0, 0, NULL, 0.00, 0, '普通', 0, NULL);
INSERT INTO `delivery_order` VALUES (5, 'OD20260624164715075359', 2, 6, NULL, 3, '1', '1111', '地图选点位置', 34.663750, 112.372499, 15.00, 12.00, 3.00, 0.00, 15.00, 0.00, -1, 1, '2026-06-24 16:47:15', NULL, NULL, NULL, NULL, NULL, '2026-06-24 16:47:33', '用户主动取消', NULL, NULL, '地图选点位置', '测试', 0, 0, NULL, 0.00, 0, '普通', 0, NULL);
INSERT INTO `delivery_order` VALUES (6, 'OD20260624164746666175', 2, 3, 4, 3, '1', '1111', '地图选点位置', 34.663750, 112.372499, 28.00, 25.00, 3.00, 0.00, 28.00, 0.00, 4, 1, '2026-06-24 16:47:46', NULL, '2026-06-24 16:47:55', '2026-06-24 16:52:38', '2026-06-24 17:56:53', '2026-06-24 17:27:22', NULL, NULL, '暂无电话', '极速骑手', '地图选点位置', '测试', 1, 2, '2026-06-24 17:27:00', 0.00, 0, '普通', 0, NULL);
INSERT INTO `delivery_order` VALUES (7, 'OD20260624173933666512', 2, 3, 4, 3, '1', '1111', '地图选点位置', 34.663750, 112.372499, 21.50, 18.50, 3.00, 0.00, 21.50, 0.00, 4, 1, '2026-06-24 17:39:33', NULL, '2026-06-24 17:39:43', '2026-06-24 17:39:46', '2026-06-24 18:09:54', '2026-06-24 17:40:03', NULL, NULL, '暂无电话', '极速骑手', '地图选点位置', '测试', 0, 0, NULL, 0.00, 0, '普通', 0, NULL);
INSERT INTO `delivery_order` VALUES (8, 'OD20260624213057129987', 2, 3, 4, 3, '1', '1111', '地图选点位置', 34.663750, 112.372499, 21.50, 18.50, 3.00, 0.00, 21.50, 0.00, 4, 1, '2026-06-24 21:30:57', NULL, '2026-06-24 21:31:08', '2026-06-25 13:47:06', '2026-06-25 14:17:29', '2026-06-25 14:27:32', NULL, NULL, '暂无电话', '极速骑手', '地图选点位置', '1', 0, 0, NULL, 0.00, 0, '普通骑手', 0, NULL);
INSERT INTO `delivery_order` VALUES (9, 'OD20260625142751649554', 2, 3, NULL, 3, '1', '1111', '地图选点位置', 34.663750, 112.372499, 28.00, 25.00, 3.00, 0.00, 28.00, 0.00, -1, 1, '2026-06-25 14:27:51', NULL, NULL, NULL, NULL, NULL, '2026-06-25 14:27:55', '用户主动取消', NULL, NULL, '地图选点位置', '', 0, 0, NULL, 0.00, 0, '普通骑手', 0, NULL);
INSERT INTO `delivery_order` VALUES (10, 'OD20260625143216415367', 2, 3, 4, 4, '小明', '1110111101', '河科大北苑', 34.664383, 112.369997, 28.00, 25.00, 3.00, 0.00, 28.00, 0.00, 4, 1, '2026-06-25 14:32:16', NULL, '2026-06-25 14:32:33', '2026-06-25 14:32:45', '2026-06-25 15:03:25', '2026-06-25 16:09:37', NULL, NULL, '暂无电话', '极速骑手', '河科大北苑', '备注测试', 0, 1, '2026-06-25 14:32:41', 0.00, 0, '普通骑手', 0, NULL);
INSERT INTO `delivery_order` VALUES (11, 'OD20260625162143705668', 2, 3, 4, 4, '小明', '1110111101', '河科大北苑', 34.664383, 112.369997, 21.50, 18.50, 3.00, 0.00, 21.50, 0.00, 4, 1, '2026-06-25 16:21:43', NULL, '2026-06-25 16:22:12', '2026-06-25 16:22:17', '2026-06-25 16:52:25', '2026-06-25 17:53:17', NULL, NULL, '暂无电话', '极速骑手', '河科大北苑', '少辣', 0, 0, NULL, 0.00, 0, '普通骑手', 0, NULL);
INSERT INTO `delivery_order` VALUES (12, 'OD20260626003310083644', 2, 3, 4, 4, '小明', '1110111101', '河科大北苑', 34.664383, 112.369997, 21.50, 18.50, 3.00, 0.00, 21.50, 0.00, 3, 1, '2026-06-26 00:33:10', NULL, '2026-06-26 00:33:24', '2026-06-26 00:34:11', '2026-06-26 01:04:13', NULL, NULL, NULL, '13800001111', '极速骑手', '河科大北苑', '', 1, 1, '2026-06-26 00:33:28', 0.00, 0, '普通骑手', 0, NULL);

-- ----------------------------
-- Table structure for merchant_info
-- ----------------------------
DROP TABLE IF EXISTS `merchant_info`;
CREATE TABLE `merchant_info`  (
  `merchant_id` int(0) NOT NULL COMMENT '商家用户ID，对应sys_user.user_id',
  `store_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '店铺名称',
  `store_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '店铺Logo',
  `store_notice` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '店铺公告',
  `store_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '门店地址',
  `store_longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '门店经度',
  `store_latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '门店纬度',
  `rating` decimal(2, 1) NULL DEFAULT 5.0 COMMENT '店铺评分',
  `monthly_sales` int(0) NULL DEFAULT 0 COMMENT '月售数量',
  `min_order_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '起送价',
  `delivery_fee` decimal(10, 2) NULL DEFAULT 3.00 COMMENT '配送费',
  `delivery_time` int(0) NULL DEFAULT 30 COMMENT '预计配送分钟数',
  `distance_km` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '距离公里',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '1营业 0休息',
  PRIMARY KEY (`merchant_id`) USING BTREE,
  CONSTRAINT `merchant_info_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of merchant_info
-- ----------------------------
INSERT INTO `merchant_info` VALUES (3, '汉堡王校园店', '/images/store-burger.jpg', '新用户下单立减，热卖汉堡套餐供应中', '西苑校区商业街一楼', 112.374500, 34.661200, 4.8, 420, 15.00, 3.00, 28, 0.80, 1);
INSERT INTO `merchant_info` VALUES (9, '蜜雪冰城', NULL, '好喝的蜜雪冰城', NULL, NULL, NULL, 5.0, 0, 0.00, 3.00, 30, 1.00, 1);

-- ----------------------------
-- Table structure for order_comment
-- ----------------------------
DROP TABLE IF EXISTS `order_comment`;
CREATE TABLE `order_comment`  (
  `comment_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '璇勪环ID',
  `order_id` int(0) NOT NULL COMMENT '璁㈠崟ID',
  `user_id` int(0) NOT NULL COMMENT '鐢ㄦ埛ID',
  `rating` tinyint(0) NULL DEFAULT 5 COMMENT '璇勫垎1-5',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '璇勪环鍐呭?',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '璇勪环鏃堕棿',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `fk_order_comment_order`(`order_id`) USING BTREE,
  INDEX `fk_order_comment_user`(`user_id`) USING BTREE,
  CONSTRAINT `fk_order_comment_order` FOREIGN KEY (`order_id`) REFERENCES `delivery_order` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_comment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_comment
-- ----------------------------

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `item_id` int(0) NOT NULL AUTO_INCREMENT,
  `order_id` int(0) NOT NULL COMMENT '所属订单ID',
  `product_id` int(0) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下单时商品名快照',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下单时图片快照',
  `quantity` int(0) NOT NULL COMMENT '数量',
  `price` decimal(10, 2) NOT NULL COMMENT '购买时的单价',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '小计金额',
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `idx_order_item_order`(`order_id`) USING BTREE,
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `delivery_order` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 2, NULL, NULL, 2, 18.50, NULL);
INSERT INTO `order_item` VALUES (2, 1, 3, NULL, NULL, 1, 6.00, NULL);
INSERT INTO `order_item` VALUES (5, 4, 6, '柠檬冰茶', '', 2, 10.00, 20.00);
INSERT INTO `order_item` VALUES (6, 5, 5, '珍珠奶茶', '', 1, 12.00, 12.00);
INSERT INTO `order_item` VALUES (7, 6, 1, '经典双层芝士汉堡', '/images/burger1.jpg', 1, 25.00, 25.00);
INSERT INTO `order_item` VALUES (8, 7, 2, '香辣鸡腿堡', '/images/burger2.jpg', 1, 18.50, 18.50);
INSERT INTO `order_item` VALUES (9, 8, 2, '香辣鸡腿堡', '/images/burger2.jpg', 1, 18.50, 18.50);
INSERT INTO `order_item` VALUES (10, 9, 1, '经典双层芝士汉堡', '/images/burger1.jpg', 1, 25.00, 25.00);
INSERT INTO `order_item` VALUES (11, 10, 1, '经典双层芝士汉堡', '/images/burger1.jpg', 1, 25.00, 25.00);
INSERT INTO `order_item` VALUES (12, 11, 2, '香辣鸡腿堡', '/images/burger2.jpg', 1, 18.50, 18.50);
INSERT INTO `order_item` VALUES (13, 12, 2, '香辣鸡腿堡', '/images/burger2.jpg', 1, 18.50, 18.50);

-- ----------------------------
-- Table structure for order_reminder
-- ----------------------------
DROP TABLE IF EXISTS `order_reminder`;
CREATE TABLE `order_reminder`  (
  `reminder_id` int(0) NOT NULL AUTO_INCREMENT,
  `order_id` int(0) NOT NULL,
  `user_id` int(0) NOT NULL,
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MERCHANT/RIDER',
  `target_id` int(0) NOT NULL COMMENT '提醒对象ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提醒内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'UNREAD' COMMENT 'UNREAD/READ/HANDLED',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `read_time` datetime(0) NULL DEFAULT NULL COMMENT '已读时间',
  `handled_time` datetime(0) NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`reminder_id`) USING BTREE,
  INDEX `idx_reminder_order`(`order_id`) USING BTREE,
  INDEX `idx_reminder_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_reminder
-- ----------------------------
INSERT INTO `order_reminder` VALUES (1, 6, 2, 'MERCHANT', 3, '用户催单：订单 OD20260624164746666175 请尽快处理', 'UNREAD', '2026-06-24 17:01:57', NULL, NULL);
INSERT INTO `order_reminder` VALUES (2, 6, 2, 'RIDER', 4, '用户催单：订单 OD20260624164746666175 请尽快处理', 'UNREAD', '2026-06-24 17:27:00', NULL, NULL);
INSERT INTO `order_reminder` VALUES (3, 10, 2, 'MERCHANT', 3, '用户催单：订单 OD20260625143216415367 请尽快处理', 'HANDLED', '2026-06-25 14:32:41', NULL, '2026-06-25 14:32:45');
INSERT INTO `order_reminder` VALUES (4, 12, 2, 'MERCHANT', 3, '用户催单：订单 OD20260626003310083644 请尽快处理', 'UNREAD', '2026-06-26 00:33:28', NULL, NULL);

-- ----------------------------
-- Table structure for order_status_log
-- ----------------------------
DROP TABLE IF EXISTS `order_status_log`;
CREATE TABLE `order_status_log`  (
  `log_id` int(0) NOT NULL AUTO_INCREMENT,
  `order_id` int(0) NOT NULL,
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '状态码',
  `status_text` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态文本',
  `old_status` tinyint(0) NULL DEFAULT NULL COMMENT '旧状态',
  `new_status` tinyint(0) NULL DEFAULT NULL COMMENT '新状态',
  `operator_id` int(0) NULL DEFAULT NULL,
  `operator_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作者类型：USER/MERCHANT/RIDER/SYSTEM',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `operator_id`(`operator_id`) USING BTREE,
  CONSTRAINT `order_status_log_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `delivery_order` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `order_status_log_ibfk_2` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_status_log
-- ----------------------------
INSERT INTO `order_status_log` VALUES (1, 4, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功', '2026-06-24 14:59:05');
INSERT INTO `order_status_log` VALUES (2, 4, -1, '用户取消订单', NULL, NULL, 2, 'USER', '用户主动取消', '2026-06-24 14:59:12');
INSERT INTO `order_status_log` VALUES (3, 5, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功；用户累计点餐数=1，匹配骑手等级=普通', '2026-06-24 16:47:15');
INSERT INTO `order_status_log` VALUES (4, 5, -1, '用户取消订单', NULL, NULL, 2, 'USER', '用户主动取消', '2026-06-24 16:47:33');
INSERT INTO `order_status_log` VALUES (5, 6, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功；用户累计点餐数=1，匹配骑手等级=普通', '2026-06-24 16:47:46');
INSERT INTO `order_status_log` VALUES (6, 6, 1, '商家已接单', NULL, NULL, 3, 'MERCHANT', '商家确认接单，开始制作', '2026-06-24 16:47:55');
INSERT INTO `order_status_log` VALUES (7, 6, 2, '商家已出餐', NULL, NULL, 3, 'MERCHANT', '订单进入骑手接单池，等待匹配骑手', '2026-06-24 16:52:38');
INSERT INTO `order_status_log` VALUES (8, 6, 2, '商家已召唤骑手', NULL, NULL, 3, 'MERCHANT', '订单已在骑手接单池中，按用户等级匹配骑手', '2026-06-24 16:52:40');
INSERT INTO `order_status_log` VALUES (9, 6, 2, '商家已召唤骑手', NULL, NULL, 3, 'MERCHANT', '订单已在骑手接单池中，按用户等级匹配骑手', '2026-06-24 16:52:42');
INSERT INTO `order_status_log` VALUES (10, 6, 2, '商家已召唤骑手', NULL, NULL, 3, 'MERCHANT', '订单已在骑手接单池中，按用户等级匹配骑手', '2026-06-24 17:00:49');
INSERT INTO `order_status_log` VALUES (11, 6, 2, '用户已催单', NULL, NULL, 2, 'USER', '催单对象：MERCHANT', '2026-06-24 17:01:57');
INSERT INTO `order_status_log` VALUES (12, 6, 3, '骑手已接单', NULL, NULL, 4, 'RIDER', '订单进入配送中', '2026-06-24 17:26:53');
INSERT INTO `order_status_log` VALUES (13, 6, 3, '用户已催单', NULL, NULL, 2, 'USER', '催单对象：RIDER', '2026-06-24 17:27:00');
INSERT INTO `order_status_log` VALUES (14, 6, 4, '骑手已送达', NULL, NULL, 4, 'RIDER', '配送完成', '2026-06-24 17:27:22');
INSERT INTO `order_status_log` VALUES (15, 6, 4, '用户评价订单', NULL, NULL, 2, 'USER', '评分：1', '2026-06-24 17:27:30');
INSERT INTO `order_status_log` VALUES (16, 7, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功；用户累计点餐数=2，匹配骑手等级=普通', '2026-06-24 17:39:33');
INSERT INTO `order_status_log` VALUES (17, 7, 1, '商家已接单', NULL, NULL, 3, 'MERCHANT', '商家确认接单，开始制作', '2026-06-24 17:39:43');
INSERT INTO `order_status_log` VALUES (18, 7, 2, '商家已出餐', NULL, NULL, 3, 'MERCHANT', '订单进入骑手接单池，等待匹配骑手', '2026-06-24 17:39:46');
INSERT INTO `order_status_log` VALUES (19, 7, 3, '骑手已接单', NULL, NULL, 4, 'RIDER', '订单进入配送中', '2026-06-24 17:39:54');
INSERT INTO `order_status_log` VALUES (20, 7, 4, '骑手已送达', NULL, NULL, 4, 'RIDER', '配送完成，催单提醒已处理', '2026-06-24 17:40:03');
INSERT INTO `order_status_log` VALUES (21, 8, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功；用户累计点餐数=3，匹配骑手等级=普通骑手', '2026-06-24 21:30:57');
INSERT INTO `order_status_log` VALUES (22, 8, 1, '商家已接单', NULL, NULL, 3, 'MERCHANT', '商家确认接单，开始制作', '2026-06-24 21:31:08');
INSERT INTO `order_status_log` VALUES (23, 8, 2, '商家已出餐', NULL, NULL, 3, 'MERCHANT', '订单进入骑手接单池，等待匹配骑手', '2026-06-25 13:47:06');
INSERT INTO `order_status_log` VALUES (24, 8, 3, '骑手已接单', NULL, NULL, 4, 'RIDER', '订单进入配送中', '2026-06-25 13:47:29');
INSERT INTO `order_status_log` VALUES (25, 8, 4, '骑手已送达', NULL, NULL, 4, 'RIDER', '配送完成，催单提醒已处理', '2026-06-25 14:27:32');
INSERT INTO `order_status_log` VALUES (26, 9, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功；用户累计点餐数=4，匹配骑手等级=普通骑手', '2026-06-25 14:27:51');
INSERT INTO `order_status_log` VALUES (27, 9, -1, '用户取消订单', NULL, NULL, 2, 'USER', '用户主动取消', '2026-06-25 14:27:55');
INSERT INTO `order_status_log` VALUES (28, 10, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功；用户累计点餐数=4，匹配骑手等级=普通骑手', '2026-06-25 14:32:16');
INSERT INTO `order_status_log` VALUES (29, 10, 1, '商家已接单', NULL, NULL, 3, 'MERCHANT', '商家确认接单，开始制作', '2026-06-25 14:32:33');
INSERT INTO `order_status_log` VALUES (30, 10, 1, '用户已催单', NULL, NULL, 2, 'USER', '催单对象：MERCHANT', '2026-06-25 14:32:41');
INSERT INTO `order_status_log` VALUES (31, 10, 2, '商家已出餐', NULL, NULL, 3, 'MERCHANT', '订单进入骑手接单池，等待匹配骑手', '2026-06-25 14:32:45');
INSERT INTO `order_status_log` VALUES (32, 10, 3, '骑手已接单', NULL, NULL, 4, 'RIDER', '订单进入配送中', '2026-06-25 14:33:25');
INSERT INTO `order_status_log` VALUES (33, 10, 4, '骑手已送达', NULL, NULL, 4, 'RIDER', '配送完成，催单提醒已处理', '2026-06-25 16:09:37');
INSERT INTO `order_status_log` VALUES (34, 11, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功；用户累计点餐数=5，匹配骑手等级=普通骑手', '2026-06-25 16:21:43');
INSERT INTO `order_status_log` VALUES (35, 11, 1, '商家已接单', NULL, NULL, 3, 'MERCHANT', '商家确认接单，开始制作', '2026-06-25 16:22:12');
INSERT INTO `order_status_log` VALUES (36, 11, 2, '商家已出餐', NULL, NULL, 3, 'MERCHANT', '订单进入骑手接单池，等待匹配骑手', '2026-06-25 16:22:17');
INSERT INTO `order_status_log` VALUES (37, 11, 3, '骑手已接单', NULL, NULL, 4, 'RIDER', '订单进入配送中', '2026-06-25 16:22:25');
INSERT INTO `order_status_log` VALUES (38, 11, 4, '骑手已送达', NULL, NULL, 4, 'RIDER', '配送完成，催单提醒已处理', '2026-06-25 17:53:17');
INSERT INTO `order_status_log` VALUES (39, 12, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功；用户累计点餐数=6，匹配骑手等级=普通骑手', '2026-06-26 00:33:10');
INSERT INTO `order_status_log` VALUES (40, 12, 1, '用户已催单', NULL, NULL, 2, 'USER', '催单对象：MERCHANT', '2026-06-26 00:33:28');
INSERT INTO `order_status_log` VALUES (41, 11, 4, '用户评价订单', NULL, NULL, 2, 'USER', '评分：5', '2026-06-28 01:13:52');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `product_id` int(0) NOT NULL AUTO_INCREMENT,
  `merchant_id` int(0) NOT NULL COMMENT '所属商家ID',
  `category_id` int(0) NOT NULL COMMENT '所属分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜品/物品名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `stock` int(0) NULL DEFAULT 999 COMMENT '库存',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片路径',
  `upload_date` date NOT NULL COMMENT '上传日期',
  `order_count` int(0) NULL DEFAULT 0 COMMENT '累计点餐人数（用于推荐排序）',
  `rating` decimal(2, 1) NULL DEFAULT 5.0 COMMENT '商品评分',
  `monthly_sales` int(0) NULL DEFAULT 0 COMMENT '月销量',
  `tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签，如招牌/新品/热卖',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '1-上架, 0-下架',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `idx_product_category_status`(`category_id`, `status`) USING BTREE,
  INDEX `idx_product_merchant_status`(`merchant_id`, `status`) USING BTREE,
  INDEX `idx_product_name`(`name`) USING BTREE,
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `product_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 3, 1, '经典双层芝士汉堡', '芝士浓郁，牛肉多汁', 25.00, 996, '/images/burger1.jpg', '2026-06-19', 123, 4.8, 123, '热卖', 1);
INSERT INTO `product` VALUES (2, 3, 1, '香辣鸡腿堡', '外酥里嫩，微辣可口', 18.50, 995, '/images/burger2.jpg', '2026-06-19', 89, 4.8, 89, '热卖', 1);
INSERT INTO `product` VALUES (3, 3, 2, '冰镇可乐', '加冰口感更佳', 6.00, 999, '/images/cola.jpg', '2026-06-19', 200, 4.8, 200, '热卖', 1);
INSERT INTO `product` VALUES (4, 5, 1, '招牌鸡排饭', '大块鸡排配米饭和时蔬，适合午晚餐。', 16.80, 999, '', '2026-06-21', 236, 5.0, 0, NULL, 1);
INSERT INTO `product` VALUES (5, 6, 2, '珍珠奶茶', '默认三分糖，可在备注中调整糖度和冰量。', 12.00, 998, '', '2026-06-21', 311, 5.0, 1, NULL, 1);
INSERT INTO `product` VALUES (6, 6, 2, '柠檬冰茶', '清爽酸甜，适合下午自习提神。', 10.00, 997, '', '2026-06-21', 156, 5.0, 2, NULL, 1);

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `category_id` int(0) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES (1, '美食');
INSERT INTO `product_category` VALUES (2, '饮品');
INSERT INTO `product_category` VALUES (3, '跑腿代购');

-- ----------------------------
-- Table structure for product_review
-- ----------------------------
DROP TABLE IF EXISTS `product_review`;
CREATE TABLE `product_review`  (
  `review_id` int(0) NOT NULL AUTO_INCREMENT,
  `order_id` int(0) NOT NULL,
  `user_id` int(0) NOT NULL,
  `merchant_id` int(0) NOT NULL,
  `product_id` int(0) NULL DEFAULT NULL,
  `score` tinyint(0) NOT NULL COMMENT '1-5分',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`review_id`) USING BTREE,
  INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  INDEX `idx_review_merchant`(`merchant_id`, `create_time`) USING BTREE,
  CONSTRAINT `product_review_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `delivery_order` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `product_review_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `product_review_ibfk_3` FOREIGN KEY (`merchant_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `product_review_ibfk_4` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_review
-- ----------------------------
INSERT INTO `product_review` VALUES (1, 6, 2, 3, NULL, 1, '1', '2026-06-24 17:27:30');
INSERT INTO `product_review` VALUES (2, 11, 2, 3, NULL, 5, '11111', '2026-06-28 01:13:52');

-- ----------------------------
-- Table structure for rider_info
-- ----------------------------
DROP TABLE IF EXISTS `rider_info`;
CREATE TABLE `rider_info`  (
  `rider_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL COMMENT '关联用户表的骑手ID',
  `is_full_time` tinyint(0) NULL DEFAULT 0 COMMENT '0-兼职骑手, 1-全职骑手',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0-空闲, 1-忙碌中',
  `avg_speed` decimal(3, 1) NULL DEFAULT NULL COMMENT '平均速度(分钟/公里)',
  `total_finished_count` int(0) NULL DEFAULT 0 COMMENT '累计完成配送单数',
  `rider_level` tinyint(0) NULL DEFAULT 0 COMMENT '骑手等级：0普通 1闪电侠 2单王配送',
  `rider_title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '普通骑手' COMMENT '骑手等级称号',
  PRIMARY KEY (`rider_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `rider_info_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rider_info
-- ----------------------------
INSERT INTO `rider_info` VALUES (1, 4, 1, 0, 10.5, 5, 0, '普通骑手');
INSERT INTO `rider_info` VALUES (2, 7, 1, 0, 9.8, 0, 0, '普通骑手');

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart`  (
  `cart_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `product_id` int(0) NOT NULL,
  `quantity` int(0) NOT NULL DEFAULT 1,
  `checked` tinyint(0) NULL DEFAULT 1,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`cart_id`) USING BTREE,
  UNIQUE INDEX `uk_cart_user_product`(`user_id`, `product_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `shopping_cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `shopping_cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shopping_cart
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `role_type` tinyint(0) NOT NULL COMMENT '1-用户, 2-商家, 3-骑手, 4-管理员',
  `credit_score` int(0) NULL DEFAULT 0 COMMENT '用户信誉积分（仅用户端使用）',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '1-正常, 0-禁用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '注册时间',
  `level_id` int(0) NULL DEFAULT 1 COMMENT '用户等级ID',
  `growth_value` int(0) NULL DEFAULT 0 COMMENT '成长值',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', '系统管理员', NULL, 4, 0, 1, '2026-06-19 16:56:49', 1, 0);
INSERT INTO `sys_user` VALUES (2, 'zhangsan', '123456', '张三', NULL, 1, 10, 1, '2026-06-19 16:56:49', 1, 10);
INSERT INTO `sys_user` VALUES (3, 'burger_king', '123456', '汉堡王校园店', '17554894168', 2, 0, 1, '2026-06-19 16:56:49', 1, 0);
INSERT INTO `sys_user` VALUES (4, 'rider_knight', '123456', '极速骑手', NULL, 3, 0, 1, '2026-06-19 16:56:49', 1, 0);
INSERT INTO `sys_user` VALUES (5, 'xiyuan_food', '123456', '西苑快餐', '13600000001', 2, 0, 1, '2026-06-21 22:33:38', 1, 0);
INSERT INTO `sys_user` VALUES (6, 'tea_shop', '123456', '茶语小站', '13600000002', 2, 0, 1, '2026-06-21 22:33:38', 1, 0);
INSERT INTO `sys_user` VALUES (7, 'runner_li', '123456', '李师傅骑手', '13700000001', 3, 0, 1, '2026-06-21 22:33:38', 1, 0);
INSERT INTO `sys_user` VALUES (8, 'testuser99', '123456', 'Test', '', 1, 10, 1, '2026-06-21 22:36:55', 1, 0);
INSERT INTO `sys_user` VALUES (9, '1112', '111266', '蜜雪冰城', '17554894168', 2, 0, 1, '2026-06-25 18:24:41', 1, 0);

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `address_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '区县',
  `address_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签，如家/公司/学校',
  `is_default` tinyint(0) NULL DEFAULT 0 COMMENT '1默认 0普通',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`address_id`) USING BTREE,
  INDEX `idx_address_user_default`(`user_id`, `is_default`) USING BTREE,
  CONSTRAINT `user_address_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (4, 2, '小明', '1110111101', NULL, NULL, NULL, '河科大北苑', 34.664383, 112.369997, NULL, 1, '2026-06-25 14:31:40', '2026-06-25 14:31:40');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `user_coupon_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `coupon_id` int(0) NOT NULL,
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0未使用 1已使用 2已过期',
  `order_id` int(0) NULL DEFAULT NULL,
  `receive_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `use_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`user_coupon_id`) USING BTREE,
  UNIQUE INDEX `uk_user_coupon_once`(`user_id`, `coupon_id`) USING BTREE,
  INDEX `coupon_id`(`coupon_id`) USING BTREE,
  INDEX `order_id`(`order_id`) USING BTREE,
  CONSTRAINT `user_coupon_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_coupon_ibfk_2` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`coupon_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_coupon_ibfk_3` FOREIGN KEY (`order_id`) REFERENCES `delivery_order` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------
INSERT INTO `user_coupon` VALUES (1, 2, 3, 0, NULL, '2026-06-26 00:17:22', NULL);
INSERT INTO `user_coupon` VALUES (2, 2, 2, 0, NULL, '2026-06-26 00:17:26', NULL);
INSERT INTO `user_coupon` VALUES (3, 2, 1, 0, NULL, '2026-06-26 00:17:27', NULL);

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite`  (
  `favorite_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `target_type` tinyint(0) NOT NULL COMMENT '1商品 2商家',
  `target_id` int(0) NOT NULL,
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`favorite_id`) USING BTREE,
  UNIQUE INDEX `uk_fav_user_target`(`user_id`, `target_type`, `target_id`) USING BTREE,
  CONSTRAINT `user_favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite
-- ----------------------------
INSERT INTO `user_favorite` VALUES (1, 1, 2, 6, '2026-06-25 17:42:41');
INSERT INTO `user_favorite` VALUES (3, 2, 2, 3, '2026-06-26 00:32:15');

-- ----------------------------
-- Table structure for user_growth_log
-- ----------------------------
DROP TABLE IF EXISTS `user_growth_log`;
CREATE TABLE `user_growth_log`  (
  `log_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `order_id` int(0) NULL DEFAULT NULL,
  `change_value` int(0) NOT NULL COMMENT '变动值',
  `reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '变动原因',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `idx_growth_user`(`user_id`) USING BTREE,
  INDEX `idx_growth_order`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_growth_log
-- ----------------------------
INSERT INTO `user_growth_log` VALUES (1, 2, 6, 5, 'ORDER_REVIEW', '2026-06-24 17:27:30');
INSERT INTO `user_growth_log` VALUES (2, 2, 11, 5, 'ORDER_REVIEW', '2026-06-28 01:13:52');

-- ----------------------------
-- Table structure for user_level
-- ----------------------------
DROP TABLE IF EXISTS `user_level`;
CREATE TABLE `user_level`  (
  `level_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '等级ID',
  `level_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '等级名称',
  `min_growth` int(0) NOT NULL DEFAULT 0 COMMENT '最小成长值',
  `max_growth` int(0) NULL DEFAULT NULL COMMENT '最大成长值',
  `delivery_discount_rate` decimal(4, 2) NOT NULL DEFAULT 1.00 COMMENT '配送费折扣',
  `remind_cooldown_seconds` int(0) NOT NULL DEFAULT 180 COMMENT '催单冷却时间，单位秒',
  `priority_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否优先提醒：0否 1是',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '等级说明',
  PRIMARY KEY (`level_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户等级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_level
-- ----------------------------
INSERT INTO `user_level` VALUES (1, '普通用户', 0, 99, 1.00, 180, 0, '基础下单权益');
INSERT INTO `user_level` VALUES (2, '白银用户', 100, 499, 0.95, 150, 0, '配送费95折，催单冷却缩短');
INSERT INTO `user_level` VALUES (3, '黄金用户', 500, 999, 0.90, 120, 1, '配送费9折，商家端显示优先标识');
INSERT INTO `user_level` VALUES (4, '黑金用户', 1000, NULL, 0.80, 90, 1, '配送费8折，优先提醒商家和骑手');

SET FOREIGN_KEY_CHECKS = 1;
