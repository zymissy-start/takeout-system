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
<<<<<<< HEAD
/**
 * 商家商品业务逻辑实现类。
 *
 * 主要负责商品查询、新增、修改、上下架，
 * 并在进入 Mapper 前进行必要的参数校验。
 */
=======

>>>>>>> origin/feature-user-rider-merchant
@Service
public class MerchantProductServiceImpl implements MerchantProductService {

    private final MerchantProductMapper merchantProductMapper;

    public MerchantProductServiceImpl(MerchantProductMapper merchantProductMapper) {
        this.merchantProductMapper = merchantProductMapper;
    }
<<<<<<< HEAD
    /**
     * 查询商家首页商品预览。
     * size 限制最大值，避免一次查询过多数据影响性能。
     */
=======

>>>>>>> origin/feature-user-rider-merchant
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

<<<<<<< HEAD
    /**
     * 查询菜品管理列表。
     * 对 keyword 去除前后空格，对非法 status 进行清理。
     */

=======
>>>>>>> origin/feature-user-rider-merchant
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

<<<<<<< HEAD
    /** 查询商品分类。 */
=======
>>>>>>> origin/feature-user-rider-merchant
    @Override
    public List<ProductCategory> listCategories() {
        return merchantProductMapper.listCategories();
    }

<<<<<<< HEAD
    /**
     * 新增菜品。
     * 新增时不需要 productId，但必须校验分类、名称和价格。
     */
=======
>>>>>>> origin/feature-user-rider-merchant
    @Override
    public boolean addFood(Integer merchantId, MerchantFoodForm form) {
        if (!isValidForm(form, false)) {
            return false;
        }

        normalizeForm(form);

        return merchantProductMapper.addFood(merchantId, form) > 0;
    }
<<<<<<< HEAD
    /**
     * 修改菜品。
     * 修改时必须携带 productId，防止无法定位具体商品。
     */
=======
>>>>>>> origin/feature-user-rider-merchant

    @Override
    public boolean updateFood(Integer merchantId, MerchantFoodForm form) {
        if (!isValidForm(form, true)) {
            return false;
        }

        normalizeForm(form);

        return merchantProductMapper.updateFood(merchantId, form) > 0;
    }
<<<<<<< HEAD
    /**
     * 上架或下架菜品。
     */
=======
>>>>>>> origin/feature-user-rider-merchant

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
<<<<<<< HEAD
    /**
     * 表单校验方法。
     * 价格必须大于 0，名称不能为空，分类不能为空。
     */
=======
>>>>>>> origin/feature-user-rider-merchant

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
<<<<<<< HEAD
    /**
     * 统一清理表单文本，避免用户输入多余空格。
     */
=======
>>>>>>> origin/feature-user-rider-merchant

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