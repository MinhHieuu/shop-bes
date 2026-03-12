package com.beeshop.sd44.dto.response;

import java.util.UUID;

public class CartDetailResponse {
    private UUID id;
    private ProductDetailResponse productDetail;
    private Integer quantity;
    private Double totalPrice;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ProductDetailResponse getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(ProductDetailResponse productDetail) {
        this.productDetail = productDetail;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
