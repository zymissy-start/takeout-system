package com.example.takeoutsystem.controller.merchant;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.MerchantOrderDetailVO;
import com.example.takeoutsystem.entity.MerchantOrderVO;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.MerchantOrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant")
public class MerchantOrderController {

    private final MerchantOrderService merchantOrderService;

    public MerchantOrderController(MerchantOrderService merchantOrderService) {
        this.merchantOrderService = merchantOrderService;
    }

    @GetMapping("/orders")
    public Result<List<MerchantOrderVO>> listRecentOrders(Integer size, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        List<MerchantOrderVO> orders =
                merchantOrderService.listRecentOrders(merchant.getUserId(), size);

        return Result.success("获取商家订单成功", orders);
    }

    @GetMapping("/orders/list")
    public Result<List<MerchantOrderVO>> listOrders(Integer status, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        List<MerchantOrderVO> orders =
                merchantOrderService.listOrders(merchant.getUserId(), status);

        return Result.success("获取订单列表成功", orders);
    }

    @GetMapping("/orders/detail")
    public Result<MerchantOrderDetailVO> detail(Integer orderId, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        MerchantOrderDetailVO detail =
                merchantOrderService.getOrderDetail(merchant.getUserId(), orderId);

        if (detail == null) {
            return Result.fail("订单不存在或不属于当前商家");
        }

        return Result.success("获取订单详情成功", detail);
    }

    @PostMapping("/order/accept")
    public Result<Void> acceptOrder(Integer orderId, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        boolean success = merchantOrderService.acceptOrder(merchant.getUserId(), orderId);

        if (!success) {
            return Result.fail("接单失败，订单状态可能已变化");
        }

        return Result.success("接单成功");
    }

    @PostMapping("/order/finish-cooking")
    public Result<Void> finishCooking(Integer orderId, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        boolean success = merchantOrderService.finishCooking(merchant.getUserId(), orderId);

        if (!success) {
            return Result.fail("出餐失败，请确认订单是否已接单");
        }

        return Result.success("出餐完成");
    }

    @PostMapping("/order/call-rider")
    public Result<Void> callRider(Integer orderId, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        boolean success = merchantOrderService.callRider(merchant.getUserId(), orderId);

        if (!success) {
            return Result.fail("召唤骑手失败，请确认订单是否已出餐");
        }

        return Result.success("召唤骑手成功");
    }

    private SysUser getLoginMerchant(HttpSession session) {
        return (SysUser) session.getAttribute("loginMerchant");
    }
}