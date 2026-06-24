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

 Date: 24/06/2026 14:59:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  PRIMARY KEY (`coupon_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------

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
<<<<<<< HEAD
=======
  `tip_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '用户给骑手打赏金额',
  `required_rider_level` tinyint(0) NULL DEFAULT 0 COMMENT '订单要求骑手等级：0普通 1闪电侠 2单王配送',
  `required_rider_title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '普通' COMMENT '订单要求骑手称号',
  `rider_urge_count` int(0) NULL DEFAULT 0 COMMENT '骑手催商家次数',
  `rider_urge_time` datetime(0) NULL DEFAULT NULL COMMENT '骑手最近催商家时间',
>>>>>>> origin/feature-user-rider-merchant
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no`) USING BTREE,
  INDEX `idx_order_user_status_time`(`user_id`, `status`, `order_time`) USING BTREE,
  INDEX `idx_order_merchant_status_time`(`merchant_id`, `status`, `order_time`) USING BTREE,
  INDEX `idx_delivery_order_user_status`(`user_id`, `status`) USING BTREE,
<<<<<<< HEAD
  INDEX `idx_delivery_order_rider_status`(`rider_id`, `status`) USING BTREE
=======
  INDEX `idx_delivery_order_rider_status`(`rider_id`, `status`) USING BTREE,
  INDEX `idx_order_required_rider`(`status`, `required_rider_level`) USING BTREE
>>>>>>> origin/feature-user-rider-merchant
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of delivery_order
-- ----------------------------
<<<<<<< HEAD
INSERT INTO `delivery_order` VALUES (1, NULL, 2, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 43.00, 0.00, 0.00, 0.00, 0.00, 0.00, -1, 0, '2026-06-21 22:38:37', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '西苑校区 6 号宿舍楼', 'no ice', 1, 0, NULL);
INSERT INTO `delivery_order` VALUES (4, 'OD20260624145905849849', 2, 6, NULL, 2, '测试', '17554896666', '地图选点位置', 34.663712, 112.372509, 23.00, 20.00, 3.00, 0.00, 23.00, 0.00, -1, 1, '2026-06-24 14:59:05', NULL, NULL, NULL, NULL, NULL, '2026-06-24 14:59:12', '用户主动取消', NULL, NULL, '地图选点位置', '', 0, 0, NULL);
=======
INSERT INTO `delivery_order` VALUES (1, NULL, 2, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 43.00, 0.00, 0.00, 0.00, 0.00, 0.00, -1, 0, '2026-06-21 22:38:37', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '西苑校区 6 号宿舍楼', 'no ice', 1, 0, NULL, 0.00, 0, '普通', 0, NULL);
INSERT INTO `delivery_order` VALUES (4, 'OD20260624145905849849', 2, 6, NULL, 2, '测试', '17554896666', '地图选点位置', 34.663712, 112.372509, 23.00, 20.00, 3.00, 0.00, 23.00, 0.00, -1, 1, '2026-06-24 14:59:05', NULL, NULL, NULL, NULL, NULL, '2026-06-24 14:59:12', '用户主动取消', NULL, NULL, '地图选点位置', '', 0, 0, NULL, 0.00, 0, '普通', 0, NULL);
>>>>>>> origin/feature-user-rider-merchant

-- ----------------------------
-- Table structure for merchant_info
-- ----------------------------
DROP TABLE IF EXISTS `merchant_info`;
CREATE TABLE `merchant_info`  (
  `merchant_id` int(0) NOT NULL COMMENT '商家用户ID，对应sys_user.user_id',
  `store_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '店铺名称',
  `store_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '店铺Logo',
  `store_notice` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '店铺公告',
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
INSERT INTO `merchant_info` VALUES (3, '汉堡王校园店', '/images/store-burger.jpg', '新用户下单立减，热卖汉堡套餐供应中', 4.8, 420, 15.00, 3.00, 28, 0.80, 1);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 2, NULL, NULL, 2, 18.50, NULL);
INSERT INTO `order_item` VALUES (2, 1, 3, NULL, NULL, 1, 6.00, NULL);
INSERT INTO `order_item` VALUES (5, 4, 6, '柠檬冰茶', '', 2, 10.00, 20.00);

