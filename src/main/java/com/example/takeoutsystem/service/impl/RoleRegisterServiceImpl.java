package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.RoleRegisterForm;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.RoleRegisterMapper;
import com.example.takeoutsystem.service.RoleRegisterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家 / 骑手注册业务实现。
 */
@Service
public class RoleRegisterServiceImpl implements RoleRegisterService {

    private static final int ROLE_MERCHANT = 2;
    private static final int ROLE_RIDER = 3;

    private final RoleRegisterMapper roleRegisterMapper;

    public RoleRegisterServiceImpl(RoleRegisterMapper roleRegisterMapper) {
        this.roleRegisterMapper = roleRegisterMapper;
    }

    @Override
    @Transactional
    public SysUser registerMerchant(RoleRegisterForm form) {
        String checkMessage = checkBaseForm(form);

        if (checkMessage != null) {
            return null;
        }

        String shopName = trim(form.getShopName());
        String shopAddress = trim(form.getShopAddress());
        String shopNotice = trim(form.getShopNotice());
        String businessHours = trim(form.getBusinessHours());
        String deliveryDescription = trim(form.getDeliveryDescription());

        if (shopName == null || shopName.isEmpty()) {
            return null;
        }

        if (shopAddress == null || shopAddress.isEmpty()) {
            return null;
        }

        if (roleRegisterMapper.countByUsername(trim(form.getUsername())) > 0) {
            return null;
        }

        SysUser merchant = buildUser(form, ROLE_MERCHANT);
        merchant.setRealName(shopName);

        roleRegisterMapper.insertSysUser(merchant);

        roleRegisterMapper.insertMerchantInfo(
                merchant.getUserId(),
                shopName,
                trim(form.getPhone()),
                shopAddress,
                shopNotice == null || shopNotice.isEmpty() ? "欢迎光临本店" : shopNotice,
                businessHours == null || businessHours.isEmpty() ? "09:00-22:00" : businessHours,
                deliveryDescription == null || deliveryDescription.isEmpty() ? "商家接单后会尽快出餐" : deliveryDescription
        );

        merchant.setPassword(null);
        return merchant;
    }

    @Override
    @Transactional
    public SysUser registerRider(RoleRegisterForm form) {
        String checkMessage = checkBaseForm(form);

        if (checkMessage != null) {
            return null;
        }

        if (roleRegisterMapper.countByUsername(trim(form.getUsername())) > 0) {
            return null;
        }

        SysUser rider = buildUser(form, ROLE_RIDER);
        roleRegisterMapper.insertSysUser(rider);

        rider.setPassword(null);
        return rider;
    }

    private SysUser buildUser(RoleRegisterForm form, int roleType) {
        SysUser user = new SysUser();

        user.setUsername(trim(form.getUsername()));
        user.setPassword(trim(form.getPassword()));
        user.setRealName(trim(form.getRealName()));
        user.setPhone(trim(form.getPhone()));
        user.setRoleType(roleType);
        user.setCreditScore(0);
        user.setStatus(1);

        return user;
    }

    private String checkBaseForm(RoleRegisterForm form) {
        if (form == null) {
            return "注册信息不能为空";
        }

        String username = trim(form.getUsername());
        String password = trim(form.getPassword());
        String confirmPassword = trim(form.getConfirmPassword());
        String realName = trim(form.getRealName());
        String phone = trim(form.getPhone());

        if (username == null || !username.matches("^[A-Za-z0-9_]{3,20}$")) {
            return "账号只能包含字母、数字、下划线，长度为3到20位";
        }

        if (password == null || password.length() < 6 || password.length() > 20) {
            return "密码长度必须为6到20位";
        }

        if (!password.equals(confirmPassword)) {
            return "两次输入的密码不一致";
        }

        if (realName == null || realName.isEmpty()) {
            return "姓名不能为空";
        }

        if (phone == null || !phone.matches("^1\\d{10}$")) {
            return "手机号格式不正确";
        }

        return null;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}