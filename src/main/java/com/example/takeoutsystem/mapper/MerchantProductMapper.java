package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.MerchantFoodForm;
import com.example.takeoutsystem.entity.MerchantFoodVO;
import com.example.takeoutsystem.entity.MerchantProductVO;
import com.example.takeoutsystem.entity.ProductCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商家商品 Mapper。
 *
 * 负责商品列表查询、分类查询、新增商品、修改商品、上下架商品等数据库操作。
 */
public interface MerchantProductMapper {
    /**
     * 查询商家首页热卖商品。
     *
     * order_count 表示累计点餐次数，按该字段倒序排列可以展示销量高的商品。
     */

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
    /**
     * 查询菜品管理列表。
     *
     * 支持三个条件：
     * 1. keyword：按商品名称或描述模糊搜索；
     * 2. categoryId：按分类筛选；
     * 3. status：按上架/下架状态筛选。
     */
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
    /** 查询所有商品分类，用于前端下拉框。 */
    @Select("""
            SELECT
                category_id AS categoryId,
                category_name AS categoryName
            FROM product_category
            ORDER BY category_id ASC
            """)
    List<ProductCategory> listCategories();
    /**
     * 修改菜品状态。
     * status = 1 表示上架，status = 0 表示下架。
     */
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