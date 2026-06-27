package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.*;
import com.example.takeoutsystem.mapper.UserAddressMapper;
import com.example.takeoutsystem.mapper.UserCouponMapper;
import com.example.takeoutsystem.mapper.UserOrderMapper;
import com.example.takeoutsystem.mapper.UserProductMapper;
import com.example.takeoutsystem.mapper.UserReminderMapper;
import com.example.takeoutsystem.service.UserLevelService;
import com.example.takeoutsystem.service.UserOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserOrderServiceImpl implements UserOrderService {
    private final UserOrderMapper userOrderMapper;
    private final UserProductMapper userProductMapper;
    private final UserAddressMapper userAddressMapper;
    private final UserReminderMapper userReminderMapper;
    private final UserCouponMapper userCouponMapper;
    private final UserLevelService userLevelService;

    public UserOrderServiceImpl(UserOrderMapper userOrderMapper,
                                UserProductMapper userProductMapper,
                                UserAddressMapper userAddressMapper,
                                UserReminderMapper userReminderMapper,
                                UserCouponMapper userCouponMapper,
                                UserLevelService userLevelService) {
        this.userOrderMapper = userOrderMapper;
        this.userProductMapper = userProductMapper;
        this.userAddressMapper = userAddressMapper;
        this.userReminderMapper = userReminderMapper;
        this.userCouponMapper = userCouponMapper;
        this.userLevelService = userLevelService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserOrderVO createOrder(Integer userId, UserOrderCreateForm form) {
        if (form == null || form.getItems() == null || form.getItems().isEmpty()) {
            throw new IllegalArgumentException("购物车为空");
        }
        UserAddress address = userAddressMapper.selectByIdAndUserId(form.getAddressId(), userId);
        if (address == null) throw new IllegalArgumentException("请选择有效收货地址");
        if (address.getLatitude() == null || address.getLongitude() == null) {
            throw new IllegalArgumentException("该地址缺少经纬度，请先定位或地图选点");
        }

        Map<Integer, Integer> quantityMap = aggregateQuantity(form.getItems());
        List<UserProductVO> products = userProductMapper.selectProductsByIds(new ArrayList<>(quantityMap.keySet()));
        if (products.size() != quantityMap.size()) throw new IllegalArgumentException("部分商品不存在或已下架");

        Integer merchantId = null;
        BigDecimal productAmount = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal minOrderAmount = BigDecimal.ZERO;
        for (UserProductVO p : products) {
            if (merchantId == null) merchantId = p.getMerchantId();
            if (!Objects.equals(merchantId, p.getMerchantId())) throw new IllegalArgumentException("一次订单只能选择同一商家的商品");
            int quantity = quantityMap.get(p.getProductId());
            if (p.getStock() != null && p.getStock() < quantity) throw new IllegalArgumentException(p.getName() + "库存不足");
            productAmount = productAmount.add(nvl(p.getPrice()).multiply(BigDecimal.valueOf(quantity)));
            deliveryFee = p.getDeliveryFee() == null ? new BigDecimal("3.00") : p.getDeliveryFee();
            minOrderAmount = p.getMinOrderAmount() == null ? BigDecimal.ZERO : p.getMinOrderAmount();
        }
        if (productAmount.compareTo(minOrderAmount) < 0) {
            throw new IllegalArgumentException("未达到商家起送价：" + minOrderAmount + "元");
        }

        UserLevelVO level = userLevelService.getCurrentLevel(userId);
        BigDecimal rate = level.getDeliveryDiscountRate() == null ? BigDecimal.ONE : level.getDeliveryDiscountRate();
        BigDecimal discountedDeliveryFee = deliveryFee.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountAmount = deliveryFee.subtract(discountedDeliveryFee).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualAmount = productAmount.add(discountedDeliveryFee).setScale(2, RoundingMode.HALF_UP);

        // 优惠券抵扣
        UserCouponVO appliedCoupon = null;
        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (form.getUserCouponId() != null) {
            userCouponMapper.expireUserCoupons(userId);
            appliedCoupon = userCouponMapper.selectByIdAndUserId(form.getUserCouponId(), userId);
            if (appliedCoupon == null) throw new IllegalArgumentException("优惠券不存在");
            if (appliedCoupon.getUserCouponStatus() != null && appliedCoupon.getUserCouponStatus() != 0) {
                throw new IllegalArgumentException("该优惠券不可用");
            }
            if (appliedCoupon.getStatus() == null || appliedCoupon.getStatus() != 1) {
                throw new IllegalArgumentException("优惠券已失效");
            }
            java.util.Date now = new java.util.Date();
            if (appliedCoupon.getStartTime() != null && now.before(appliedCoupon.getStartTime())) {
                throw new IllegalArgumentException("优惠券还未到使用时间");
            }
            if (appliedCoupon.getEndTime() != null && now.after(appliedCoupon.getEndTime())) {
                throw new IllegalArgumentException("优惠券已过期");
            }
            if (appliedCoupon.getMinAmount() != null && productAmount.compareTo(appliedCoupon.getMinAmount()) < 0) {
                throw new IllegalArgumentException("订单金额未达到优惠券使用门槛：" + appliedCoupon.getMinAmount() + "元");
            }
            couponDiscount = nvl(appliedCoupon.getAmount()).setScale(2, RoundingMode.HALF_UP);
            actualAmount = actualAmount.subtract(couponDiscount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            discountAmount = discountAmount.add(couponDiscount).setScale(2, RoundingMode.HALF_UP);
        }

        int orderCountAfterThisOrder = userOrderMapper.countUserOrders(userId) + 1;
        int requiredRiderLevel = resolveRequiredRiderLevel(orderCountAfterThisOrder);
        String requiredRiderTitle = resolveRequiredRiderTitle(requiredRiderLevel);

        UserOrder order = new UserOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setTotalPrice(actualAmount);
        order.setProductAmount(productAmount);
        order.setDeliveryFee(deliveryFee);
        order.setDiscountAmount(discountAmount);
        order.setActualAmount(actualAmount);
        order.setTipAmount(BigDecimal.ZERO);
        order.setRequiredRiderLevel(requiredRiderLevel);
        order.setRequiredRiderTitle(requiredRiderTitle);
        order.setStatus(0);
        order.setPayStatus(1); // 课程演示：默认已支付，方便商家接单和骑手配送流程联调。
        order.setAddressId(address.getAddressId());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getAddressDetail());
        order.setReceiverLatitude(address.getLatitude());
        order.setReceiverLongitude(address.getLongitude());
        order.setAddress(address.getAddressDetail()); // 兼容原订单表字段
        order.setRemark(form.getRemark());
        order.setIsUrged(0);
        userOrderMapper.insertOrder(order);

        if (appliedCoupon != null && appliedCoupon.getUserCouponId() != null) {
            userCouponMapper.markCouponUsed(appliedCoupon.getUserCouponId(), order.getOrderId());
        }

        Map<Integer, UserProductVO> productMap = products.stream().collect(Collectors.toMap(UserProductVO::getProductId, p -> p));
        for (Map.Entry<Integer, Integer> entry : quantityMap.entrySet()) {
            UserProductVO p = productMap.get(entry.getKey());
            int quantity = entry.getValue();
            int updated = userProductMapper.decreaseStock(p.getProductId(), quantity);
            if (updated == 0) throw new IllegalArgumentException(p.getName() + "库存不足");
            userProductMapper.increaseSales(p.getProductId(), quantity);

            UserOrderItem item = new UserOrderItem();
            item.setOrderId(order.getOrderId());
            item.setProductId(p.getProductId());
            item.setProductName(p.getName());
            item.setImageUrl(p.getImageUrl());
            item.setQuantity(quantity);
            item.setPrice(p.getPrice());
            item.setSubtotalAmount(p.getPrice().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP));
            userOrderMapper.insertOrderItem(item);
        }
        userOrderMapper.insertStatusLog(order.getOrderId(), 0, "用户已下单", "USER", userId,
                "订单创建成功；用户累计点餐数=" + orderCountAfterThisOrder + "，匹配骑手等级=" + requiredRiderTitle);
        return getDetail(userId, order.getOrderId());
    }

    @Override
    public List<UserOrderVO> listOrders(Integer userId, String status) {
        List<UserOrderVO> orders = userOrderMapper.selectOrders(userId, blankToNull(status));
        for (UserOrderVO order : orders) {
            List<UserOrderItemVO> items = userOrderMapper.selectOrderItems(order.getOrderId());
            order.setItems(items);
            order.setSummary(items.stream()
                    .limit(3)
                    .map(i -> i.getProductName() + " × " + i.getQuantity())
                    .collect(Collectors.joining("，")));
        }
        return orders;
    }

    @Override
    public UserOrderVO getDetail(Integer userId, Integer orderId) {
        UserOrderVO order = userOrderMapper.selectOrderById(orderId, userId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        order.setItems(userOrderMapper.selectOrderItems(orderId));
        order.setStatusLogs(userOrderMapper.selectStatusLogs(orderId));
        order.setReminders(userReminderMapper.selectByOrderId(orderId));
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Integer userId, Integer orderId, String reason) {
        UserOrderVO order = userOrderMapper.selectOrderById(orderId, userId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!Integer.valueOf(0).equals(order.getStatus())) throw new IllegalArgumentException("当前状态不能取消订单");
        userOrderMapper.cancelOrder(orderId, userId, reason == null ? "用户主动取消" : reason);
        userOrderMapper.insertStatusLog(orderId, -1, "用户取消订单", "USER", userId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> urge(Integer userId, Integer orderId) {
        UserOrderVO order = userOrderMapper.selectOrderById(orderId, userId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        int status = order.getStatus() == null ? 0 : order.getStatus();
        if (status == -1 || status == 4) throw new IllegalArgumentException("订单已结束，不能催单");

        int cooldown = userLevelService.getReminderCooldownSeconds(userId);
        Date last = userOrderMapper.selectLastRemindTime(orderId, userId);
        if (last != null) {
            long seconds = (System.currentTimeMillis() - last.getTime()) / 1000;
            if (seconds < cooldown) throw new IllegalArgumentException("催单太频繁，请 " + (cooldown - seconds) + " 秒后再试");
        }

        String targetType;
        Integer targetId;
        if (status == 3 && order.getRiderId() != null) {
            targetType = "RIDER";
            targetId = order.getRiderId();
        } else {
            targetType = "MERCHANT";
            targetId = order.getMerchantId();
        }
        String content = "用户催单：订单 " + order.getOrderNo() + " 请尽快处理";
        userReminderMapper.insertReminder(orderId, userId, targetType, targetId, content);
        userOrderMapper.updateUrgeInfo(orderId);
        userOrderMapper.insertStatusLog(orderId, status, "用户已催单", "USER", userId, "催单对象：" + targetType);

        Map<String, Object> result = new HashMap<>();
        result.put("targetType", targetType);
        result.put("targetId", targetId);
        result.put("cooldownSeconds", cooldown);
        result.put("message", "已提醒" + ("RIDER".equals(targetType) ? "骑手" : "商家"));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> tip(Integer userId, Integer orderId, BigDecimal tipAmount) {
        UserOrderVO order = userOrderMapper.selectOrderById(orderId, userId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (tipAmount == null || tipAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("打赏金额必须大于0");
        }
        if (tipAmount.compareTo(new BigDecimal("100.00")) > 0) {
            throw new IllegalArgumentException("单次打赏金额不能超过100元");
        }
        int status = order.getStatus() == null ? 0 : order.getStatus();
        if (status != 3 && status != 4) {
            throw new IllegalArgumentException("骑手配送中或订单完成后才能打赏");
        }
        if (order.getRiderId() == null) {
            throw new IllegalArgumentException("订单还没有骑手，不能打赏");
        }
        BigDecimal amount = tipAmount.setScale(2, RoundingMode.HALF_UP);
        int updated = userOrderMapper.updateTipAmount(orderId, userId, amount);
        if (updated <= 0) throw new IllegalArgumentException("打赏失败，请稍后再试");
        userReminderMapper.insertReminder(orderId, userId, "RIDER", order.getRiderId(),
                "用户打赏：订单 " + order.getOrderNo() + " 获得打赏 " + amount + " 元");
        userOrderMapper.insertStatusLog(orderId, status, "用户打赏骑手", "USER", userId, "打赏金额：" + amount + "元");

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("riderId", order.getRiderId());
        result.put("tipAmount", amount);
        result.put("message", "已打赏骑手" + amount + "元");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void comment(Integer userId, Integer orderId, UserCommentForm form) {
        UserOrderVO order = userOrderMapper.selectOrderById(orderId, userId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!Integer.valueOf(4).equals(order.getStatus())) throw new IllegalArgumentException("订单完成后才能评价");
        if (userOrderMapper.countCommentByOrderId(orderId, userId) > 0) throw new IllegalArgumentException("该订单已评价");
        int score = form == null || form.getScore() == null ? 5 : Math.max(1, Math.min(5, form.getScore()));
        String content = form == null ? null : form.getContent();
        userOrderMapper.insertComment(orderId, userId, order.getMerchantId(), score, content);
        userLevelService.addGrowthForReview(userId, orderId);
        userOrderMapper.insertStatusLog(orderId, 4, "用户评价订单", "USER", userId, "评分：" + score);
    }

    private int resolveRequiredRiderLevel(int orderCountAfterThisOrder) {
        if (orderCountAfterThisOrder >= 15) return 2;
        if (orderCountAfterThisOrder >= 10) return 1;
        return 0;
    }

    private String resolveRequiredRiderTitle(int level) {
        if (level >= 2) return "单王配送骑手";
        if (level == 1) return "闪电侠骑手";
        return "普通骑手";
    }

    private Map<Integer, Integer> aggregateQuantity(List<UserOrderItemForm> items) {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (UserOrderItemForm item : items) {
            if (item == null || item.getProductId() == null) throw new IllegalArgumentException("商品ID不能为空");
            int quantity = item.getQuantity() == null ? 1 : item.getQuantity();
            if (quantity < 1) throw new IllegalArgumentException("商品数量必须大于0");
            map.put(item.getProductId(), map.getOrDefault(item.getProductId(), 0) + quantity);
        }
        return map;
    }

    private BigDecimal nvl(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
    private String blankToNull(String value) { return value == null || value.trim().isEmpty() || "all".equals(value) ? null : value.trim(); }
    private String generateOrderNo() {
        return "OD" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + (int)(Math.random() * 900 + 100);
    }
}
