package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.UserCategoryVO;
import com.example.takeoutsystem.entity.UserProductVO;
<<<<<<< HEAD
=======
import com.example.takeoutsystem.entity.UserMerchantVO;
>>>>>>> origin/feature-user-rider-merchant
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProductMapper {
    List<UserCategoryVO> selectCategories();

<<<<<<< HEAD
    List<UserProductVO> selectProducts(@Param("keyword") String keyword,
                                       @Param("categoryId") Integer categoryId,
=======
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
>>>>>>> origin/feature-user-rider-merchant
                                       @Param("sort") String sort,
                                       @Param("offset") Integer offset,
                                       @Param("size") Integer size);

<<<<<<< HEAD
    int countProducts(@Param("keyword") String keyword, @Param("categoryId") Integer categoryId);
=======
    int countProducts(@Param("keyword") String keyword,
                      @Param("categoryId") Integer categoryId,
                      @Param("merchantId") Integer merchantId);
>>>>>>> origin/feature-user-rider-merchant

    List<UserProductVO> selectProductsByIds(@Param("ids") List<Integer> ids);

    int decreaseStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    int increaseSales(@Param("productId") Integer productId, @Param("quantity") Integer quantity);
}
