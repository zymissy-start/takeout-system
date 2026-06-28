package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.AiCouponVO;
import com.example.takeoutsystem.entity.AiOrderVO;
import com.example.takeoutsystem.entity.AiProductVO;
import com.example.takeoutsystem.entity.AiRiderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiAgentMapper {
    List<AiProductVO> selectHotProducts(@Param("keyword") String keyword, @Param("limit") Integer limit);

    List<AiProductVO> selectProductsByIds(@Param("ids") List<Integer> ids);

    List<AiProductVO> selectHistoryProducts(@Param("userId") Integer userId, @Param("limit") Integer limit);

    List<AiCouponVO> selectAvailableCoupons(@Param("userId") Integer userId);

    AiOrderVO selectLatestActiveOrder(@Param("userId") Integer userId);

    AiOrderVO selectOrderById(@Param("userId") Integer userId, @Param("orderId") Integer orderId);

    List<AiOrderVO> selectRecentOrders(@Param("userId") Integer userId, @Param("limit") Integer limit);

    List<AiProductVO> selectOrderItems(@Param("orderId") Integer orderId);

    List<AiRiderVO> selectAvailableRiders(@Param("requiredLevel") Integer requiredLevel, @Param("limit") Integer limit);
}