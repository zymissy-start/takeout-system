package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.MerchantProductVO;

import java.util.List;

public interface MerchantProductService {

    List<MerchantProductVO> listMerchantProducts(Integer merchantId, Integer size);
}