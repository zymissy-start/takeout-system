package com.example.takeoutsystem.controller.rider;

import com.example.takeoutsystem.entity.RiderNavigationVO;
import com.example.takeoutsystem.service.RiderNavigationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rider/navigation")
public class RiderNavigationController {

    private final RiderNavigationService riderNavigationService;

    public RiderNavigationController(RiderNavigationService riderNavigationService) {
        this.riderNavigationService = riderNavigationService;
    }

    @GetMapping("/active")
    public Map<String, Object> active(@RequestParam(required = false) Integer orderId,
                                      @RequestParam(required = false) Integer riderId) {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            if (orderId == null && riderId == null) {
                throw new IllegalArgumentException("请传入 orderId 或 riderId");
            }

            RiderNavigationVO data = riderNavigationService.getNavigationOrder(orderId, riderId);

            result.put("code", 200);
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", data);
            return result;
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", e.getMessage());
            result.put("data", null);
            return result;
        }
    }
}