package com.example.takeoutsystem.controller.merchant;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.MerchantPrintOrderVO;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.MerchantOrderPrintService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

/**
 * 商家订单打印 Controller。
 */
@RestController
@RequestMapping("/merchant/orders")
public class MerchantOrderPrintController {

    private final MerchantOrderPrintService merchantOrderPrintService;

    public MerchantOrderPrintController(MerchantOrderPrintService merchantOrderPrintService) {
        this.merchantOrderPrintService = merchantOrderPrintService;
    }

    @GetMapping("/print-data")
    public Result<MerchantPrintOrderVO> printData(Integer orderId, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        if (orderId == null) {
            return Result.fail("订单ID不能为空");
        }

        MerchantPrintOrderVO order = merchantOrderPrintService.getPrintOrder(merchant.getUserId(), orderId);

        if (order == null) {
            return Result.fail("订单不存在或不属于当前商家");
        }

        return Result.success("获取打印数据成功", order);
    }

    private SysUser getLoginMerchant(HttpSession session) {
        Object value = session.getAttribute("loginMerchant");

        if (value instanceof SysUser) {
            return (SysUser) value;
        }

        return null;
    }
}