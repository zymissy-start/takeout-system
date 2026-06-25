package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.common.UserContext;
import com.example.takeoutsystem.service.UserFavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserFavoriteController {
    private final UserFavoriteService userFavoriteService;

    public UserFavoriteController(UserFavoriteService userFavoriteService) {
        this.userFavoriteService = userFavoriteService;
    }

    @GetMapping("/api/user/favorites")
    public UserApiResult<?> favorites(HttpServletRequest request,
                                      @RequestParam(required = false, defaultValue = "merchant") String type,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "20") Integer size,
                                      @RequestParam(required = false, defaultValue = "false") Boolean idsOnly) {
        Integer userId = UserContext.getCurrentUserId(request);

        if (!"merchant".equalsIgnoreCase(type) && !"merchants".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("当前收藏功能仅支持商家收藏");
        }

        if (Boolean.TRUE.equals(idsOnly)) {
            return UserApiResult.success(userFavoriteService.listFavoriteMerchantIds(userId));
        }

        return UserApiResult.success(userFavoriteService.listMerchantFavorites(userId, page, size));
    }

    @PostMapping("/api/user/favorites/merchants/{merchantId}")
    public UserApiResult<?> addMerchant(HttpServletRequest request, @PathVariable Integer merchantId) {
        return UserApiResult.success(
                userFavoriteService.addMerchantFavorite(UserContext.getCurrentUserId(request), merchantId)
        );
    }

    @DeleteMapping("/api/user/favorites/merchants/{merchantId}")
    public UserApiResult<?> removeMerchant(HttpServletRequest request, @PathVariable Integer merchantId) {
        return UserApiResult.success(
                userFavoriteService.removeMerchantFavorite(UserContext.getCurrentUserId(request), merchantId)
        );
    }

    @PostMapping("/api/user/favorites/merchants/{merchantId}/toggle")
    public UserApiResult<?> toggleMerchant(HttpServletRequest request, @PathVariable Integer merchantId) {
        return UserApiResult.success(
                userFavoriteService.toggleMerchantFavorite(UserContext.getCurrentUserId(request), merchantId)
        );
    }
}