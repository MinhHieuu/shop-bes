package com.beeshop.sd44.dto.request;

import java.util.UUID;

public class CartRequest {
    private UUID customerId;

    public UUID getProductDetailId() {
        return productDetailId;
    }

    public void setProductDetailId(UUID productDetailId) {
        this.productDetailId = productDetailId;
    }

    private UUID productDetailId; // sửa lại
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    private UUID productId;
    private Integer quantity;


}
