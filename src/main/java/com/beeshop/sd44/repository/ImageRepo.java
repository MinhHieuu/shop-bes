package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.Image;
import com.beeshop.sd44.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ImageRepo extends JpaRepository<Image, UUID> {
    List<Image> getImageByProductDetail(ProductDetail productDetail);
    List<Image> findByUrlIn(List<String> urls);
}
