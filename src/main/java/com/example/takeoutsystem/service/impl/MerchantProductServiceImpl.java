package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.MerchantProductVO;
import com.example.takeoutsystem.mapper.MerchantProductMapper;
import com.example.takeoutsystem.service.MerchantProductService;
import org.springframework.stereotype.Service;

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
}