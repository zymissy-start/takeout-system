package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.UserFavoriteMerchantVO;
import com.example.takeoutsystem.mapper.UserFavoriteMapper;
import com.example.takeoutsystem.service.UserFavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {
    private final UserFavoriteMapper userFavoriteMapper;

    public UserFavoriteServiceImpl(UserFavoriteMapper userFavoriteMapper) {
        this.userFavoriteMapper = userFavoriteMapper;
    }

    @Override
    public Map<String, Object> listMerchantFavorites(Integer userId, Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : Math.min(size, 50);
        int offset = (safePage - 1) * safeSize;

        List<UserFavoriteMerchantVO> rows = userFavoriteMapper.selectMerchantFavorites(userId, offset, safeSize);
        int total = userFavoriteMapper.countMerchantFavorites(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", rows);
        result.put("total", total);
        result.put("page", safePage);
        result.put("size", safeSize);
        result.put("hasMore", safePage * safeSize < total);
        return result;
    }

    @Override
    public List<Integer> listFavoriteMerchantIds(Integer userId) {
        return userFavoriteMapper.selectFavoriteMerchantIds(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addMerchantFavorite(Integer userId, Integer merchantId) {
        checkMerchant(merchantId);
        userFavoriteMapper.insertMerchantFavorite(userId, merchantId);
        return favoriteState(userId, merchantId, true, "收藏成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> removeMerchantFavorite(Integer userId, Integer merchantId) {
        userFavoriteMapper.deleteMerchantFavorite(userId, merchantId);
        return favoriteState(userId, merchantId, false, "已取消收藏");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> toggleMerchantFavorite(Integer userId, Integer merchantId) {
        checkMerchant(merchantId);

        boolean exists = userFavoriteMapper.countMerchantFavorite(userId, merchantId) > 0;

        if (exists) {
            userFavoriteMapper.deleteMerchantFavorite(userId, merchantId);
            return favoriteState(userId, merchantId, false, "已取消收藏");
        }

        userFavoriteMapper.insertMerchantFavorite(userId, merchantId);
        return favoriteState(userId, merchantId, true, "收藏成功");
    }

    private void checkMerchant(Integer merchantId) {
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("商家ID不能为空");
        }

        if (userFavoriteMapper.existsMerchant(merchantId) <= 0) {
            throw new IllegalArgumentException("商家不存在或已停业");
        }
    }

    private Map<String, Object> favoriteState(Integer userId,
                                              Integer merchantId,
                                              boolean favorite,
                                              String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("merchantId", merchantId);
        result.put("favorite", favorite);
        result.put("favoriteCount", userFavoriteMapper.countMerchantFavorites(userId));
        result.put("message", message);
        return result;
    }
}