package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.UserFavoriteMerchantVO;

import java.util.List;
import java.util.Map;

public interface UserFavoriteService {
    Map<String, Object> listMerchantFavorites(Integer userId, Integer page, Integer size);

    List<Integer> listFavoriteMerchantIds(Integer userId);

    Map<String, Object> addMerchantFavorite(Integer userId, Integer merchantId);

    Map<String, Object> removeMerchantFavorite(Integer userId, Integer merchantId);

    Map<String, Object> toggleMerchantFavorite(Integer userId, Integer merchantId);
}