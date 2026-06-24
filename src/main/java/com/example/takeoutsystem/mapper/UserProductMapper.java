package com.example.takeoutsystem.mapper;

import com.example.takeoutsystem.entity.UserCategoryVO;
import com.example.takeoutsystem.entity.UserProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProductMapper {
    List<UserCategoryVO> selectCategories();

    List<UserProductVO> selectProducts(@Param("keyword") String keyword,
                                       @Param("categoryId") Integer categoryId,
                                       @Param("sort") String sort,
                                       @Param("offset") Integer offset,
                                       @Param("size") Integer size);

    int countProducts(@Param("keyword") String keyword, @Param("categoryId") Integer categoryId);

    List<UserProductVO> selectProductsByIds(@Param("ids") List<Integer> ids);

    int decreaseStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    int increaseSales(@Param("productId") Integer productId, @Param("quantity") Integer quantity);
}
