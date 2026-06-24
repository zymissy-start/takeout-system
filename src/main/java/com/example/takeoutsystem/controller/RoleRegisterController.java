package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.RoleRegisterForm;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.RoleRegisterService;
import org.springframework.web.bind.annotation.*;

/**
 * 商家 / 骑手注册 Controller。
 */
@RestController
public class RoleRegisterController {

    private final RoleRegisterService roleRegisterService;

    public RoleRegisterController(RoleRegisterService roleRegisterService) {
        this.roleRegisterService = roleRegisterService;
    }

    @PostMapping("/merchant/register")
    public Result<SysUser> registerMerchant(RoleRegisterForm form) {
        SysUser merchant = roleRegisterService.registerMerchant(form);

        if (merchant == null) {
            return Result.fail("商家注册失败，请检查账号是否重复、手机号格式、店铺名称或地址");
        }

        return Result.success("商家注册成功", merchant);
    }

    @PostMapping("/rider/register")
    public Result<SysUser> registerRider(RoleRegisterForm form) {
        SysUser rider = roleRegisterService.registerRider(form);

        if (rider == null) {
            return Result.fail("骑手注册失败，请检查账号是否重复、手机号格式或必填信息");
        }

        return Result.success("骑手注册成功", rider);
    }
}