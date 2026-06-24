package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.common.UserContext;
import com.example.takeoutsystem.entity.UserCommentForm;
import com.example.takeoutsystem.entity.UserOrderCreateForm;
import com.example.takeoutsystem.service.UserOrderService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/user/orders")
public class UserOrderController {
    private final UserOrderService userOrderService;

    public UserOrderController(UserOrderService userOrderService) {
        this.userOrderService = userOrderService;
    }

    @PostMapping
    public UserApiResult<?> create(HttpServletRequest request, @RequestBody UserOrderCreateForm form) {
        return UserApiResult.success("下单成功", userOrderService.createOrder(UserContext.getCurrentUserId(request), form));
    }

    @GetMapping
    public UserApiResult<?> list(HttpServletRequest request, @RequestParam(required = false) String status) {
        return UserApiResult.success(userOrderService.listOrders(UserContext.getCurrentUserId(request), status));
    }

    @GetMapping("/{orderId}")
    public UserApiResult<?> detail(HttpServletRequest request, @PathVariable Integer orderId) {
        return UserApiResult.success(userOrderService.getDetail(UserContext.getCurrentUserId(request), orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public UserApiResult<?> cancel(HttpServletRequest request,
                                   @PathVariable Integer orderId,
                                   @RequestBody(required = false) Map<String, String> body) {
        String reason = body == null ? "用户主动取消" : body.getOrDefault("reason", "用户主动取消");
        userOrderService.cancel(UserContext.getCurrentUserId(request), orderId, reason);
        return UserApiResult.success("订单已取消", null);
    }

    @PutMapping("/{orderId}/urge")
    public UserApiResult<?> urge(HttpServletRequest request, @PathVariable Integer orderId) {
        return UserApiResult.success(userOrderService.urge(UserContext.getCurrentUserId(request), orderId));
    }

    @PostMapping("/{orderId}/tip")
    public UserApiResult<?> tip(HttpServletRequest request,
                                @PathVariable Integer orderId,
                                @RequestBody(required = false) Map<String, Object> body) {
        Object value = body == null ? null : body.get("tipAmount");
        if (value == null && body != null) {
            value = body.get("amount");
        }

        BigDecimal tipAmount = null;
        if (value != null) {
            try {
                tipAmount = new BigDecimal(String.valueOf(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("打赏金额格式不正确");
            }
        }

        return UserApiResult.success(userOrderService.tip(UserContext.getCurrentUserId(request), orderId, tipAmount));
    }

    @PostMapping("/{orderId}/comments")
    public UserApiResult<?> comment(HttpServletRequest request, @PathVariable Integer orderId, @RequestBody UserCommentForm form) {
        userOrderService.comment(UserContext.getCurrentUserId(request), orderId, form);
        return UserApiResult.success("评价成功", null);
    }
}
