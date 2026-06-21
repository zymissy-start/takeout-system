package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.MerchantFoodForm;
import com.example.takeoutsystem.entity.MerchantFoodVO;
import com.example.takeoutsystem.entity.MerchantProductVO;
import com.example.takeoutsystem.entity.ProductCategory;

import java.util.List;

public interface MerchantProductService {

    List<MerchantProductVO> listMerchantProducts(Integer merchantId, Integer size);

    List<MerchantFoodVO> listFoods(Integer merchantId, String keyword, Integer categoryId, Integer status);

    List<ProductCategory> listCategories();

    boolean addFood(Integer merchantId, MerchantFoodForm form);

    boolean updateFood(Integer merchantId, MerchantFoodForm form);

    boolean updateFoodStatus(Integer merchantId, Integer productId, Integer status);
}