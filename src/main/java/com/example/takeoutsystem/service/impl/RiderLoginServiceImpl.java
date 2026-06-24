package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.RiderMapper;
import com.example.takeoutsystem.service.RiderLoginService;
import org.springframework.stereotype.Service;

/**
 * 骑手登录业务实现。
 * 只允许 role_type = 3 且 status = 1 的用户登录骑手端。
 */
@Service
public class RiderLoginServiceImpl implements RiderLoginService {

    private final RiderMapper riderMapper;

    public RiderLoginServiceImpl(RiderMapper riderMapper) {
        this.riderMapper = riderMapper;
    }

    @Override
    public SysUser login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        return riderMapper.findRiderByUsernameAndPassword(username.trim(), password.trim());
    }
}