-- ----------------------------
-- Table structure for order_reminder
-- ----------------------------
DROP TABLE IF EXISTS `order_reminder`;
CREATE TABLE `order_reminder`  (
  `reminder_id` int(0) NOT NULL AUTO_INCREMENT,
  `order_id` int(0) NOT NULL,
  `user_id` int(0) NOT NULL,
<<<<<<< HEAD
  `target_type` tinyint(0) NOT NULL COMMENT '提醒对象类型 1商家 2骑手',
=======
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MERCHANT/RIDER',
>>>>>>> origin/feature-user-rider-merchant
  `target_id` int(0) NOT NULL COMMENT '提醒对象ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提醒内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'UNREAD' COMMENT 'UNREAD/READ/HANDLED',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `read_time` datetime(0) NULL DEFAULT NULL COMMENT '已读时间',
  `handled_time` datetime(0) NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`reminder_id`) USING BTREE,
  INDEX `idx_reminder_order`(`order_id`) USING BTREE,
  INDEX `idx_reminder_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_reminder
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_status_log
-- ----------------------------
INSERT INTO `order_status_log` VALUES (1, 4, 0, '用户已下单', NULL, NULL, 2, 'USER', '订单创建成功', '2026-06-24 14:59:05');
INSERT INTO `order_status_log` VALUES (2, 4, -1, '用户取消订单', NULL, NULL, 2, 'USER', '用户主动取消', '2026-06-24 14:59:12');

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 3, 1, '经典双层芝士汉堡', '芝士浓郁，牛肉多汁', 25.00, 999, '/images/burger1.jpg', '2026-06-19', 120, 4.8, 120, '热卖', 1);
INSERT INTO `product` VALUES (2, 3, 1, '香辣鸡腿堡', '外酥里嫩，微辣可口', 18.50, 999, '/images/burger2.jpg', '2026-06-19', 85, 4.8, 85, '热卖', 1);
INSERT INTO `product` VALUES (3, 3, 2, '冰镇可乐', '加冰口感更佳', 6.00, 999, '/images/cola.jpg', '2026-06-19', 200, 4.8, 200, '热卖', 1);
INSERT INTO `product` VALUES (4, 5, 1, '招牌鸡排饭', '大块鸡排配米饭和时蔬，适合午晚餐。', 16.80, 999, '', '2026-06-21', 236, 5.0, 0, NULL, 1);
INSERT INTO `product` VALUES (5, 6, 2, '珍珠奶茶', '默认三分糖，可在备注中调整糖度和冰量。', 12.00, 999, '', '2026-06-21', 310, 5.0, 0, NULL, 1);
INSERT INTO `product` VALUES (6, 6, 2, '柠檬冰茶', '清爽酸甜，适合下午自习提神。', 10.00, 997, '', '2026-06-21', 156, 5.0, 2, NULL, 1);

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `category_id` int(0) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_review
-- ----------------------------

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
<<<<<<< HEAD
=======
  `total_finished_count` int(0) NULL DEFAULT 0 COMMENT '累计完成配送单数',
  `rider_level` tinyint(0) NULL DEFAULT 0 COMMENT '骑手等级：0普通 1闪电侠 2单王配送',
  `rider_title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '普通骑手' COMMENT '骑手等级称号',
