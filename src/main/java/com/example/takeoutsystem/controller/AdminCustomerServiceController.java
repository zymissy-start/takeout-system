package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.AdminContext;
import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.entity.CustomerServiceReplyForm;
import com.example.takeoutsystem.entity.CustomerServiceStatusForm;
import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.SysUserMapper;
import com.example.takeoutsystem.service.CustomerServiceService;
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
@RequestMapping("/api/admin/customer-service")
public class AdminCustomerServiceController {
    private final CustomerServiceService customerServiceService;
    private final SysUserMapper sysUserMapper;

    public AdminCustomerServiceController(CustomerServiceService customerServiceService,
                                          SysUserMapper sysUserMapper) {
        this.customerServiceService = customerServiceService;
        this.sysUserMapper = sysUserMapper;
    }

    @GetMapping("/tickets")
    public UserApiResult listTickets(HttpServletRequest request,
                                     @RequestParam(required = false) Integer status,
                                     @RequestParam(required = false) String keyword) {
        requireAdmin(request);
        return UserApiResult.success(customerServiceService.listAdminTickets(status, keyword));
    }

    @GetMapping("/tickets/{ticketId}")
    public UserApiResult detail(HttpServletRequest request,
                                @PathVariable Integer ticketId) {
        Integer adminId = requireAdmin(request);
        return UserApiResult.success(customerServiceService.getAdminTicketDetail(adminId, ticketId));
    }

    @PostMapping("/tickets/{ticketId}/messages")
    public UserApiResult reply(HttpServletRequest request,
                               @PathVariable Integer ticketId,
                               @RequestBody CustomerServiceReplyForm form) {
        Integer adminId = requireAdmin(request);
        return UserApiResult.success("回复成功", customerServiceService.replyByAdmin(adminId, ticketId, form));
    }

    @PutMapping("/tickets/{ticketId}/status")
    public UserApiResult updateStatus(HttpServletRequest request,
                                      @PathVariable Integer ticketId,
                                      @RequestBody CustomerServiceStatusForm form) {
        Integer adminId = requireAdmin(request);
        customerServiceService.updateStatusByAdmin(adminId, ticketId, form == null ? null : form.getStatus());
        return UserApiResult.success("状态已更新", null);
    }

    private Integer requireAdmin(HttpServletRequest request) {
        Integer adminId = AdminContext.getCurrentAdminId(request);
        SysUser user = sysUserMapper.selectById(adminId);
        if (user == null || user.getRoleType() == null || user.getRoleType() != 4 || user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("无管理员权限");
        }
        return adminId;
    }
}
