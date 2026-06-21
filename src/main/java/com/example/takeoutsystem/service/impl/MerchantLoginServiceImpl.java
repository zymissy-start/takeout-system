package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.SysUserMapper;
import com.example.takeoutsystem.service.MerchantLoginService;
import org.springframework.stereotype.Service;

@Service
public class MerchantLoginServiceImpl implements MerchantLoginService {

    private final SysUserMapper sysUserMapper;

    public MerchantLoginServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public SysUser login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        return sysUserMapper.findMerchantByUsernameAndPassword(username.trim(), password.trim());
    }
}