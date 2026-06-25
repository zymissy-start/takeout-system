package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.MerchantShopForm;
import com.example.takeoutsystem.entity.MerchantShopInfo;
import com.example.takeoutsystem.mapper.MerchantShopMapper;
import com.example.takeoutsystem.service.MerchantShopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 商家店铺信息业务实现。
 */
@Service
public class MerchantShopServiceImpl implements MerchantShopService {

    private final MerchantShopMapper merchantShopMapper;

    public MerchantShopServiceImpl(MerchantShopMapper merchantShopMapper) {
        this.merchantShopMapper = merchantShopMapper;
    }

    @Override
    public MerchantShopInfo getShopInfo(Integer merchantId) {
        if (merchantId == null) {
            return null;
        }

        MerchantShopInfo shopInfo = merchantShopMapper.findShopByMerchantId(merchantId);

        if (shopInfo == null) {
            merchantShopMapper.insertDefaultShop(merchantId);
            shopInfo = merchantShopMapper.findShopByMerchantId(merchantId);
        }

        return shopInfo;
    }

    @Override
    @Transactional
    public MerchantShopInfo updateShopInfo(Integer merchantId, MerchantShopForm form) {
        if (merchantId == null || form == null) {
            return null;
        }

        String storeName = trim(form.getStoreName());
        String storeLogo = trim(form.getStoreLogo());
        String storeNotice = trim(form.getStoreNotice());
        String contactPhone = trim(form.getContactPhone());

        BigDecimal minOrderAmount = form.getMinOrderAmount();
        BigDecimal deliveryFee = form.getDeliveryFee();
        Integer deliveryTime = form.getDeliveryTime();
        BigDecimal distanceKm = form.getDistanceKm();
        Integer businessStatus = form.getBusinessStatus();

        if (storeName == null || storeName.isEmpty()) {
            return null;
        }

        if (contactPhone != null && !contactPhone.isEmpty() && !contactPhone.matches("^1\\d{10}$")) {
            return null;
        }

        if (minOrderAmount == null || minOrderAmount.compareTo(BigDecimal.ZERO) < 0) {
            minOrderAmount = BigDecimal.ZERO;
        }

        if (deliveryFee == null || deliveryFee.compareTo(BigDecimal.ZERO) < 0) {
            deliveryFee = new BigDecimal("3.00");
        }

        if (deliveryTime == null || deliveryTime <= 0) {
            deliveryTime = 30;
        }

        if (distanceKm == null || distanceKm.compareTo(BigDecimal.ZERO) < 0) {
            distanceKm = new BigDecimal("1.00");
        }

        if (businessStatus == null || (businessStatus != 0 && businessStatus != 1)) {
            businessStatus = 1;
        }

        MerchantShopInfo oldInfo = getShopInfo(merchantId);

        if (oldInfo == null) {
            return null;
        }

        merchantShopMapper.updateShop(
                merchantId,
                storeName,
                storeLogo == null ? "" : storeLogo,
                storeNotice == null || storeNotice.isEmpty() ? "欢迎光临本店" : storeNotice,
                minOrderAmount,
                deliveryFee,
                deliveryTime,
                distanceKm,
                businessStatus
        );

        merchantShopMapper.updateSysUserMerchantInfo(
                merchantId,
                storeName,
                contactPhone == null ? "" : contactPhone
        );

        return merchantShopMapper.findShopByMerchantId(merchantId);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}