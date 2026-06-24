package com.example.takeoutsystem.entity;

import java.util.List;

public class UserOrderCreateForm {
    private Integer addressId;
    private String remark;
    private List<UserOrderItemForm> items;

    public Integer getAddressId() { return addressId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public List<UserOrderItemForm> getItems() { return items; }
    public void setItems(List<UserOrderItemForm> items) { this.items = items; }
}