>>>>>>> origin/feature-user-rider-merchant
  PRIMARY KEY (`rider_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `rider_info_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rider_info
-- ----------------------------
<<<<<<< HEAD
INSERT INTO `rider_info` VALUES (1, 4, 1, 0, 10.5);
INSERT INTO `rider_info` VALUES (2, 7, 1, 0, 9.8);
=======
INSERT INTO `rider_info` VALUES (1, 4, 1, 0, 10.5, 0, 0, '普通骑手');
INSERT INTO `rider_info` VALUES (2, 7, 1, 0, 9.8, 10, 1, '闪电侠骑手');
>>>>>>> origin/feature-user-rider-merchant

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', '系统管理员', NULL, 4, 0, 1, '2026-06-19 16:56:49', 1, 0);
INSERT INTO `sys_user` VALUES (2, 'zhangsan', '123456', '张三', NULL, 1, 10, 1, '2026-06-19 16:56:49', 1, 0);
INSERT INTO `sys_user` VALUES (3, 'burger_king', '123456', '汉堡王商家', NULL, 2, 0, 1, '2026-06-19 16:56:49', 1, 0);
INSERT INTO `sys_user` VALUES (4, 'rider_knight', '123456', '极速骑手', NULL, 3, 0, 1, '2026-06-19 16:56:49', 1, 0);
INSERT INTO `sys_user` VALUES (5, 'xiyuan_food', '123456', '西苑快餐', '13600000001', 2, 0, 1, '2026-06-21 22:33:38', 1, 0);
INSERT INTO `sys_user` VALUES (6, 'tea_shop', '123456', '茶语小站', '13600000002', 2, 0, 1, '2026-06-21 22:33:38', 1, 0);
INSERT INTO `sys_user` VALUES (7, 'runner_li', '123456', '李师傅骑手', '13700000001', 3, 0, 1, '2026-06-21 22:33:38', 1, 0);
INSERT INTO `sys_user` VALUES (8, 'testuser99', '123456', 'Test', '', 1, 10, 1, '2026-06-21 22:36:55', 1, 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (2, 2, '测试', '17554896666', NULL, NULL, NULL, '地图选点位置', 34.663712, 112.372509, NULL, 1, '2026-06-24 14:57:17', '2026-06-24 14:57:17');

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
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `coupon_id`(`coupon_id`) USING BTREE,
  INDEX `order_id`(`order_id`) USING BTREE,
  CONSTRAINT `user_coupon_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_coupon_ibfk_2` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`coupon_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_coupon_ibfk_3` FOREIGN KEY (`order_id`) REFERENCES `delivery_order` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_growth_log
-- ----------------------------

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

<<<<<<< HEAD
=======

-- ----------------------------
-- Merge patch: 商家/骑手/用户端接口对齐，支持打赏、骑手催单、高等级用户匹配高等级骑手
-- ----------------------------
ALTER TABLE `delivery_order`
  ADD COLUMN `tip_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '用户给骑手的打赏金额' AFTER `actual_amount`,
  ADD COLUMN `required_rider_level` tinyint(0) NULL DEFAULT 0 COMMENT '订单所需骑手等级：0普通 1闪电侠 2单王配送' AFTER `tip_amount`,
  ADD COLUMN `required_rider_title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '普通' COMMENT '订单所需骑手等级名称' AFTER `required_rider_level`,
  ADD COLUMN `rider_urge_count` int(0) NULL DEFAULT 0 COMMENT '骑手催商家出餐次数' AFTER `last_remind_time`,
  ADD COLUMN `rider_urge_time` datetime(0) NULL DEFAULT NULL COMMENT '骑手最近催商家出餐时间' AFTER `rider_urge_count`;

ALTER TABLE `order_reminder`
  MODIFY COLUMN `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提醒对象类型：MERCHANT/RIDER';

ALTER TABLE `rider_info`
  ADD COLUMN `rider_level` tinyint(0) NULL DEFAULT 0 COMMENT '骑手等级：0普通 1闪电侠 2单王骑手' AFTER `avg_speed`,
  ADD COLUMN `rider_title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '普通骑手' COMMENT '骑手等级名称' AFTER `rider_level`,
  ADD COLUMN `total_finished_count` int(0) NULL DEFAULT 0 COMMENT '累计完成配送单数' AFTER `rider_title`;

UPDATE `rider_info`
SET `total_finished_count` = 12,
    `rider_level` = 1,
    `rider_title` = '闪电侠骑手'
WHERE `user_id` = 4;

UPDATE `rider_info`
SET `total_finished_count` = 16,
    `rider_level` = 2,
    `rider_title` = '单王骑手'
WHERE `user_id` = 7;

UPDATE `delivery_order`
SET `tip_amount` = 0.00,
    `required_rider_level` = 0,
    `required_rider_title` = '普通',
    `rider_urge_count` = 0;

>>>>>>> origin/feature-user-rider-merchant
SET FOREIGN_KEY_CHECKS = 1;
