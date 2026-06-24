package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.example.takeoutsystem.controller.user")
public class UserExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public UserApiResult<?> handleBiz(IllegalArgumentException e) {
        return UserApiResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public UserApiResult<?> handleOther(Exception e) {
        return UserApiResult.error("系统异常：" + e.getMessage());
    }
}
