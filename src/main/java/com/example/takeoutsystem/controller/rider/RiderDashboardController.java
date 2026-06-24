package com.example.takeoutsystem.controller.rider;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.RiderDashboardStatistics;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.RiderDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

/**
 * 骑手工作台控制器。
 */
@RestController
@RequestMapping("/rider/dashboard")
public class RiderDashboardController {

    private final RiderDashboardService riderDashboardService;

    public RiderDashboardController(RiderDashboardService riderDashboardService) {
        this.riderDashboardService = riderDashboardService;
    }

    @GetMapping("/statistics")
    public Result<RiderDashboardStatistics> statistics(HttpSession session) {
        SysUser rider = (SysUser) session.getAttribute("loginRider");

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        RiderDashboardStatistics statistics =
                riderDashboardService.getStatistics(rider.getUserId());

        return Result.success("获取骑手工作台统计成功", statistics);
    }
}