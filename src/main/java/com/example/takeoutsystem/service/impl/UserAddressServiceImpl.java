package com.example.takeoutsystem.service.impl;

import com.example.takeoutsystem.entity.UserAddress;
import com.example.takeoutsystem.entity.UserAddressForm;
import com.example.takeoutsystem.mapper.UserAddressMapper;
import com.example.takeoutsystem.service.UserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {
    private final UserAddressMapper userAddressMapper;

    public UserAddressServiceImpl(UserAddressMapper userAddressMapper) {
        this.userAddressMapper = userAddressMapper;
    }

    @Override
    public List<UserAddress> list(Integer userId) {
        return userAddressMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddress create(Integer userId, UserAddressForm form) {
        validate(form);
        UserAddress address = copyForm(userId, null, form);
        if (address.getIsDefault() == null) address.setIsDefault(userAddressMapper.countByUserId(userId) == 0 ? 1 : 0);
        if (Integer.valueOf(1).equals(address.getIsDefault())) userAddressMapper.clearDefault(userId);
        userAddressMapper.insert(address);
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddress update(Integer userId, Integer addressId, UserAddressForm form) {
        validate(form);
        UserAddress old = userAddressMapper.selectByIdAndUserId(addressId, userId);
        if (old == null) throw new IllegalArgumentException("地址不存在");
        UserAddress address = copyForm(userId, addressId, form);
        if (Integer.valueOf(1).equals(address.getIsDefault())) userAddressMapper.clearDefault(userId);
        userAddressMapper.update(address);
        return userAddressMapper.selectByIdAndUserId(addressId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer userId, Integer addressId) {
        UserAddress old = userAddressMapper.selectByIdAndUserId(addressId, userId);
        if (old == null) throw new IllegalArgumentException("地址不存在");
        userAddressMapper.deleteByIdAndUserId(addressId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Integer userId, Integer addressId) {
        UserAddress old = userAddressMapper.selectByIdAndUserId(addressId, userId);
        if (old == null) throw new IllegalArgumentException("地址不存在");
        userAddressMapper.clearDefault(userId);
        userAddressMapper.setDefault(addressId, userId);
    }

    private UserAddress copyForm(Integer userId, Integer addressId, UserAddressForm form) {
        UserAddress address = new UserAddress();
        address.setAddressId(addressId);
        address.setUserId(userId);
        address.setReceiverName(trim(form.getReceiverName()));
        address.setReceiverPhone(trim(form.getReceiverPhone()));
        address.setProvince(trim(form.getProvince()));
        address.setCity(trim(form.getCity()));
        address.setDistrict(trim(form.getDistrict()));
        address.setAddressDetail(trim(form.getAddressDetail()));
        address.setLatitude(form.getLatitude());
        address.setLongitude(form.getLongitude());
        address.setTag(trim(form.getTag()));
        address.setIsDefault(form.getIsDefault() == null ? 0 : form.getIsDefault());
        return address;
    }

    private void validate(UserAddressForm form) {
        if (form == null) throw new IllegalArgumentException("地址信息不能为空");
        if (isBlank(form.getReceiverName())) throw new IllegalArgumentException("收货人不能为空");
        if (isBlank(form.getReceiverPhone())) throw new IllegalArgumentException("手机号不能为空");
        if (isBlank(form.getAddressDetail())) throw new IllegalArgumentException("详细地址不能为空");
    }

    private String trim(String v) { return v == null ? null : v.trim(); }
    private boolean isBlank(String v) { return v == null || v.trim().isEmpty(); }
}
