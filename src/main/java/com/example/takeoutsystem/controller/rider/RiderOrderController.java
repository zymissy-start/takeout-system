package com.example.takeoutsystem.controller.rider;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.RiderOrderDetailVO;
import com.example.takeoutsystem.entity.RiderOrderVO;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.RiderOrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 骑手订单控制器。
 * 包含可接订单、我的配送、订单详情、接单、完成配送。
 */
@RestController
@RequestMapping("/rider/orders")
public class RiderOrderController {

    private final RiderOrderService riderOrderService;

    public RiderOrderController(RiderOrderService riderOrderService) {
        this.riderOrderService = riderOrderService;
    }

    @GetMapping("/available")
    public Result<List<RiderOrderVO>> available(HttpSession session) {
        SysUser rider = getLoginRider(session);

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        return Result.success("获取可接订单成功", riderOrderService.listAvailableOrders(rider.getUserId()));
    }

    @GetMapping("/my")
    public Result<List<RiderOrderVO>> myOrders(Integer status, HttpSession session) {
        SysUser rider = getLoginRider(session);

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        List<RiderOrderVO> orders =
                riderOrderService.listMyOrders(rider.getUserId(), status);

        return Result.success("获取我的配送订单成功", orders);
    }

    @GetMapping("/detail")
    public Result<RiderOrderDetailVO> detail(Integer orderId, HttpSession session) {
        SysUser rider = getLoginRider(session);

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        RiderOrderDetailVO detail =
                riderOrderService.getOrderDetail(rider.getUserId(), orderId);

        if (detail == null) {
            return Result.fail("订单不存在或无权限查看");
        }

        return Result.success("获取订单详情成功", detail);
    }

    @PostMapping("/accept")
    public Result<Void> accept(Integer orderId, HttpSession session) {
        return doAccept(orderId, session);
    }

    /**
     * 兼容前端直接 POST /rider/orders/accept/{orderId} 的写法。
     * 原接口 /rider/orders/accept 仍然保留，参数为 orderId。
     */
    @PostMapping("/accept/{orderId}")
    public Result<Void> acceptByPath(@PathVariable Integer orderId, HttpSession session) {
        return doAccept(orderId, session);
    }

    private Result<Void> doAccept(Integer orderId, HttpSession session) {
        SysUser rider = getLoginRider(session);

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        boolean success = riderOrderService.acceptOrder(rider, orderId);

        if (!success) {
            return Result.fail("接单失败：订单可能已被接走、商家还未出餐，或当前骑手等级不足");
        }

        return Result.success("接单成功，已进入配送中");
    }
    @GetMapping("/wait-cooking")
    public Result<List<RiderOrderVO>> waitCooking(HttpSession session) {
        SysUser rider = getLoginRider(session);

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        return Result.success("获取待出餐订单成功", riderOrderService.listWaitCookingOrders());
    }

    @PostMapping("/urge-merchant")
    public Result<Void> urgeMerchant(Integer orderId, HttpSession session) {
        SysUser rider = getLoginRider(session);

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        boolean success = riderOrderService.urgeMerchant(rider.getUserId(), orderId);

        if (!success) {
            return Result.fail("催促失败，订单可能已经出餐");
        }

        return Result.success("已提醒商家尽快出餐");
    }

    @PostMapping("/tip")
    public Result<Void> addTip(Integer orderId, java.math.BigDecimal tipAmount, HttpSession session) {
        SysUser rider = getLoginRider(session);

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        boolean success = riderOrderService.addTip(rider.getUserId(), orderId, tipAmount);

        if (!success) {
            return Result.fail("打赏失败，请确认订单是否属于当前骑手");
        }

        return Result.success("用户打赏已到账");
    }

    @PostMapping("/finish")
    public Result<Void> finish(Integer orderId, HttpSession session) {
        return doFinish(orderId, session);
    }

    @PostMapping("/finish/{orderId}")
    public Result<Void> finishByPath(@PathVariable Integer orderId, HttpSession session) {
        return doFinish(orderId, session);
    }

    private Result<Void> doFinish(Integer orderId, HttpSession session) {
        SysUser rider = getLoginRider(session);

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        boolean success = riderOrderService.finishOrder(rider.getUserId(), orderId);

        if (!success) {
            return Result.fail("完成配送失败，请确认订单状态");
        }

        return Result.success("配送完成");
    }

    private SysUser getLoginRider(HttpSession session) {
        return (SysUser) session.getAttribute("loginRider");
    }
}