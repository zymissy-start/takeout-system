package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.MerchantShopForm;
import com.example.takeoutsystem.entity.MerchantShopInfo;

/**
 * 商家店铺信息业务接口。
 */
public interface MerchantShopService {

    MerchantShopInfo getShopInfo(Integer merchantId);

    MerchantShopInfo updateShopInfo(Integer merchantId, MerchantShopForm form);
}