package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.AdminContext;
import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.entity.UserStatusForm;
import com.example.takeoutsystem.mapper.SysUserMapper;
import com.example.takeoutsystem.service.AdminManageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/manage")
public class AdminManageController {

    private final AdminManageService adminManageService;
    private final SysUserMapper sysUserMapper;

    public AdminManageController(AdminManageService adminManageService,
                                 SysUserMapper sysUserMapper) {
        this.adminManageService = adminManageService;
        this.sysUserMapper = sysUserMapper;
    }

    @GetMapping("/users")
    public UserApiResult listUsers(HttpServletRequest request,
                                   @RequestParam("role") Integer roleType,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        requireAdmin(request);
        if (roleType == null || roleType < 1 || roleType > 3) {
            return UserApiResult.error("角色类型不合法");
        }
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 20;
        return UserApiResult.success(adminManageService.listUsers(roleType, keyword, page, size));
    }

    @GetMapping("/users/{userId}")
    public UserApiResult getUserDetail(HttpServletRequest request,
                                       @PathVariable Integer userId) {
        requireAdmin(request);
        if (userId == null || userId <= 0) {
            return UserApiResult.error("用户ID不合法");
        }
        return UserApiResult.success(adminManageService.getUserDetail(userId));
    }

    @PutMapping("/users/{userId}/status")
    public UserApiResult updateUserStatus(HttpServletRequest request,
                                          @PathVariable Integer userId,
                                          @RequestBody UserStatusForm form) {
        requireAdmin(request);
        if (userId == null || userId <= 0) {
            return UserApiResult.error("用户ID不合法");
        }
        if (form == null || form.getStatus() == null || (form.getStatus() != 0 && form.getStatus() != 1)) {
            return UserApiResult.error("状态值不合法（0=禁用, 1=正常）");
        }
        adminManageService.updateUserStatus(userId, form.getStatus());
        return UserApiResult.success(form.getStatus() == 1 ? "账号已启用" : "账号已禁用", null);
    }

    private Integer requireAdmin(HttpServletRequest request) {
        Integer adminId = AdminContext.getCurrentAdminId(request);
        SysUser user = sysUserMapper.selectById(adminId);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 4
                || user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("无管理员权限");
        }
        return adminId;
    }
}
