-- 1. 如果数据库已存在则删除，重新创建
DROP DATABASE IF EXISTS takeout_system;
CREATE DATABASE takeout_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 切换到该数据库
USE takeout_system;

-- ==================== 建表语句开始 ====================

-- 1. 用户表（包含管理员、商家、骑手、用户四种角色）
CREATE TABLE sys_user (
    user_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    PASSWORD VARCHAR(100) NOT NULL COMMENT '加密密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    role_type TINYINT NOT NULL COMMENT '1-用户, 2-商家, 3-骑手, 4-管理员',
    credit_score INT DEFAULT 0 COMMENT '用户信誉积分（仅用户端使用）',
    STATUS TINYINT DEFAULT 1 COMMENT '1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
);

-- 2. 商品分类表（食品、饮品、跑腿生活用品）
CREATE TABLE product_category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称'
);

-- 3. 商品表（含菜品和跑腿用品）
CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT NOT NULL COMMENT '所属商家ID',
    category_id INT NOT NULL COMMENT '所属分类ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品/物品名称',
    description VARCHAR(255) COMMENT '描述',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    image_url VARCHAR(255) COMMENT '图片路径',
    upload_date DATE NOT NULL COMMENT '上传日期',
    order_count INT DEFAULT 0 COMMENT '累计点餐人数（用于推荐排序）',
    status TINYINT DEFAULT 1 COMMENT '1-上架, 0-下架',
    FOREIGN KEY (merchant_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (category_id) REFERENCES product_category(category_id)
);

-- 4. 骑手信息表（额外记录骑手特征）
CREATE TABLE rider_info (
    rider_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT '关联用户表的骑手ID',
    is_full_time TINYINT DEFAULT 0 COMMENT '0-兼职骑手, 1-全职骑手',
    status TINYINT DEFAULT 0 COMMENT '0-空闲, 1-忙碌中',
    avg_speed DECIMAL(3,1) COMMENT '平均速度(分钟/公里)',
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id)
);

-- 5. 订单表（包含整个外卖流程的状态流转）
CREATE TABLE delivery_order (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT '下单用户ID',
    merchant_id INT NOT NULL COMMENT '接单商家ID',
    rider_id INT DEFAULT NULL COMMENT '接单骑手ID',
    total_price DECIMAL(10,2) NOT NULL COMMENT '订单总价',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-待商家接单, 1-商家已接单, 2-已出餐(待召唤骑手), 3-骑手配送中, 4-已完成',
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    merchant_confirm_time DATETIME COMMENT '商家接单时间',
    kitchen_finish_time DATETIME COMMENT '出餐时间',
    estimated_arrival_time DATETIME COMMENT '骑手预计送达时间',
    finish_time DATETIME COMMENT '送达完成时间',
    rider_phone VARCHAR(20) COMMENT '骑手电话（冗余，方便展示）',
    rider_name VARCHAR(50) COMMENT '骑手姓名（冗余，方便展示）',
    address VARCHAR(255) NOT NULL COMMENT '用户收货地址',
    remark VARCHAR(255) COMMENT '订单备注',
    is_urged TINYINT DEFAULT 0 COMMENT '0-未催单, 1-已催单'
);

-- 6. 订单详情表（记录点餐明细）
CREATE TABLE order_item (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL COMMENT '所属订单ID',
    product_id INT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '购买时的单价',
    FOREIGN KEY (order_id) REFERENCES delivery_order(order_id)
);

-- ==================== 建表语句结束 ====================

-- ==================== 插入测试数据 ====================
-- 插入用户 (密码为了测试用明文，实际开发需要 BCrypt 加密)
INSERT INTO sys_user (username, password, real_name, role_type, credit_score) VALUES 
('admin', '123456', '系统管理员', 4, 0),
('zhangsan', '123456', '张三', 1, 10),
('burger_king', '123456', '汉堡王商家', 2, 0),
('rider_knight', '123456', '极速骑手', 3, 0);

-- 插入骑手详细信息
INSERT INTO rider_info (user_id, is_full_time, status, avg_speed) VALUES 
(4, 1, 0, 10.5); -- 关联 user_id = 4 的骑手

-- 插入商品分类
INSERT INTO product_category (category_name) VALUES 
('美食'),
('饮品'),
('跑腿代购');

-- 插入测试商品 (假设商家ID为 3，汉堡王)
INSERT INTO product (merchant_id, category_id, name, description, price, image_url, upload_date, order_count) VALUES 
(3, 1, '经典双层芝士汉堡', '芝士浓郁，牛肉多汁', 25.00, '/images/burger1.jpg', CURDATE(), 120),
(3, 1, '香辣鸡腿堡', '外酥里嫩，微辣可口', 18.50, '/images/burger2.jpg', CURDATE(), 85),
(3, 2, '冰镇可乐', '加冰口感更佳', 6.00, '/images/cola.jpg', CURDATE(), 200);