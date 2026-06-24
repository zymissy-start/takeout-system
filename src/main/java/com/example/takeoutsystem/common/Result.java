package com.example.takeoutsystem.common;
<<<<<<< HEAD
/**
 * 统一响应结果封装类。
 *
 * 前端所有 Ajax 请求都接收统一 JSON 格式：
 * {
 *   "success": true/false,
 *   "message": "提示信息",
 *   "data": 返回数据
 * }
 *
 * 这样可以降低前端判断复杂度，也方便后端统一处理成功和失败结果。
 */

public class Result<T> {
    /** 请求是否成功 */
    private Boolean success;
    /** 返回给前端的提示信息 */
    private String message;
    /** 实际业务数据，例如商家信息、订单列表、菜品列表等 */
=======

public class Result<T> {

    private Boolean success;
    private String message;
>>>>>>> origin/feature-user-rider-merchant
    private T data;

    public Result() {
    }

<<<<<<< HEAD
    /**
     * 返回带数据的成功结果。
     */
=======
>>>>>>> origin/feature-user-rider-merchant
    public Result(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, message, data);
    }

<<<<<<< HEAD
    /**
     * 返回不带数据的成功结果。
     */

=======
>>>>>>> origin/feature-user-rider-merchant
    public static <T> Result<T> success(String message) {
        return new Result<>(true, message, null);
    }

<<<<<<< HEAD
    /**
     * 返回失败结果。
     */

=======
>>>>>>> origin/feature-user-rider-merchant
    public static <T> Result<T> fail(String message) {
        return new Result<>(false, message, null);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}