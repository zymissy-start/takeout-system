package com.example.takeoutsystem.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AiChatRequest {
    /**
     * 用户输入内容。
     */
    private String message;

    /**
     * 前端可主动指定意图，例如 create_order。
     */
    private String intent;

    /**
     * 指定订单 ID。用于查骑手、催单、联系商家或骑手。
     */
    private Integer orderId;

    /**
     * 下单地址 ID。
     */
    private Integer addressId;

    /**
     * 使用的用户优惠券 ID。
     */
    private Integer userCouponId;

    /**
     * 订单备注。
     */
    private String remark;

    /**
     * 是否确认下单。
     * AI 只能先生成下单方案，真正下单必须 confirmOrder=true。
     */
    private Boolean confirmOrder;

    /**
     * 预算。
     */
    private BigDecimal budget;

    /**
     * 购物车或 AI 推荐后待确认的商品。
     */
    private List<AiCartItemForm> items;
}