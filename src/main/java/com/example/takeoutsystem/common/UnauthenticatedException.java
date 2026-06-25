package com.example.takeoutsystem.common;

/**
 * 未登录或登录态失效。
 */
public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String message) {
        super(message);
    }
}
