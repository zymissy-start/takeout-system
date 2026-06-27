package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.common.UserContext;
import com.example.takeoutsystem.entity.ContactCreateForm;
import com.example.takeoutsystem.entity.ContactReplyForm;
import com.example.takeoutsystem.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/contact")
public class UserContactController {

    private final ContactService contactService;

    public UserContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    private static final int USER_ROLE = 1;

    @GetMapping("/sessions")
    public UserApiResult listSessions(HttpServletRequest request) {
        Integer userId = UserContext.getCurrentUserId(request);
        return UserApiResult.success(contactService.listUserSessions(userId, USER_ROLE));
    }

    @GetMapping("/sessions/{sessionId}")
    public UserApiResult getSessionDetail(HttpServletRequest request,
                                          @PathVariable Integer sessionId) {
        Integer userId = UserContext.getCurrentUserId(request);
        return UserApiResult.success(contactService.getSessionDetail(sessionId, userId, USER_ROLE));
    }

    @PostMapping("/sessions")
    public UserApiResult createSession(HttpServletRequest request,
                                       @RequestBody ContactCreateForm form) {
        Integer userId = UserContext.getCurrentUserId(request);
        if (form == null || form.getTargetId() == null || form.getTargetRole() == null) {
            return UserApiResult.error("缺少目标用户信息");
        }
        if (form.getTargetRole() != 2 && form.getTargetRole() != 3) {
            return UserApiResult.error("只能联系商家或骑手");
        }
        return UserApiResult.success("会话已创建", contactService.createSession(userId, USER_ROLE, form));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public UserApiResult sendMessage(HttpServletRequest request,
                                    @PathVariable Integer sessionId,
                                    @RequestBody ContactReplyForm form) {
        Integer userId = UserContext.getCurrentUserId(request);
        if (form == null || form.getContent() == null || form.getContent().trim().isEmpty()) {
            return UserApiResult.error("消息内容不能为空");
        }
        return UserApiResult.success("发送成功",
                contactService.sendMessage(sessionId, userId, USER_ROLE, form.getContent().trim()));
    }

    @PutMapping("/sessions/{sessionId}/close")
    public UserApiResult closeSession(HttpServletRequest request,
                                      @PathVariable Integer sessionId) {
        Integer userId = UserContext.getCurrentUserId(request);
        contactService.closeSession(sessionId, userId, USER_ROLE);
        return UserApiResult.success("会话已关闭", null);
    }
}
