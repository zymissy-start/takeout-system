package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.RiderNavigationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RiderNavigationMapper {

    RiderNavigationVO selectByOrderId(@Param("orderId") Integer orderId);

    RiderNavigationVO selectActiveByRiderId(@Param("riderId") Integer riderId);
}