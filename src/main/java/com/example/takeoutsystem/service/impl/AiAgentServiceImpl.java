package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.*;
import com.example.takeoutsystem.mapper.AiAgentMapper;
import com.example.takeoutsystem.service.AiAgentService;
import com.example.takeoutsystem.service.UserOrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiAgentServiceImpl implements AiAgentService {
    private final AiAgentMapper aiAgentMapper;
    private final DeepSeekClient deepSeekClient;
    private final UserOrderService userOrderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiAgentServiceImpl(AiAgentMapper aiAgentMapper,
                              DeepSeekClient deepSeekClient,
                              UserOrderService userOrderService) {
        this.aiAgentMapper = aiAgentMapper;
        this.deepSeekClient = deepSeekClient;
        this.userOrderService = userOrderService;
    }

    @Override
    public AiAgentSummaryVO getSummary(Integer userId) {
        AiAgentSummaryVO summary = new AiAgentSummaryVO();
        summary.setDeepSeekEnabled(deepSeekClient.isEnabled());
        summary.setWelcomeText(deepSeekClient.isEnabled()
                ? "DeepSeek 已接入。你可以直接告诉我口味、预算、人数，我可以帮你推荐、凑单、算优惠、查骑手，也可以在你确认后帮你下单。"
                : "当前未配置 DeepSeek Key，已启用本地规则推荐。配置 DEEPSEEK_API_KEY 后可使用更灵活的 AI 对话。"
        );

        List<AiProductVO> history = safeList(aiAgentMapper.selectHistoryProducts(userId, 6));
        List<AiProductVO> hot = safeList(aiAgentMapper.selectHotProducts(null, 8));

        summary.setHistoryProducts(history);
        summary.setRecommendations(mergeProducts(history, hot, 8));
        summary.setAvailableCoupons(safeList(aiAgentMapper.selectAvailableCoupons(userId)));

        AiOrderVO activeOrder = attachOrderDetails(aiAgentMapper.selectLatestActiveOrder(userId));
        summary.setActiveOrder(activeOrder);
        summary.setCouponPlan(buildCouponPlan(userId, null));

        return summary;
    }

    @Override
    public AiChatResponseVO chat(Integer userId, AiChatRequest request) {
        AiChatRequest req = request == null ? new AiChatRequest() : request;
        String message = normalize(req.getMessage());

        List<AiProductVO> hotProducts = safeList(aiAgentMapper.selectHotProducts(extractKeyword(message), 10));
        if (hotProducts.isEmpty()) {
            hotProducts = safeList(aiAgentMapper.selectHotProducts(null, 10));
        }

        List<AiProductVO> historyProducts = safeList(aiAgentMapper.selectHistoryProducts(userId, 6));
        List<AiProductVO> recommendations = mergeProducts(historyProducts, hotProducts, 10);

        AiCouponPlanVO plan = buildCouponPlan(userId, req);
        AiOrderVO activeOrder = attachOrderDetails(resolveOrder(userId, req.getOrderId()));

        AiChatResponseVO resp = new AiChatResponseVO();
        resp.setDeepSeekEnabled(deepSeekClient.isEnabled());
        resp.setRecommendations(recommendations);
        resp.setCouponPlan(plan);
        resp.setActiveOrder(activeOrder);

        String forcedIntent = normalize(req.getIntent());

        if ("create_order".equals(forcedIntent) && Boolean.TRUE.equals(req.getConfirmOrder())) {
            resp.setAction("create_order");
            createOrderByAgent(userId, resp, req, null);
            return resp;
        }

        String systemPrompt = buildAgentSystemPrompt();
        String userPrompt = buildAgentUserPrompt(message, req, recommendations, plan, activeOrder);

        String aiRaw = deepSeekClient.chat(systemPrompt, userPrompt);
        JsonNode actionJson = parseActionJson(aiRaw);

        if (actionJson == null) {
            resp.setFallback(true);
            resp.setAction(resolveLocalAction(message, forcedIntent));
            handleLocalAction(userId, resp, req, recommendations, plan, activeOrder);
            return resp;
        }

        String action = actionJson.path("action").asText("chat");
        String reply = actionJson.path("reply").asText("");

        resp.setFallback(false);
        resp.setAction(action);
        resp.setReply(reply);

        switch (action) {
            case "prepare_order":
                prepareOrder(resp, req, actionJson, recommendations, plan);
                break;
            case "create_order":
                createOrderByAgent(userId, resp, req, actionJson);
                break;
            case "contact_merchant":
            case "contact_rider":
                contactByAgent(userId, resp, req, activeOrder, action);
                break;
            case "track_order":
                resp.setActiveOrder(getRiderAndEta(userId, req.getOrderId()));
                resp.setNeedConfirm(false);
                if (blank(resp.getReply()) && resp.getActiveOrder() != null) {
                    AiOrderVO o = resp.getActiveOrder();
                    resp.setReply("当前订单「" + o.getOrderNo() + "」状态为「" + o.getStatusText()
                            + "」，骑手：" + nullToDash(o.getRiderName())
                            + "，预计：" + nullToDash(o.getEtaText()) + "。");
                }
                break;
            case "coupon_plan":
                resp.setCouponPlan(buildCouponPlan(userId, req));
                resp.setNeedConfirm(false);
                if (blank(resp.getReply())) {
                    resp.setReply(buildCouponText(resp.getCouponPlan()));
                }
                break;
            case "recommend_food":
                resp.setRecommendations(recommendations);
                resp.setNeedConfirm(false);
                if (blank(resp.getReply())) {
                    resp.setReply(buildRecommendText(recommendations));
                }
                break;
            default:
                resp.setNeedConfirm(false);
                if (blank(resp.getReply())) {
                    resp.setReply(localReply(message, recommendations, plan, activeOrder, Collections.emptyList()));
                }
                break;
        }

        return resp;
    }

    @Override
    public AiCouponPlanVO optimizeCoupons(Integer userId, AiChatRequest request) {
        return buildCouponPlan(userId, request);
    }

    @Override
    public AiOrderVO getRiderAndEta(Integer userId, Integer orderId) {
        AiOrderVO order = attachOrderDetails(resolveOrder(userId, orderId));
        if (order == null) {
            throw new IllegalArgumentException("当前没有可跟踪的订单");
        }
        return order;
    }

    @Override
    public Map<String, Object> summonRider(Integer userId, Integer orderId) {
        AiOrderVO order = resolveOrder(userId, orderId);
        if (order == null) {
            throw new IllegalArgumentException("当前没有可召唤骑手的订单");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        try {
            Map urgeResult = userOrderService.urge(userId, order.getOrderId());
            result.put("urge", urgeResult);
            result.put("message", "已帮你联系" + ("RIDER".equals(urgeResult.get("targetType")) ? "骑手" : "商家") + "。");
        } catch (Exception e) {
            result.put("message", e.getMessage());
        }

        result.put("order", getRiderAndEta(userId, order.getOrderId()));
        result.put("availableRiders", aiAgentMapper.selectAvailableRiders(order.getRequiredRiderLevel(), 3));
        return result;
    }

    private void handleLocalAction(Integer userId,
                                   AiChatResponseVO resp,
                                   AiChatRequest req,
                                   List<AiProductVO> recommendations,
                                   AiCouponPlanVO plan,
                                   AiOrderVO activeOrder) {
        String action = resp.getAction();

        switch (action) {
            case "prepare_order":
                prepareOrder(resp, req, null, recommendations, plan);
                break;
            case "create_order":
                createOrderByAgent(userId, resp, req, null);
                break;
            case "contact_merchant":
            case "contact_rider":
                contactByAgent(userId, resp, req, activeOrder, action);
                break;
            case "track_order":
                resp.setNeedConfirm(false);
                resp.setActiveOrder(attachOrderDetails(resolveOrder(userId, req.getOrderId())));
                if (resp.getActiveOrder() == null) {
                    resp.setReply("当前没有可跟踪的进行中订单。");
                } else {
                    AiOrderVO o = resp.getActiveOrder();
                    resp.setReply("当前订单「" + o.getOrderNo() + "」状态为「" + o.getStatusText()
                            + "」，骑手：" + nullToDash(o.getRiderName())
                            + "，预计：" + nullToDash(o.getEtaText()) + "。");
                }
                break;
            case "coupon_plan":
                resp.setNeedConfirm(false);
                resp.setCouponPlan(plan);
                resp.setReply(buildCouponText(plan));
                break;
            case "recommend_food":
                resp.setNeedConfirm(false);
                resp.setRecommendations(recommendations);
                resp.setReply(buildRecommendText(recommendations));
                break;
            default:
                resp.setNeedConfirm(false);
                resp.setReply(localReply(req.getMessage(), recommendations, plan, activeOrder, Collections.emptyList()));
                break;
        }
    }

    private String resolveLocalAction(String message, String forcedIntent) {
        if (!blank(forcedIntent)) {
            return forcedIntent;
        }

        String text = normalize(message);

        if (containsAny(text, "确认下单", "下单", "帮我点", "就这个", "就这些", "来一份", "买这个")) {
            return "prepare_order";
        }
        if (containsAny(text, "联系商家", "催商家", "商家怎么还", "提醒商家")) {
            return "contact_merchant";
        }
        if (containsAny(text, "联系骑手", "催骑手", "骑手在哪", "召唤骑手")) {
            return "contact_rider";
        }
        if (containsAny(text, "多久到", "什么时候到", "预计送达", "送到", "订单状态")) {
            return "track_order";
        }
        if (containsAny(text, "优惠", "便宜", "最划算", "省钱", "券")) {
            return "coupon_plan";
        }
        if (containsAny(text, "推荐", "吃什么", "喝什么", "想吃", "预算")) {
            return "recommend_food";
        }

        return "chat";
    }

    private String buildAgentSystemPrompt() {
        return ""
                + "你是外卖系统里的 AI 点餐 Agent，不是普通聊天机器人。"
                + "你必须根据系统给出的真实商品、优惠券、订单和骑手信息回答，不能编造不存在的商品、价格、优惠券、订单或骑手。"
                + "你可以自然语言回答，但必须同时判断用户意图，并输出纯 JSON。"
                + "不要输出 Markdown，不要输出代码块，不要在 JSON 外添加解释。"
                + "JSON 格式："
                + "{"
                + "\"action\":\"chat|recommend_food|prepare_order|create_order|contact_merchant|contact_rider|track_order|coupon_plan\","
                + "\"reply\":\"自然语言回复\","
                + "\"items\":[{\"productId\":1,\"quantity\":1}],"
                + "\"addressId\":1,"
                + "\"userCouponId\":1,"
                + "\"remark\":\"备注\""
                + "}"
                + "规则："
                + "1. 用户只是问吃什么，action=recommend_food。"
                + "2. 用户说帮我点、我要下单、就这些，先 action=prepare_order，必须让用户确认。"
                + "3. 只有用户明确说确认下单，并且请求里 confirmOrder=true，才 action=create_order。"
                + "4. 用户说联系商家、催商家，action=contact_merchant。"
                + "5. 用户说联系骑手、催骑手、骑手在哪，action=contact_rider 或 track_order。"
                + "6. 用户问怎么最便宜、优惠券怎么用，action=coupon_plan。"
                + "7. 如果缺地址，提醒用户先选择地址，不要直接下单。";
    }

    private String buildAgentUserPrompt(String message,
                                        AiChatRequest req,
                                        List<AiProductVO> recommendations,
                                        AiCouponPlanVO plan,
                                        AiOrderVO activeOrder) {
        StringBuilder sb = new StringBuilder();

        sb.append("用户输入：").append(message == null ? "" : message).append("\n");
        sb.append("是否确认下单：").append(Boolean.TRUE.equals(req.getConfirmOrder())).append("\n");
        sb.append("用户选择地址ID：").append(req.getAddressId()).append("\n");
        sb.append("用户选择优惠券ID：").append(req.getUserCouponId()).append("\n");
        sb.append("用户备注：").append(req.getRemark()).append("\n\n");

        sb.append("当前可选商品，只能从这里选：\n");
        for (AiProductVO p : recommendations) {
            sb.append("商品ID=").append(p.getProductId())
                    .append("，名称=").append(p.getName())
                    .append("，价格=").append(nvl(p.getPrice()))
                    .append("，商家=").append(nullToDash(p.getMerchantName()))
                    .append("，分类=").append(nullToDash(p.getCategoryName()))
                    .append("，月售=").append(p.getMonthlySales())
                    .append("\n");
        }

        sb.append("\n当前优惠方案：\n");
        sb.append("商品金额=").append(plan.getProductAmount())
                .append("，配送费=").append(plan.getDeliveryFee())
                .append("，优惠=").append(plan.getCouponDiscount())
                .append("，预计支付=").append(plan.getPayAmount())
                .append("\n");

        if (plan.getBestCoupon() != null) {
            sb.append("最优优惠券 userCouponId=")
                    .append(plan.getBestCoupon().getUserCouponId())
                    .append("，名称=")
                    .append(plan.getBestCoupon().getTitle())
                    .append("\n");
        }

        if (activeOrder != null) {
            sb.append("\n当前进行中订单：\n");
            sb.append("orderId=").append(activeOrder.getOrderId())
                    .append("，订单号=").append(activeOrder.getOrderNo())
                    .append("，状态=").append(activeOrder.getStatusText())
                    .append("，商家=").append(activeOrder.getMerchantName())
                    .append("，骑手=").append(activeOrder.getRiderName())
                    .append("，预计=").append(activeOrder.getEtaText())
                    .append("\n");
        } else {
            sb.append("\n当前没有进行中订单。\n");
        }

        return sb.toString();
    }

    private JsonNode parseActionJson(String aiRaw) {
        if (aiRaw == null || aiRaw.trim().isEmpty()) {
            return null;
        }

        String text = aiRaw.trim();

        if (text.startsWith("```")) {
            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();
        }

        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            text = text.substring(start, end + 1);
        }

        try {
            return objectMapper.readTree(text);
        } catch (Exception e) {
            return null;
        }
    }

    private void prepareOrder(AiChatResponseVO resp,
                              AiChatRequest req,
                              JsonNode actionJson,
                              List<AiProductVO> recommendations,
                              AiCouponPlanVO plan) {
        List<Map<String, Object>> items = parseItemsFromJson(actionJson);

        if (items.isEmpty() && req.getItems() != null) {
            for (AiCartItemForm item : req.getItems()) {
                if (item == null || item.getProductId() == null) {
                    continue;
                }
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("productId", item.getProductId());
                map.put("quantity", item.getQuantity() == null ? 1 : Math.max(1, item.getQuantity()));
                items.add(map);
            }
        }

        if (items.isEmpty() && !recommendations.isEmpty()) {
            AiProductVO first = recommendations.get(0);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("productId", first.getProductId());
            map.put("quantity", 1);
            items.add(map);
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("items", items);
        payload.put("addressId", req.getAddressId());
        payload.put("userCouponId", plan.getBestCoupon() == null ? req.getUserCouponId() : plan.getBestCoupon().getUserCouponId());
        payload.put("remark", req.getRemark());

        resp.setNeedConfirm(true);
        resp.setActionPayload(payload);

        if (blank(resp.getReply())) {
            String names = readableItemNames(items, recommendations);
            resp.setReply("我已经帮你整理好下单方案：" + names
                    + "。请确认收货地址、商品和优惠券后，再点击「确认下单」。如果还没有地址，请先在地址管理里选择或新增地址。");
        }
    }

    private void createOrderByAgent(Integer userId,
                                    AiChatResponseVO resp,
                                    AiChatRequest req,
                                    JsonNode actionJson) {
        if (!Boolean.TRUE.equals(req.getConfirmOrder())) {
            resp.setNeedConfirm(true);
            resp.setReply("下单前需要你确认。请检查商品、地址和优惠券后，再点击确认下单。");
            return;
        }

        Integer addressId = req.getAddressId();
        if (addressId == null && actionJson != null && actionJson.hasNonNull("addressId")) {
            addressId = actionJson.path("addressId").asInt();
        }

        if (addressId == null) {
            resp.setNeedConfirm(true);
            resp.setReply("还不能下单：缺少收货地址。请先选择地址。");
            return;
        }

        Integer userCouponId = req.getUserCouponId();
        if (userCouponId == null && actionJson != null && actionJson.hasNonNull("userCouponId")) {
            userCouponId = actionJson.path("userCouponId").asInt();
        }

        String remark = req.getRemark();
        if (blank(remark) && actionJson != null && actionJson.hasNonNull("remark")) {
            remark = actionJson.path("remark").asText();
        }

        List<UserOrderItemForm> items = new ArrayList<>();

        if (req.getItems() != null && !req.getItems().isEmpty()) {
            for (AiCartItemForm source : req.getItems()) {
                if (source == null || source.getProductId() == null) {
                    continue;
                }
                UserOrderItemForm item = new UserOrderItemForm();
                item.setProductId(source.getProductId());
                item.setQuantity(source.getQuantity() == null ? 1 : Math.max(1, source.getQuantity()));
                items.add(item);
            }
        } else {
            for (Map<String, Object> map : parseItemsFromJson(actionJson)) {
                if (map.get("productId") == null) {
                    continue;
                }
                UserOrderItemForm item = new UserOrderItemForm();
                item.setProductId(Integer.valueOf(String.valueOf(map.get("productId"))));
                item.setQuantity(Integer.valueOf(String.valueOf(map.getOrDefault("quantity", 1))));
                items.add(item);
            }
        }

        if (items.isEmpty()) {
            resp.setNeedConfirm(true);
            resp.setReply("还不能下单：没有选择商品。请告诉我你想点什么，或者先加入购物车。");
            return;
        }

        UserOrderCreateForm form = new UserOrderCreateForm();
        form.setAddressId(addressId);
        form.setItems(items);
        form.setUserCouponId(userCouponId);
        form.setRemark(remark);

        UserOrderVO order = userOrderService.createOrder(userId, form);

        resp.setCreatedOrder(order);
        resp.setActiveOrder(convertUserOrderToAiOrder(order));
        resp.setNeedConfirm(false);
        resp.setAction("create_order");
        resp.setReply("已为你完成下单，订单号：" + order.getOrderNo()
                + "。你可以继续问我骑手信息、预计送达时间，或者让我帮你联系商家。");
    }

    private void contactByAgent(Integer userId,
                                AiChatResponseVO resp,
                                AiChatRequest req,
                                AiOrderVO activeOrder,
                                String action) {
        Integer orderId = req.getOrderId();

        if (orderId == null && activeOrder != null) {
            orderId = activeOrder.getOrderId();
        }

        if (orderId == null) {
            resp.setNeedConfirm(false);
            resp.setReply("当前没有可联系商家或骑手的订单。请先下单，或者指定订单。");
            return;
        }

        Map<String, Object> result = userOrderService.urge(userId, orderId);

        String targetType = String.valueOf(result.getOrDefault("targetType", ""));
        String targetName = "RIDER".equals(targetType) ? "骑手" : "商家";

        resp.setNeedConfirm(false);
        resp.setAction(action);
        resp.setActionPayload(result);
        resp.setReply(String.valueOf(result.getOrDefault("message", "已帮你联系" + targetName + "。")));
    }

    private List<Map<String, Object>> parseItemsFromJson(JsonNode actionJson) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (actionJson == null || !actionJson.has("items") || !actionJson.get("items").isArray()) {
            return result;
        }

        for (JsonNode node : actionJson.get("items")) {
            if (!node.hasNonNull("productId")) {
                continue;
            }

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("productId", node.path("productId").asInt());
            map.put("quantity", Math.max(1, node.path("quantity").asInt(1)));
            result.add(map);
        }

        return result;
    }

    private AiCouponPlanVO buildCouponPlan(Integer userId, AiChatRequest request) {
        AiCouponPlanVO plan = new AiCouponPlanVO();
        List<AiCouponVO> coupons = safeList(aiAgentMapper.selectAvailableCoupons(userId));
        plan.setAvailableCoupons(coupons);

        List<AiProductVO> cartItems = resolveCartProducts(request == null ? null : request.getItems());
        plan.setCartItems(cartItems);

        BigDecimal productAmount = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal minOrderAmount = BigDecimal.ZERO;

        for (AiProductVO item : cartItems) {
            int quantity = item.getQuantity() == null ? 1 : item.getQuantity();
            BigDecimal subtotal = nvl(item.getPrice())
                    .multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, RoundingMode.HALF_UP);
            item.setSubtotalAmount(subtotal);
            productAmount = productAmount.add(subtotal);
            deliveryFee = item.getDeliveryFee() == null ? deliveryFee : item.getDeliveryFee();
            minOrderAmount = max(minOrderAmount, nvl(item.getMinOrderAmount()));
        }

        plan.setProductAmount(productAmount);
        plan.setDeliveryFee(deliveryFee);

        AiCouponVO best = null;
        BigDecimal bestDiscount = BigDecimal.ZERO;

        for (AiCouponVO coupon : coupons) {
            BigDecimal minAmount = nvl(coupon.getMinAmount());
            BigDecimal amount = nvl(coupon.getAmount());

            if (productAmount.compareTo(minAmount) >= 0) {
                coupon.setReason("当前购物车可用");
                if (amount.compareTo(bestDiscount) > 0) {
                    best = coupon;
                    bestDiscount = amount;
                }
            } else {
                coupon.setReason("还差￥" + minAmount.subtract(productAmount).setScale(2, RoundingMode.HALF_UP) + " 可用");
            }
        }

        plan.setBestCoupon(best);
        plan.setCouponDiscount(bestDiscount);
        plan.setPayAmount(productAmount.add(deliveryFee).subtract(bestDiscount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));

        List<String> suggestions = new ArrayList<>();
        if (cartItems.isEmpty()) {
            suggestions.add("购物车为空，可以先让 AI 推荐 1-2 个热卖菜品。优惠券方案会在有购物车金额后更准确。");
        } else if (productAmount.compareTo(minOrderAmount) < 0) {
            suggestions.add("当前未达到起送价，还差￥" + minOrderAmount.subtract(productAmount).setScale(2, RoundingMode.HALF_UP) + "。建议加一份饮品或小食。");
        }

        final BigDecimal finalProductAmount = productAmount;
        AiCouponVO next = coupons.stream()
                .filter(c -> nvl(c.getMinAmount()).compareTo(finalProductAmount) > 0)
                .min(Comparator.comparing(c -> nvl(c.getMinAmount()).subtract(finalProductAmount)))
                .orElse(null);

        if (next != null) {
            suggestions.add("离优惠券「" + next.getTitle() + "」还差￥"
                    + nvl(next.getMinAmount()).subtract(finalProductAmount).setScale(2, RoundingMode.HALF_UP)
                    + "，凑单后可减￥" + nvl(next.getAmount()) + "。");
        }

        if (best != null) {
            suggestions.add("当前最优优惠券是「" + best.getTitle() + "」，预计优惠￥" + bestDiscount + "。");
        } else if (!coupons.isEmpty()) {
            suggestions.add("当前购物车金额暂未满足已领取优惠券门槛。");
        } else {
            suggestions.add("暂无可用已领取优惠券，可到优惠券中心先领券。");
        }

        plan.setSuggestions(suggestions);
        return plan;
    }

    private List<AiProductVO> resolveCartProducts(List<AiCartItemForm> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, Integer> quantityMap = new LinkedHashMap<>();
        for (AiCartItemForm item : items) {
            if (item == null || item.getProductId() == null) {
                continue;
            }
            int quantity = item.getQuantity() == null || item.getQuantity() < 1 ? 1 : item.getQuantity();
            quantityMap.put(item.getProductId(), quantityMap.getOrDefault(item.getProductId(), 0) + quantity);
        }

        if (quantityMap.isEmpty()) {
            return new ArrayList<>();
        }

        List<AiProductVO> products = safeList(aiAgentMapper.selectProductsByIds(new ArrayList<>(quantityMap.keySet())));
        for (AiProductVO product : products) {
            product.setQuantity(quantityMap.getOrDefault(product.getProductId(), 1));
        }

        return products;
    }

    private AiOrderVO resolveOrder(Integer userId, Integer orderId) {
        if (orderId != null) {
            return aiAgentMapper.selectOrderById(userId, orderId);
        }
        return aiAgentMapper.selectLatestActiveOrder(userId);
    }

    private AiOrderVO attachOrderDetails(AiOrderVO order) {
        if (order == null) {
            return null;
        }

        order.setItems(safeList(aiAgentMapper.selectOrderItems(order.getOrderId())));

        int minutes = predictMinutes(order);
        order.setPredictedMinutes(minutes);

        if (order.getEstimatedArrivalTime() != null) {
            order.setEtaText("系统预计送达：" + new SimpleDateFormat("HH:mm").format(order.getEstimatedArrivalTime()));
        } else if (minutes > 0) {
            order.setEtaText("AI 预测约 " + minutes + " 分钟送达");
        } else {
            order.setEtaText("订单尚未进入配送阶段，暂无法准确预测");
        }

        return order;
    }

    private int predictMinutes(AiOrderVO order) {
        if (order == null) {
            return 0;
        }

        if (order.getEstimatedArrivalTime() != null) {
            long minutes = Duration.between(Instant.now(), order.getEstimatedArrivalTime().toInstant()).toMinutes();
            return Math.max(0, (int) minutes);
        }

        Integer status = order.getStatus();
        if (status == null) {
            return 0;
        }

        if (status == 3) return 20;
        if (status == 2) return 30;
        if (status == 1) return 40;
        if (status == 0) return 45;

        return 0;
    }

    private String localReply(String message,
                              List<AiProductVO> products,
                              AiCouponPlanVO plan,
                              AiOrderVO order,
                              List<AiRiderVO> riders) {
        String lower = message == null ? "" : message.toLowerCase();

        if (containsAny(lower, "骑手", "召唤", "多久", "到达", "订单")) {
            if (order == null) {
                return "当前没有可跟踪的进行中订单。你可以先下单，订单进入配送流程后我会展示骑手和预计送达时间。";
            }

            String rider = order.getRiderName() == null
                    ? "暂未分配骑手"
                    : order.getRiderName() + "（" + nullToDash(order.getRiderPhone()) + "）";

            return "当前订单「" + order.getOrderNo() + "」状态为「" + order.getStatusText()
                    + "」。骑手：" + rider + "。" + order.getEtaText();
        }

        if (containsAny(lower, "优惠", "便宜", "最优", "省钱")) {
            return buildCouponText(plan);
        }

        return buildRecommendText(products);
    }

    private String buildRecommendText(List<AiProductVO> products) {
        if (products == null || products.isEmpty()) {
            return "当前没有可推荐的菜品。";
        }

        String names = products.stream()
                .limit(3)
                .map(p -> p.getName() + "￥" + nvl(p.getPrice()))
                .collect(Collectors.joining("、"));

        return "根据你的历史点单和当前热卖，我建议先看：" + names
                + "。你可以继续告诉我预算、人数、想吃辣还是清淡，我会帮你调整方案。";
    }

    private String buildCouponText(AiCouponPlanVO plan) {
        if (plan == null) {
            return "当前暂时无法计算优惠方案。";
        }

        return "当前购物车商品金额￥" + plan.getProductAmount()
                + "，配送费￥" + plan.getDeliveryFee()
                + "，预计优惠￥" + plan.getCouponDiscount()
                + "，预计支付￥" + plan.getPayAmount()
                + "。" + String.join(" ", safeList(plan.getSuggestions()));
    }

    private String readableItemNames(List<Map<String, Object>> items, List<AiProductVO> products) {
        if (items == null || items.isEmpty()) {
            return "暂无商品";
        }

        Map<Integer, AiProductVO> productMap = new HashMap<>();
        for (AiProductVO product : safeList(products)) {
            productMap.put(product.getProductId(), product);
        }

        List<String> names = new ArrayList<>();
        for (Map<String, Object> item : items) {
            Integer productId = null;
            try {
                productId = Integer.valueOf(String.valueOf(item.get("productId")));
            } catch (Exception ignored) {
            }

            int quantity = 1;
            try {
                quantity = Integer.parseInt(String.valueOf(item.getOrDefault("quantity", 1)));
            } catch (Exception ignored) {
            }

            AiProductVO product = productMap.get(productId);
            names.add((product == null ? "商品ID " + productId : product.getName()) + " × " + quantity);
        }

        return String.join("，", names);
    }

    private AiOrderVO convertUserOrderToAiOrder(UserOrderVO source) {
        if (source == null) {
            return null;
        }

        AiOrderVO target = new AiOrderVO();
        target.setOrderId(source.getOrderId());
        target.setOrderNo(source.getOrderNo());
        target.setUserId(source.getUserId());
        target.setMerchantId(source.getMerchantId());
        target.setMerchantName(source.getMerchantName());
        target.setRiderId(source.getRiderId());
        target.setRiderName(source.getRiderName());
        target.setRiderPhone(source.getRiderPhone());
        target.setTotalPrice(source.getTotalPrice());
        target.setProductAmount(source.getProductAmount());
        target.setDeliveryFee(source.getDeliveryFee());
        target.setDiscountAmount(source.getDiscountAmount());
        target.setActualAmount(source.getActualAmount());
        target.setStatus(source.getStatus());
        target.setStatusText(orderStatusText(source.getStatus()));
        target.setOrderTime(source.getOrderTime());
        target.setEstimatedArrivalTime(source.getEstimatedArrivalTime());
        target.setFinishTime(source.getFinishTime());
        target.setAddress(source.getAddress());
        target.setRequiredRiderLevel(source.getRequiredRiderLevel());
        target.setRequiredRiderTitle(source.getRequiredRiderTitle());
        return attachOrderDetails(target);
    }

    private List<AiProductVO> mergeProducts(List<AiProductVO> first, List<AiProductVO> second, int limit) {
        Map<Integer, AiProductVO> map = new LinkedHashMap<>();

        for (AiProductVO p : safeList(first)) {
            if (p.getProductId() != null) {
                map.put(p.getProductId(), p);
            }
        }

        for (AiProductVO p : safeList(second)) {
            if (p.getProductId() != null) {
                map.putIfAbsent(p.getProductId(), p);
            }
        }

        return map.values().stream().limit(limit).collect(Collectors.toList());
    }
    private String orderStatusText(Integer status) {
        if (status == null) {
            return "未知状态";
        }

        switch (status) {
            case -1:
                return "已取消";
            case 0:
                return "待商家接单";
            case 1:
                return "商家已接单";
            case 2:
                return "已出餐，待骑手接单";
            case 3:
                return "骑手配送中";
            case 4:
                return "已完成";
            default:
                return "未知状态";
        }
    }

    private String extractKeyword(String message) {
        if (blank(message)) {
            return null;
        }

        String m = message.replaceAll("[，。！？!?；;,.]", " ").trim();
        String[] useless = {"推荐", "想吃", "今天", "点什么", "给我", "便宜", "优惠", "骑手", "多久", "到达", "外卖", "帮我", "下单"};

        for (String u : useless) {
            m = m.replace(u, " ");
        }

        m = m.trim();
        return m.length() >= 2 && m.length() <= 20 ? m : null;
    }

    private boolean containsAny(String text, String... words) {
        if (text == null) {
            return false;
        }

        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }

        return false;
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal max(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String nullToDash(Object value) {
        return value == null || String.valueOf(value).trim().isEmpty() ? "暂无" : String.valueOf(value);
    }
}