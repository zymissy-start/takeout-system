package com.example.takeoutsystem.common;

/**
 * 用户端接口统一返回体。为避免影响项目中已有 Result.java，用户模块单独使用本类。
 */
public class UserApiResult<T> {
    private int code;
    private String message;
    private T data;

    public UserApiResult() {}

    public UserApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> UserApiResult<T> success(T data) {
        return new UserApiResult<>(200, "success", data);
    }

    public static <T> UserApiResult<T> success(String message, T data) {
        return new UserApiResult<>(200, message, data);
    }

    public static <T> UserApiResult<T> error(String message) {
        return new UserApiResult<>(500, message, null);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
