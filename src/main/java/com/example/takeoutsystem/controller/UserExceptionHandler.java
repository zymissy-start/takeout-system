package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UnauthenticatedException;
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
        UserProfileController.class,
        UserCustomerServiceController.class,
        AdminAuthController.class,
        AdminCustomerServiceController.class
})
public class UserExceptionHandler {
    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public UserApiResult handleUnauthenticated(UnauthenticatedException e) {
        return UserApiResult.error(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UserApiResult handleBiz(IllegalArgumentException e) {
        return UserApiResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public UserApiResult handleOther(Exception e) {
        return UserApiResult.error("系统异常：" + e.getMessage());
    }
}
