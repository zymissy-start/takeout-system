package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.UserCategoryVO;

import java.util.List;
import java.util.Map;

public interface UserProductService {
    List<UserCategoryVO> listCategories();
<<<<<<< HEAD
    Map<String, Object> pageProducts(String keyword, Integer categoryId, String sort, Integer page, Integer size);
=======
    Map<String, Object> pageMerchants(String keyword, Integer categoryId, String sort, Integer page, Integer size);
    Map<String, Object> pageProducts(String keyword, Integer categoryId, Integer merchantId, String sort, Integer page, Integer size);
>>>>>>> origin/feature-user-rider-merchant
}
