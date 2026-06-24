package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.common.UserContext;
import com.example.takeoutsystem.service.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/api/user/me")
    public UserApiResult<?> me(HttpServletRequest request) {
        return UserApiResult.success(userProfileService.getMe(UserContext.getCurrentUserId(request)));
    }

    @GetMapping("/api/user/stats")
    public UserApiResult<?> stats(HttpServletRequest request) {
        return UserApiResult.success(userProfileService.getStats(UserContext.getCurrentUserId(request)));
    }

    @GetMapping("/api/user/level")
    public UserApiResult<?> level(HttpServletRequest request) {
        return UserApiResult.success(userProfileService.getLevel(UserContext.getCurrentUserId(request)));
    }
}
