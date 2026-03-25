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
//    List<ProductDetail> getProductDetailByName(String name);
    List<ProductDetail> getProductDetailByProductId(UUID productId);
//    Boolean existsByName(String name);
//    Boolean existsByNameAndIdNot(String name, UUID id);
    Boolean existsByProductAndColorAndSize(Product product, Color color, Size size);
    Boolean existsByProductAndColorAndSizeAndIdNot(Product product, Color color, Size size, UUID id);
    @Query("SELECT pd FROM ProductDetail pd where (:name is null or pd.product.name like %:name%) " +
            "and (:colorId is null or pd.color.id = :colorId) " +
            "and (:sizeId is null or pd.size.id = :sizeId) " +
            "and (:salePrice is null or pd.salePrice = :salePrice)")
    List<ProductDetail> searchProductDetail(@Param("name") String name,
                                            @Param("colorId")  UUID colorId,
                                            @Param("sizeId")  UUID sizeId,
                                            @Param("salePrice") Double salePrice);

    @Query(value = """
            SELECT sp.ten as productName, ms.ten as colorName, sz.ten as sizeName, spct.so_luong as quantity
            FROM san_pham_chi_tiet spct
            JOIN san_pham sp ON sp.id = spct.san_pham_id
            JOIN mau_sac ms ON ms.id = spct.mau_sac_id
            JOIN size sz ON sz.id = spct.size_id
            WHERE spct.so_luong < :threshold
              AND spct.delete_flag = false
            ORDER BY spct.so_luong ASC
            """, nativeQuery = true)
    List<com.beeshop.sd44.dto.response.LowStockProduct> getLowStockProducts(@Param("threshold") int threshold);

}
