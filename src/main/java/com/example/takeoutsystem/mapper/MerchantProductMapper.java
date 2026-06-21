package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantProductVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MerchantProductMapper {

    @Select("""
            SELECT
                product_id AS productId,
                name AS productName,
                name AS name,
                price AS price,
                order_count AS monthlySales,
                status AS status
            FROM product
            WHERE merchant_id = #{merchantId}
            ORDER BY order_count DESC, product_id DESC
            LIMIT #{size}
            """)
    List<MerchantProductVO> listMerchantProducts(@Param("merchantId") Integer merchantId,
                                                 @Param("size") Integer size);
}