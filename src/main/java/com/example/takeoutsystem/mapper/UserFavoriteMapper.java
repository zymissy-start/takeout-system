package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.UserFavoriteMerchantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {
    int existsMerchant(@Param("merchantId") Integer merchantId);

    int insertMerchantFavorite(@Param("userId") Integer userId,
                               @Param("merchantId") Integer merchantId);

    int deleteMerchantFavorite(@Param("userId") Integer userId,
                               @Param("merchantId") Integer merchantId);

    int countMerchantFavorite(@Param("userId") Integer userId,
                              @Param("merchantId") Integer merchantId);

    List<Integer> selectFavoriteMerchantIds(@Param("userId") Integer userId);

    List<UserFavoriteMerchantVO> selectMerchantFavorites(@Param("userId") Integer userId,
                                                         @Param("offset") Integer offset,
                                                         @Param("size") Integer size);

    int countMerchantFavorites(@Param("userId") Integer userId);
}