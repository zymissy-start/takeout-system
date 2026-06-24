package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.RoleRegisterForm;
import com.example.takeoutsystem.entity.SysUser;

/**
 * 商家 / 骑手注册业务接口。
 */
public interface RoleRegisterService {

    SysUser registerMerchant(RoleRegisterForm form);

    SysUser registerRider(RoleRegisterForm form);
}