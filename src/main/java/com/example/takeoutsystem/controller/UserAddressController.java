package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.common.UserContext;
import com.example.takeoutsystem.entity.UserAddressForm;
import com.example.takeoutsystem.service.UserAddressService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user/addresses")
public class UserAddressController {
    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @GetMapping
    public UserApiResult<?> list(HttpServletRequest request) {
        return UserApiResult.success(userAddressService.list(UserContext.getCurrentUserId(request)));
    }

    @PostMapping
    public UserApiResult<?> create(HttpServletRequest request, @RequestBody UserAddressForm form) {
        return UserApiResult.success("地址保存成功", userAddressService.create(UserContext.getCurrentUserId(request), form));
    }

    @PutMapping("/{addressId}")
    public UserApiResult<?> update(HttpServletRequest request, @PathVariable Integer addressId, @RequestBody UserAddressForm form) {
        return UserApiResult.success("地址更新成功", userAddressService.update(UserContext.getCurrentUserId(request), addressId, form));
    }

    @DeleteMapping("/{addressId}")
    public UserApiResult<?> delete(HttpServletRequest request, @PathVariable Integer addressId) {
        userAddressService.delete(UserContext.getCurrentUserId(request), addressId);
        return UserApiResult.success("删除成功", null);
    }

    @PutMapping("/{addressId}/default")
    public UserApiResult<?> setDefault(HttpServletRequest request, @PathVariable Integer addressId) {
        userAddressService.setDefault(UserContext.getCurrentUserId(request), addressId);
        return UserApiResult.success("已设为默认地址", null);
    }
}
