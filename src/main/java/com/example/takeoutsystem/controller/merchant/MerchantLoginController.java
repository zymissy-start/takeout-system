package com.example.takeoutsystem.controller.merchant;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.MerchantLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

/**
 * 商家登录控制器。
 *
 * 负责处理商家登录、获取当前登录商家、退出登录等请求。
 * Controller 只负责接收请求和返回结果，不直接编写 SQL，
 * 实际登录校验交给 Service 和 Mapper 完成。
 */

@RestController
@RequestMapping(value = "/merchant", produces = "application/json;charset=UTF-8")
public class MerchantLoginController {

    private final MerchantLoginService merchantLoginService;
    /**
     * 使用构造方法注入 Service，避免手动创建对象，
     * 由 Spring 容器统一管理依赖关系。
     */

    public MerchantLoginController(MerchantLoginService merchantLoginService) {
        this.merchantLoginService = merchantLoginService;
    }
    /**
     * 商家登录接口。
     *
     * 请求地址：POST /merchant/login
     * 请求参数：username、password
     *
     * 登录成功后，将商家对象保存到 Session 中，
     * 后续订单管理、菜品管理等接口都通过 Session 判断商家是否已登录。
     */

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

    /**
     * 获取当前登录商家信息。
     *
     * 前端 dashboard.html、foods.html、orders.html 页面加载时，
     * 都会调用该接口确认商家是否已经登录。
     */
    @GetMapping("/current")
    public Result<SysUser> current(HttpSession session) {
        SysUser merchant = (SysUser) session.getAttribute("loginMerchant");

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        return Result.success("获取当前商家成功", merchant);
    }

    /**
     * 商家退出登录接口。
     *
     * 通过移除 Session 中的 loginMerchant，清除登录状态。
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.removeAttribute("loginMerchant");
        return Result.success("退出成功");
    }
}