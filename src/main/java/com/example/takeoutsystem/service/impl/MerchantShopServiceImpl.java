package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.MerchantShopForm;
import com.example.takeoutsystem.entity.MerchantShopInfo;
import com.example.takeoutsystem.mapper.MerchantShopMapper;
import com.example.takeoutsystem.service.MerchantShopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        String shopName = trim(form.getShopName());
        String contactPhone = trim(form.getContactPhone());
        String shopAddress = trim(form.getShopAddress());
        String shopNotice = trim(form.getShopNotice());
        String businessHours = trim(form.getBusinessHours());
        String deliveryDescription = trim(form.getDeliveryDescription());
        Integer businessStatus = form.getBusinessStatus();

        if (shopName == null || shopName.isEmpty()) {
            return null;
        }

        if (contactPhone != null && !contactPhone.isEmpty() && !contactPhone.matches("^1\\d{10}$")) {
            return null;
        }

        if (shopAddress == null || shopAddress.isEmpty()) {
            return null;
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
                shopName,
                contactPhone == null ? "" : contactPhone,
                shopAddress,
                shopNotice == null ? "" : shopNotice,
                businessHours == null || businessHours.isEmpty() ? "09:00-22:00" : businessHours,
                deliveryDescription == null || deliveryDescription.isEmpty() ? "商家接单后会尽快出餐" : deliveryDescription,
                businessStatus
        );

        merchantShopMapper.updateSysUserMerchantName(
                merchantId,
                shopName,
                contactPhone == null ? "" : contactPhone
        );

        return merchantShopMapper.findShopByMerchantId(merchantId);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}