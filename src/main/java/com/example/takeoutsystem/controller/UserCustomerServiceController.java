package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import com.example.takeoutsystem.common.UserContext;
import com.example.takeoutsystem.entity.CustomerServiceCreateForm;
import com.example.takeoutsystem.entity.CustomerServiceReplyForm;
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
@RequestMapping("/api/user/customer-service")
public class UserCustomerServiceController {
    private final CustomerServiceService customerServiceService;

    public UserCustomerServiceController(CustomerServiceService customerServiceService) {
        this.customerServiceService = customerServiceService;
    }

    @GetMapping("/tickets")
    public UserApiResult listTickets(HttpServletRequest request,
                                     @RequestParam(required = false) Integer status) {
        Integer userId = UserContext.getCurrentUserId(request);
        return UserApiResult.success(customerServiceService.listUserTickets(userId, status));
    }

    @PostMapping("/tickets")
    public UserApiResult createTicket(HttpServletRequest request,
                                      @RequestBody CustomerServiceCreateForm form) {
        Integer userId = UserContext.getCurrentUserId(request);
        return UserApiResult.success("工单已提交", customerServiceService.createTicket(userId, form));
    }

    @GetMapping("/tickets/{ticketId}")
    public UserApiResult detail(HttpServletRequest request,
                                @PathVariable Integer ticketId) {
        Integer userId = UserContext.getCurrentUserId(request);
        return UserApiResult.success(customerServiceService.getUserTicketDetail(userId, ticketId));
    }

    @PostMapping("/tickets/{ticketId}/messages")
    public UserApiResult reply(HttpServletRequest request,
                               @PathVariable Integer ticketId,
                               @RequestBody CustomerServiceReplyForm form) {
        Integer userId = UserContext.getCurrentUserId(request);
        return UserApiResult.success("发送成功", customerServiceService.replyByUser(userId, ticketId, form));
    }

    @PutMapping("/tickets/{ticketId}/close")
    public UserApiResult close(HttpServletRequest request,
                               @PathVariable Integer ticketId) {
        Integer userId = UserContext.getCurrentUserId(request);
        customerServiceService.closeByUser(userId, ticketId);
        return UserApiResult.success("工单已关闭", null);
    }
}
