package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.SysUser;
import com.example.takeoutsystem.mapper.AdminManageMapper;
import com.example.takeoutsystem.service.AdminManageService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminManageServiceImpl implements AdminManageService {

    private final AdminManageMapper adminManageMapper;

    public AdminManageServiceImpl(AdminManageMapper adminManageMapper) {
        this.adminManageMapper = adminManageMapper;
    }

    @Override
    public Map<String, Object> listUsers(Integer roleType, String keyword, int page, int size) {
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 20;
        int offset = (page - 1) * size;

        List<SysUser> users = adminManageMapper.listUsersByRole(roleType, keyword, offset, size);
        int total = adminManageMapper.countByRole(roleType, keyword);

        // 清除密码
        users.forEach(u -> u.setPassword(null));

        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    @Override
    public Map<String, Object> getUserDetail(Integer userId) {
        SysUser user = adminManageMapper.selectUserDetail(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setPassword(null);

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);

        // 如果是商家，附加商家信息
        if (user.getRoleType() != null && user.getRoleType() == 2) {
            Map<String, Object> merchantInfo = adminManageMapper.selectMerchantDetail(userId);
            if (merchantInfo != null) {
                result.put("merchantInfo", merchantInfo);
            }
        }

        return result;
    }

    @Override
    public void updateUserStatus(Integer userId, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态值不合法");
        }
        SysUser user = adminManageMapper.selectUserDetail(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (user.getRoleType() != null && user.getRoleType() == 4) {
            throw new IllegalArgumentException("不能禁用管理员账号");
        }
        adminManageMapper.updateUserStatus(userId, status);
    }
}
