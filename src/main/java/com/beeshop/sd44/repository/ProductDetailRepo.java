package com.beeshop.sd44.repository;


import com.beeshop.sd44.entity.Color;
import com.beeshop.sd44.entity.Product;
import com.beeshop.sd44.entity.ProductDetail;
import com.beeshop.sd44.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, UUID> {
    List<ProductDetail> getProductDetailByDeleteFlag(boolean deleteFlag);
    List<ProductDetail> getProductDetailByName(String name);
        List<ProductDetail> getProductDetailByProductId(UUID productId);
    Boolean existsByName(String name);
    Boolean existsByProductAndColorAndSize(Product product, Color color, Size size);
    @Query("SELECT pd FROM ProductDetail pd where :name is null or pd.name like %:name% " +
            "and :colorId is null or pd.color = :colorId " +
            "and :sizeId is null or pd.size = :sizeId " +
            "and :salePrice is null or pd.salePrice = :salePrice")
    List<ProductDetail> searchProductDetail(@Param("name") String name,
                                            @Param("colorId")  UUID colorId,
                                            @Param("sizeId")  UUID sizeId,
                                            @Param("salePrice") Double salePrice);
}
