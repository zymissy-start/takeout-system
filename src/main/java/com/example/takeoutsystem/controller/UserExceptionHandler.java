package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {
        UserAuthController.class,
        UserAddressController.class,
        UserOrderController.class,
        UserProductController.class,
        UserProfileController.class
})
public class UserExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UserApiResult<?> handleBiz(IllegalArgumentException e) {
        return UserApiResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public UserApiResult<?> handleOther(Exception e) {
        return UserApiResult.error("系统异常：" + e.getMessage());
    }
}
