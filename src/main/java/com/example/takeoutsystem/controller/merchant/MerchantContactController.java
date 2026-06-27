package com.example.takeoutsystem.controller.merchant;

import com.example.takeoutsystem.common.Result;
import com.example.takeoutsystem.entity.ContactCreateForm;
import com.example.takeoutsystem.entity.ContactSessionVO;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.service.ContactService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/merchant/contact")
public class MerchantContactController {

    private final ContactService contactService;
    private static final int MERCHANT_ROLE = 2;

    public MerchantContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/sessions")
    public Result<?> listSessions(HttpSession session) {
        SysUser merchant = getLoginMerchant(session);
        if (merchant == null) return Result.fail("商家未登录");
        return Result.success("获取成功", contactService.listUserSessions(merchant.getUserId(), MERCHANT_ROLE));
    }

    @GetMapping("/unread-count")
    public Result<?> getUnreadCount(HttpSession session) {
        SysUser merchant = getLoginMerchant(session);
        if (merchant == null) return Result.fail("商家未登录");
        List<ContactSessionVO> sessions = contactService.listUserSessions(merchant.getUserId(), MERCHANT_ROLE);
        int total = sessions.stream()
                .filter(s -> s.getStatus() != null && s.getStatus() != 2)
                .mapToInt(s -> s.getUnreadCount() != null ? s.getUnreadCount() : 0)
                .sum();
        Map<String, Object> data = new HashMap<>();
        data.put("unreadCount", total);
        return Result.success("获取成功", data);
    }

    @GetMapping("/sessions/{sessionId}")
    public Result<?> getSessionDetail(@PathVariable Integer sessionId, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);
        if (merchant == null) return Result.fail("商家未登录");
        return Result.success("获取成功", contactService.getSessionDetail(sessionId, merchant.getUserId(), MERCHANT_ROLE));
    }

    @PostMapping("/sessions")
    public Result<?> createSession(@RequestBody ContactCreateForm form, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);
        if (merchant == null) return Result.fail("商家未登录");
        if (form.getTargetId() == null || form.getTargetRole() == null) {
            return Result.fail("缺少目标用户信息");
        }
        return Result.success("会话已创建", contactService.createSession(merchant.getUserId(), MERCHANT_ROLE, form));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public Result<?> sendMessage(@PathVariable Integer sessionId,
                                 @RequestParam String content,
                                 HttpSession session) {
        SysUser merchant = getLoginMerchant(session);
        if (merchant == null) return Result.fail("商家未登录");
        if (content == null || content.trim().isEmpty()) {
            return Result.fail("消息内容不能为空");
        }
        return Result.success("发送成功",
                contactService.sendMessage(sessionId, merchant.getUserId(), MERCHANT_ROLE, content.trim()));
    }

    @PutMapping("/sessions/{sessionId}/close")
    public Result<Void> closeSession(@PathVariable Integer sessionId, HttpSession session) {
        SysUser merchant = getLoginMerchant(session);
        if (merchant == null) return Result.fail("商家未登录");
        contactService.closeSession(sessionId, merchant.getUserId(), MERCHANT_ROLE);
        return Result.success("会话已关闭");
    }

    private SysUser getLoginMerchant(HttpSession session) {
        return (SysUser) session.getAttribute("loginMerchant");
    }
}
