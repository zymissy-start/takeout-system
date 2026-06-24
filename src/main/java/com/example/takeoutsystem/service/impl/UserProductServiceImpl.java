package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.UserCategoryVO;
import com.example.takeoutsystem.entity.UserProductVO;
import com.example.takeoutsystem.mapper.UserProductMapper;
import com.example.takeoutsystem.service.UserProductService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserProductServiceImpl implements UserProductService {
    private final UserProductMapper userProductMapper;

    public UserProductServiceImpl(UserProductMapper userProductMapper) {
        this.userProductMapper = userProductMapper;
    }

    @Override
    public List<UserCategoryVO> listCategories() {
        return userProductMapper.selectCategories();
    }

    @Override
    public Map<String, Object> pageProducts(String keyword, Integer categoryId, String sort, Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 12 : Math.min(size, 50);
        int offset = (safePage - 1) * safeSize;
        List<UserProductVO> rows = userProductMapper.selectProducts(blankToNull(keyword), categoryId, sort, offset, safeSize);
        int total = userProductMapper.countProducts(blankToNull(keyword), categoryId);
        Map<String, Object> result = new HashMap<>();
        result.put("records", rows);
        result.put("total", total);
        result.put("page", safePage);
        result.put("size", safeSize);
        result.put("hasMore", safePage * safeSize < total);
        return result;
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
