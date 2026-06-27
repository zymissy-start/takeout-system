package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.AdminContext;
import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.entity.ContactCreateForm;
import com.example.takeoutsystem.entity.ContactReplyForm;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.SysUserMapper;
import com.example.takeoutsystem.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/contact")
public class AdminContactController {

    private final ContactService contactService;
    private final SysUserMapper sysUserMapper;

    public AdminContactController(ContactService contactService,
                                 SysUserMapper sysUserMapper) {
        this.contactService = contactService;
        this.sysUserMapper = sysUserMapper;
    }

    private static final int ADMIN_ROLE = 4;

    @GetMapping("/sessions")
    public UserApiResult listSessions(HttpServletRequest request,
                                      @RequestParam(required = false) Integer targetRole,
                                      @RequestParam(required = false) String keyword) {
        requireAdmin(request);
        return UserApiResult.success(contactService.listAdminSessions(targetRole, keyword));
    }

    @GetMapping("/sessions/{sessionId}")
    public UserApiResult getSessionDetail(HttpServletRequest request,
                                          @PathVariable Integer sessionId) {
        Integer adminId = requireAdmin(request);
        return UserApiResult.success(contactService.getSessionDetail(sessionId, adminId, ADMIN_ROLE));
    }

    @PostMapping("/sessions")
    public UserApiResult createSession(HttpServletRequest request,
                                       @RequestBody ContactCreateForm form) {
        Integer adminId = requireAdmin(request);
        if (form == null || form.getTargetId() == null || form.getTargetRole() == null) {
            return UserApiResult.error("缺少目标用户信息");
        }
        return UserApiResult.success("会话已创建", contactService.createSession(adminId, ADMIN_ROLE, form));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public UserApiResult sendMessage(HttpServletRequest request,
                                    @PathVariable Integer sessionId,
                                    @RequestBody ContactReplyForm form) {
        Integer adminId = requireAdmin(request);
        if (form == null || form.getContent() == null || form.getContent().trim().isEmpty()) {
            return UserApiResult.error("消息内容不能为空");
        }
        return UserApiResult.success("发送成功",
                contactService.sendMessage(sessionId, adminId, ADMIN_ROLE, form.getContent().trim()));
    }

    @PutMapping("/sessions/{sessionId}/close")
    public UserApiResult closeSession(HttpServletRequest request,
                                      @PathVariable Integer sessionId) {
        Integer adminId = requireAdmin(request);
        contactService.closeSession(sessionId, adminId, ADMIN_ROLE);
        return UserApiResult.success("会话已关闭", null);
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
