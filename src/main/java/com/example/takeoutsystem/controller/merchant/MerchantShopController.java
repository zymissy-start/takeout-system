package com.example.takeoutsystem.controller.merchant;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.MerchantShopForm;
import com.example.takeoutsystem.entity.MerchantShopInfo;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.MerchantShopService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

/**
 * 商家店铺信息 Controller。
 */
@RestController
@RequestMapping("/merchant/shop")
public class MerchantShopController {

    private final MerchantShopService merchantShopService;

    public MerchantShopController(MerchantShopService merchantShopService) {
        this.merchantShopService = merchantShopService;
    }

    @GetMapping("/current")
    public Result<MerchantShopInfo> current(HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        MerchantShopInfo shopInfo = merchantShopService.getShopInfo(merchant.getUserId());

        if (shopInfo == null) {
            return Result.fail("店铺信息不存在");
        }

        return Result.success("获取店铺信息成功", shopInfo);
    }

    @PostMapping("/update")
    public Result<MerchantShopInfo> update(MerchantShopForm form, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        MerchantShopInfo shopInfo = merchantShopService.updateShopInfo(merchant.getUserId(), form);

        if (shopInfo == null) {
            return Result.fail("保存失败，请检查店铺名称、手机号或金额信息");
        }

        merchant.setRealName(shopInfo.getStoreName());
        merchant.setPhone(shopInfo.getContactPhone());
        session.setAttribute("loginMerchant", merchant);

        return Result.success("店铺信息保存成功", shopInfo);
    }

    private SysUser getLoginMerchant(HttpSession session) {
        Object value = session.getAttribute("loginMerchant");

        if (value instanceof SysUser) {
            return (SysUser) value;
        }

        return null;
    }
}