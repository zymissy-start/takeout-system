package com.example.takeoutsystem.controller.merchant;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.MerchantLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/merchant", produces = "application/json;charset=UTF-8")
public class MerchantLoginController {

    private final MerchantLoginService merchantLoginService;

    public MerchantLoginController(MerchantLoginService merchantLoginService) {
        this.merchantLoginService = merchantLoginService;
    }

    @PostMapping("/login")
    public Result<SysUser> login(String username, String password, HttpSession session) {
        SysUser merchant = merchantLoginService.login(username, password);

        if (merchant == null) {
            return Result.fail("商家账号或密码错误");
        }

        merchant.setPassword(null);
        session.setAttribute("loginMerchant", merchant);

        return Result.success("登录成功", merchant);
    }

    @GetMapping("/current")
    public Result<SysUser> current(HttpSession session) {
        SysUser merchant = (SysUser) session.getAttribute("loginMerchant");

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        return Result.success("获取当前商家成功", merchant);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.removeAttribute("loginMerchant");
        return Result.success("退出成功");
    }
}