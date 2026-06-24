package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.SysUser;

public interface RiderLoginService {

    SysUser login(String username, String password);
}