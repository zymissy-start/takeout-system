package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.MerchantFoodForm;
import com.example.takeoutsystem.entity.MerchantFoodVO;
import com.example.takeoutsystem.entity.MerchantProductVO;
import com.example.takeoutsystem.entity.ProductCategory;
import com.example.takeoutsystem.mapper.MerchantProductMapper;
import com.example.takeoutsystem.service.MerchantProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MerchantProductServiceImpl implements MerchantProductService {

    private final MerchantProductMapper merchantProductMapper;

    public MerchantProductServiceImpl(MerchantProductMapper merchantProductMapper) {
        this.merchantProductMapper = merchantProductMapper;
    }

    @Override
    public List<MerchantProductVO> listMerchantProducts(Integer merchantId, Integer size) {
        if (size == null || size <= 0) {
            size = 4;
        }

        if (size > 50) {
            size = 50;
        }

        return merchantProductMapper.listMerchantProducts(merchantId, size);
    }

    @Override
    public List<MerchantFoodVO> listFoods(Integer merchantId, String keyword, Integer categoryId, Integer status) {
        if (keyword != null) {
            keyword = keyword.trim();
        }

        if (status != null && status != 0 && status != 1) {
            status = null;
        }

        return merchantProductMapper.listFoods(merchantId, keyword, categoryId, status);
    }

    @Override
    public List<ProductCategory> listCategories() {
        return merchantProductMapper.listCategories();
    }

    @Override
    public boolean addFood(Integer merchantId, MerchantFoodForm form) {
        if (!isValidForm(form, false)) {
            return false;
        }

        normalizeForm(form);

        return merchantProductMapper.addFood(merchantId, form) > 0;
    }

    @Override
    public boolean updateFood(Integer merchantId, MerchantFoodForm form) {
        if (!isValidForm(form, true)) {
            return false;
        }

        normalizeForm(form);

        return merchantProductMapper.updateFood(merchantId, form) > 0;
    }

    @Override
    public boolean updateFoodStatus(Integer merchantId, Integer productId, Integer status) {
        if (productId == null) {
            return false;
        }

        if (status == null || (status != 0 && status != 1)) {
            return false;
        }

        return merchantProductMapper.updateFoodStatus(merchantId, productId, status) > 0;
    }

    private boolean isValidForm(MerchantFoodForm form, boolean needProductId) {
        if (form == null) {
            return false;
        }

        if (needProductId && form.getProductId() == null) {
            return false;
        }

        if (form.getCategoryId() == null) {
            return false;
        }

        if (form.getName() == null || form.getName().trim().isEmpty()) {
            return false;
        }

        if (form.getPrice() == null || form.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        return true;
    }

    private void normalizeForm(MerchantFoodForm form) {
        form.setName(form.getName().trim());

        if (form.getDescription() != null) {
            form.setDescription(form.getDescription().trim());
        }

        if (form.getImageUrl() != null) {
            form.setImageUrl(form.getImageUrl().trim());
        }
    }
}