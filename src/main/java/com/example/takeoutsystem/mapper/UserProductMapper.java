package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.UserCategoryVO;
import com.example.takeoutsystem.entity.UserProductVO;
import com.example.takeoutsystem.entity.UserMerchantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProductMapper {
    List<UserCategoryVO> selectCategories();

    List<UserMerchantVO> selectMerchants(@Param("keyword") String keyword,
                                         @Param("categoryId") Integer categoryId,
                                         @Param("sort") String sort,
                                         @Param("offset") Integer offset,
                                         @Param("size") Integer size);

    int countMerchants(@Param("keyword") String keyword,
                       @Param("categoryId") Integer categoryId);

    List<UserProductVO> selectProducts(@Param("keyword") String keyword,
                                       @Param("categoryId") Integer categoryId,
                                       @Param("merchantId") Integer merchantId,
                                       @Param("sort") String sort,
                                       @Param("offset") Integer offset,
                                       @Param("size") Integer size);

    int countProducts(@Param("keyword") String keyword,
                      @Param("categoryId") Integer categoryId,
                      @Param("merchantId") Integer merchantId);

    List<UserProductVO> selectProductsByIds(@Param("ids") List<Integer> ids);

    int decreaseStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    int increaseSales(@Param("productId") Integer productId, @Param("quantity") Integer quantity);
}
