package com.beeshop.sd44.dto.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProductResponse {
    private UUID id;
    private String name;
    private String image;
    private String status;
    private UUID marterialId;
    private String marterial;
    private UUID brandId;
    private String brand;
    private Date createdAt;
    private Date updatedAt;
    private List<ProductDetailResponse> detailList;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getMarterialId() {
        return marterialId;
    }

    public void setMarterialId(UUID marterialId) {
        this.marterialId = marterialId;
    }

    public String getMarterial() {
        return marterial;
    }

    public void setMarterial(String marterial) {
        this.marterial = marterial;
    }

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public List<ProductDetailResponse> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<ProductDetailResponse> detailList) {
        this.detailList = detailList;
    }
}
