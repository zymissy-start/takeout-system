package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.*;
import com.example.takeoutsystem.mapper.UserAddressMapper;
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
    private final UserLevelService userLevelService;

    public UserOrderServiceImpl(UserOrderMapper userOrderMapper,
                                UserProductMapper userProductMapper,
                                UserAddressMapper userAddressMapper,
                                UserReminderMapper userReminderMapper,
                                UserLevelService userLevelService) {
        this.userOrderMapper = userOrderMapper;
        this.userProductMapper = userProductMapper;
        this.userAddressMapper = userAddressMapper;
        this.userReminderMapper = userReminderMapper;
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

        UserOrder order = new UserOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setTotalPrice(actualAmount);
        order.setProductAmount(productAmount);
        order.setDeliveryFee(deliveryFee);
        order.setDiscountAmount(discountAmount);
        order.setActualAmount(actualAmount);
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
        userOrderMapper.insertStatusLog(order.getOrderId(), 0, "用户已下单", "USER", userId, "订单创建成功");
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
