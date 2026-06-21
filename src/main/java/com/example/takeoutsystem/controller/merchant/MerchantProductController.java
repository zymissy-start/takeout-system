package com.example.takeoutsystem.controller.merchant;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.MerchantProductVO;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.MerchantProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant")
public class MerchantProductController {

    private final MerchantProductService merchantProductService;

    public MerchantProductController(MerchantProductService merchantProductService) {
        this.merchantProductService = merchantProductService;
    }

    @GetMapping("/foods")
    public Result<List<MerchantProductVO>> listFoods(Integer size, HttpSession session) {
        SysUser merchant = (SysUser) session.getAttribute("loginMerchant");

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        List<MerchantProductVO> products =
                merchantProductService.listMerchantProducts(merchant.getUserId(), size);

        return Result.success("获取商家商品成功", products);
    }
}