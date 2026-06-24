package com.example.takeoutsystem.controller;

import com.example.takeoutsystem.common.UserApiResult;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.example.takeoutsystem.controller.user")
public class UserExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
=======
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
>>>>>>> origin/feature-user-rider-merchant
    public UserApiResult<?> handleBiz(IllegalArgumentException e) {
        return UserApiResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
<<<<<<< HEAD
=======
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
>>>>>>> origin/feature-user-rider-merchant
    public UserApiResult<?> handleOther(Exception e) {
        return UserApiResult.error("系统异常：" + e.getMessage());
    }
}
