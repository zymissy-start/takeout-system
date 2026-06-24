package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.service.UserProductService;
import org.springframework.web.bind.annotation.GetMapping;
<<<<<<< HEAD
=======
import org.springframework.web.bind.annotation.PathVariable;
>>>>>>> origin/feature-user-rider-merchant
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserProductController {
    private final UserProductService userProductService;

    public UserProductController(UserProductService userProductService) {
        this.userProductService = userProductService;
    }

    @GetMapping("/api/categories")
    public UserApiResult<?> categories() {
        return UserApiResult.success(userProductService.listCategories());
    }

<<<<<<< HEAD
    @GetMapping("/api/user/products")
    public UserApiResult<?> products(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer categoryId,
                                     @RequestParam(required = false, defaultValue = "recommend") String sort,
                                     @RequestParam(required = false, defaultValue = "1") Integer page,
                                     @RequestParam(required = false, defaultValue = "12") Integer size) {
        return UserApiResult.success(userProductService.pageProducts(keyword, categoryId, sort, page, size));
=======
    @GetMapping("/api/user/merchants")
    public UserApiResult<?> merchants(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer categoryId,
                                      @RequestParam(required = false, defaultValue = "recommend") String sort,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "12") Integer size) {
        return UserApiResult.success(userProductService.pageMerchants(keyword, categoryId, sort, page, size));
    }

    @GetMapping("/api/user/products")
    public UserApiResult<?> products(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer categoryId,
                                     @RequestParam(required = false) Integer merchantId,
                                     @RequestParam(required = false, defaultValue = "recommend") String sort,
                                     @RequestParam(required = false, defaultValue = "1") Integer page,
                                     @RequestParam(required = false, defaultValue = "12") Integer size) {
        return UserApiResult.success(userProductService.pageProducts(keyword, categoryId, merchantId, sort, page, size));
    }

    @GetMapping("/api/user/merchants/{merchantId}/products")
    public UserApiResult<?> merchantProducts(@PathVariable Integer merchantId,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Integer categoryId,
                                             @RequestParam(required = false, defaultValue = "recommend") String sort,
                                             @RequestParam(required = false, defaultValue = "1") Integer page,
                                             @RequestParam(required = false, defaultValue = "12") Integer size) {
        return UserApiResult.success(userProductService.pageProducts(keyword, categoryId, merchantId, sort, page, size));
>>>>>>> origin/feature-user-rider-merchant
    }
}
