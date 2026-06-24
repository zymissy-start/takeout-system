package com.example.takeoutsystem.controller.rider;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.RiderLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

/**
 * 骑手登录控制器。
 * 对外提供骑手登录、获取当前骑手、退出登录接口。
 */
@RestController
@RequestMapping("/rider")
public class RiderLoginController {

    private final RiderLoginService riderLoginService;

    public RiderLoginController(RiderLoginService riderLoginService) {
        this.riderLoginService = riderLoginService;
    }

    @PostMapping("/login")
    public Result<SysUser> login(String username, String password, HttpSession session) {
        SysUser rider = riderLoginService.login(username, password);

        if (rider == null) {
            return Result.fail("骑手账号或密码错误");
        }

        rider.setPassword(null);
        session.setAttribute("loginRider", rider);

        return Result.success("骑手登录成功", rider);
    }

    @GetMapping("/current")
    public Result<SysUser> current(HttpSession session) {
        SysUser rider = (SysUser) session.getAttribute("loginRider");

        if (rider == null) {
            return Result.fail("骑手未登录");
        }

        return Result.success("获取当前骑手成功", rider);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.removeAttribute("loginRider");
        return Result.success("退出成功");
    }
}