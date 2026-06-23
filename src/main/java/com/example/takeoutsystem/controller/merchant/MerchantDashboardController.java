package com.example.takeoutsystem.controller.merchant;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.MerchantDashboardStatistics;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.MerchantDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant/dashboard")
public class MerchantDashboardController {

    private final MerchantDashboardService merchantDashboardService;

    public MerchantDashboardController(MerchantDashboardService merchantDashboardService) {
        this.merchantDashboardService = merchantDashboardService;
    }

    @GetMapping("/statistics")
    public Result<MerchantDashboardStatistics> statistics(HttpSession session) {
        SysUser merchant = (SysUser) session.getAttribute("loginMerchant");

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        MerchantDashboardStatistics statistics =
                merchantDashboardService.getStatistics(merchant.getUserId());

        return Result.success("获取商家首页统计成功", statistics);
    }
}