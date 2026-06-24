package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.common.UserContext;
import com.example.takeoutsystem.service.UserProfileService;
<<<<<<< HEAD
=======
import com.example.takeoutsystem.entity.UserLevelVO;

import java.util.List;
import java.util.Map;
>>>>>>> origin/feature-user-rider-merchant
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
<<<<<<< HEAD
=======


    @GetMapping("/api/user/level/details")
    public UserApiResult<?> levelDetails(HttpServletRequest request) {
        UserLevelVO current = userProfileService.getLevel(UserContext.getCurrentUserId(request));
        return UserApiResult.success(Map.of(
                "current", current,
                "orderLevelRules", List.of(
                        Map.of(
                                "level", 0,
                                "title", "普通用户",
                                "range", "0-9 单有效点餐",
                                "matchedRider", "普通骑手",
                                "privileges", List.of("基础配送服务", "可正常催单", "普通骑手可接此类订单")
                        ),
                        Map.of(
                                "level", 1,
                                "title", "优先用户",
                                "range", "10-14 单有效点餐",
                                "matchedRider", "闪电侠骑手及以上",
                                "privileges", List.of("订单进入高等级骑手可接池", "闪电侠骑手及以上可优先处理", "催单提醒在商家/骑手端高亮显示")
                        ),
                        Map.of(
                                "level", 2,
                                "title", "尊享用户",
                                "range", "15 单及以上有效点餐",
                                "matchedRider", "单王配送骑手",
                                "privileges", List.of("订单优先匹配单王配送骑手", "骑手端按高等级订单优先排序", "催单提醒保持最高醒目提示")
                        )
                ),
                "riderLevelRules", List.of(
                        Map.of("level", 0, "title", "普通骑手", "condition", "完成配送少于 10 单", "canTake", "普通用户订单"),
                        Map.of("level", 1, "title", "闪电侠骑手", "condition", "完成配送 10-14 单", "canTake", "普通用户订单、优先用户订单"),
                        Map.of("level", 2, "title", "单王配送骑手", "condition", "完成配送 15 单及以上", "canTake", "全部订单，含尊享用户订单")
                ),
                "note", "用户点餐数达到 10 单成为优先用户，达到 15 单成为尊享用户；闪电侠、单王配送是骑手等级，不是用户等级。"
        ));
    }
>>>>>>> origin/feature-user-rider-merchant
}
