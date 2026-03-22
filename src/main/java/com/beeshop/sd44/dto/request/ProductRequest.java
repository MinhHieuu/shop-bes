package com.beeshop.sd44.dto.request;

import java.util.List;
import java.util.UUID;

public class ProductRequest {
    private UUID id;
    private String name;
    private String image;
    private Integer status;
    private UUID marterialId;
    private UUID brandId;
    private List<ProductDetailRequest> productDetails;
    private List<ProductDetailRequest> productDetailsUpdate;

    public List<ProductDetailRequest> getProductDetailsUpdate() {
        return productDetailsUpdate;
    }

    public List<ProductDetailRequest> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<ProductDetailRequest> productDetails) {
        this.productDetails = productDetails;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UUID getMarterialId() {
        return marterialId;
    }

    public void setMarterialId(UUID marterialId) {
        this.marterialId = marterialId;
    }

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }
}
