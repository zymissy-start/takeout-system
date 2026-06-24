package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantFoodForm;
import com.example.takeoutsystem.entity.MerchantFoodVO;
import com.example.takeoutsystem.entity.MerchantProductVO;
import com.example.takeoutsystem.entity.ProductCategory;
import org.apache.ibatis.annotations.*;

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

    @Select("""
            SELECT
                p.product_id AS productId,
                p.category_id AS categoryId,
                c.category_name AS categoryName,
                p.name AS productName,
                p.name AS name,
                p.description AS description,
                p.price AS price,
                p.image_url AS imageUrl,
                DATE_FORMAT(p.upload_date, '%Y-%m-%d') AS uploadDate,
                p.order_count AS monthlySales,
                p.order_count AS orderCount,
                p.status AS status
            FROM product p
            LEFT JOIN product_category c ON p.category_id = c.category_id
            WHERE p.merchant_id = #{merchantId}
              AND (
                    #{keyword} IS NULL
                    OR #{keyword} = ''
                    OR p.name LIKE CONCAT('%', #{keyword}, '%')
                    OR p.description LIKE CONCAT('%', #{keyword}, '%')
                  )
              AND (
                    #{categoryId} IS NULL
                    OR p.category_id = #{categoryId}
                  )
              AND (
                    #{status} IS NULL
                    OR p.status = #{status}
                  )
            ORDER BY p.product_id DESC
            """)
    List<MerchantFoodVO> listFoods(@Param("merchantId") Integer merchantId,
                                   @Param("keyword") String keyword,
                                   @Param("categoryId") Integer categoryId,
                                   @Param("status") Integer status);

    @Select("""
            SELECT
                category_id AS categoryId,
                category_name AS categoryName
            FROM product_category
            ORDER BY category_id ASC
            """)
    List<ProductCategory> listCategories();

    @Insert("""
            INSERT INTO product
            (
                merchant_id,
                category_id,
                name,
                description,
                price,
                image_url,
                upload_date,
                order_count,
                status
            )
            VALUES
            (
                #{merchantId},
                #{form.categoryId},
                #{form.name},
                #{form.description},
                #{form.price},
                #{form.imageUrl},
                CURDATE(),
                0,
                1
            )
            """)
    int addFood(@Param("merchantId") Integer merchantId,
                @Param("form") MerchantFoodForm form);

    @Update("""
            UPDATE product
            SET category_id = #{form.categoryId},
                name = #{form.name},
                description = #{form.description},
                price = #{form.price},
                image_url = #{form.imageUrl}
            WHERE product_id = #{form.productId}
              AND merchant_id = #{merchantId}
            """)
    int updateFood(@Param("merchantId") Integer merchantId,
                   @Param("form") MerchantFoodForm form);

    @Update("""
            UPDATE product
            SET status = #{status}
            WHERE product_id = #{productId}
              AND merchant_id = #{merchantId}
            """)
    int updateFoodStatus(@Param("merchantId") Integer merchantId,
                         @Param("productId") Integer productId,
                         @Param("status") Integer status);
}