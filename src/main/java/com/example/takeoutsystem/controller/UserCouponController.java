package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.common.UserContext;
import com.example.takeoutsystem.service.UserCouponService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserCouponController {
    private final UserCouponService userCouponService;

    public UserCouponController(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    @GetMapping("/api/user/coupons")
    public UserApiResult<?> couponCenter(HttpServletRequest request) {
        return UserApiResult.success(
                userCouponService.listCouponCenter(UserContext.getCurrentUserId(request))
        );
    }

    @GetMapping("/api/user/coupons/my")
    public UserApiResult<?> myCoupons(HttpServletRequest request,
                                      @RequestParam(required = false) Integer status) {
        return UserApiResult.success(
                userCouponService.listMyCoupons(UserContext.getCurrentUserId(request), status)
        );
    }

    @PostMapping("/api/user/coupons/{couponId}/receive")
    public UserApiResult<?> receive(HttpServletRequest request, @PathVariable Integer couponId) {
        return UserApiResult.success(
                userCouponService.receiveCoupon(UserContext.getCurrentUserId(request), couponId)
        );
    }
}