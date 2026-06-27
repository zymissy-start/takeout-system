package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.SysUser;

import java.util.Map;

public interface AdminManageService {

    Map<String, Object> listUsers(Integer roleType, String keyword, int page, int size);

    Map<String, Object> getUserDetail(Integer userId);

    void updateUserStatus(Integer userId, Integer status);
}
