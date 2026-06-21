package com.example.takeoutsystem.controller.merchant;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.MerchantFoodForm;
import com.example.takeoutsystem.entity.MerchantFoodVO;
import com.example.takeoutsystem.entity.MerchantProductVO;
import com.example.takeoutsystem.entity.ProductCategory;
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
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        List<MerchantProductVO> products =
                merchantProductService.listMerchantProducts(merchant.getUserId(), size);

        return Result.success("获取商家商品成功", products);
    }

    @GetMapping("/foods/list")
    public Result<List<MerchantFoodVO>> listManageFoods(String keyword,
                                                        Integer categoryId,
                                                        Integer status,
                                                        HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        List<MerchantFoodVO> foods =
                merchantProductService.listFoods(merchant.getUserId(), keyword, categoryId, status);

        return Result.success("获取菜品列表成功", foods);
    }

    @GetMapping("/categories")
    public Result<List<ProductCategory>> listCategories(HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        List<ProductCategory> categories = merchantProductService.listCategories();

        return Result.success("获取分类成功", categories);
    }

    @PostMapping("/foods/add")
    public Result<Void> addFood(MerchantFoodForm form, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        boolean success = merchantProductService.addFood(merchant.getUserId(), form);

        if (!success) {
            return Result.fail("新增失败，请检查菜品名称、分类和价格");
        }

        return Result.success("新增菜品成功");
    }

    @PostMapping("/foods/update")
    public Result<Void> updateFood(MerchantFoodForm form, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        boolean success = merchantProductService.updateFood(merchant.getUserId(), form);

        if (!success) {
            return Result.fail("修改失败，请确认菜品是否属于当前商家");
        }

        return Result.success("修改菜品成功");
    }

    @PostMapping("/foods/status")
    public Result<Void> updateFoodStatus(Integer productId, Integer status, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);

        if (merchant == null) {
            return Result.fail("商家未登录");
        }

        boolean success =
                merchantProductService.updateFoodStatus(merchant.getUserId(), productId, status);

        if (!success) {
            return Result.fail("状态修改失败，请确认菜品是否属于当前商家");
        }

        if (status != null && status == 1) {
            return Result.success("上架成功");
        }

        return Result.success("下架成功");
    }

    private SysUser getLoginMerchant(HttpSession session) {
        return (SysUser) session.getAttribute("loginMerchant");
    }
}