package com.example.takeoutsystem.service;

import com.example.takeoutsystem.entity.UserAddress;
import com.example.takeoutsystem.entity.UserAddressForm;

import java.util.List;

public interface UserAddressService {
    List<UserAddress> list(Integer userId);
    UserAddress create(Integer userId, UserAddressForm form);
    UserAddress update(Integer userId, Integer addressId, UserAddressForm form);
    void delete(Integer userId, Integer addressId);
    void setDefault(Integer userId, Integer addressId);
}
