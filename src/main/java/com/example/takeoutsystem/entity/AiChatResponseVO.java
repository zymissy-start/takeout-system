package com.example.takeoutsystem.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class AiChatResponseVO {
    private String reply;
    private Boolean deepSeekEnabled;
    private Boolean fallback;

    /**
     * AI 判断出的动作：
     * chat / recommend_food / prepare_order / create_order /
     * contact_merchant / contact_rider / track_order / coupon_plan
     */
    private String action;

    /**
     * 是否需要用户确认。
     * 下单前必须二次确认。
     */
    private Boolean needConfirm;

    /**
     * 动作参数。
     * 例如待下单商品、地址、优惠券、备注。
     */
    private Map<String, Object> actionPayload;

    private List<AiProductVO> recommendations = new ArrayList<>();
    private List<AiRiderVO> riders = new ArrayList<>();
    private AiOrderVO activeOrder;
    private AiCouponPlanVO couponPlan;

    /**
     * 真实创建成功后的订单对象。
     */
    private Object createdOrder;
}