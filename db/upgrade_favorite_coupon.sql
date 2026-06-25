-- 外卖点餐系统：我的收藏 + 高并发优惠券升级脚本
-- 若你已经导入过旧版 takeout_system.sql，不想重建库，可以单独执行本脚本。

ALTER TABLE coupon
    ADD COLUMN total_stock INT NOT NULL DEFAULT 0 COMMENT '总库存' AFTER status,
  ADD COLUMN remaining_stock INT NOT NULL DEFAULT 0 COMMENT '剩余库存，高并发领券按此字段原子扣减' AFTER total_stock,
  ADD COLUMN receive_count INT NOT NULL DEFAULT 0 COMMENT '已领取数量' AFTER remaining_stock,
  ADD COLUMN per_user_limit INT NOT NULL DEFAULT 1 COMMENT '每个用户限领数量' AFTER receive_count,
  ADD COLUMN create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP AFTER per_user_limit,
  ADD COLUMN update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER create_time;

UPDATE coupon
SET total_stock = IF(total_stock = 0, 1000, total_stock),
    remaining_stock = IF(remaining_stock = 0, 1000, remaining_stock),
    per_user_limit = IF(per_user_limit = 0, 1, per_user_limit)
WHERE coupon_id > 0;

CREATE INDEX idx_coupon_status_time_stock ON coupon(status, start_time, end_time, remaining_stock);

ALTER TABLE user_coupon
    ADD UNIQUE INDEX uk_user_coupon_once(user_id, coupon_id);

INSERT INTO coupon(title, amount, min_amount, start_time, end_time, status, total_stock, remaining_stock, receive_count, per_user_limit)
SELECT '新用户满30减5', 5.00, 30.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 1000, 1000, 0, 1
    WHERE NOT EXISTS (SELECT 1 FROM coupon WHERE title = '新用户满30减5');

INSERT INTO coupon(title, amount, min_amount, start_time, end_time, status, total_stock, remaining_stock, receive_count, per_user_limit)
SELECT '午餐满50减10', 10.00, 50.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 500, 500, 0, 1
    WHERE NOT EXISTS (SELECT 1 FROM coupon WHERE title = '午餐满50减10');

INSERT INTO coupon(title, amount, min_amount, start_time, end_time, status, total_stock, remaining_stock, receive_count, per_user_limit)
SELECT '限时满80减18', 18.00, 80.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 200, 200, 0, 1
    WHERE NOT EXISTS (SELECT 1 FROM coupon WHERE title = '限时满80减18');