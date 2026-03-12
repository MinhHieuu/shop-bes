package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.ProductDetailRequest;
import com.beeshop.sd44.dto.response.ProductDetailResponse;
import com.beeshop.sd44.entity.*;
import com.beeshop.sd44.repository.ProductDetailRepo;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductDetailService {
    private final ProductDetailRepo productDetailRepo;
    private final ProductService productService;
    private final ColorService colorService;
    private final SizeService sizeService;
    private final ImageService imageService;

    public ProductDetailService(ProductDetailRepo productDetailRepo, ProductService productService,
            ColorService colorService, SizeService sizeService,
            ImageService imageService) {
        this.productDetailRepo = productDetailRepo;
        this.productService = productService;
        this.colorService = colorService;
        this.sizeService = sizeService;
        this.imageService = imageService;
    }

    public List<ProductDetailResponse> getAll() {
        List<ProductDetail> detailList = this.productDetailRepo.findAll();
        List<ProductDetailResponse> responseList = new ArrayList<>();
        for (ProductDetail detail : detailList) {
            responseList.add(buildResponse(detail));
        }
        return responseList;
    }

    public ProductDetailResponse buildResponse(ProductDetail detail) {
        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(detail.getId());
        response.setName(detail.getName());
        response.setDescription(detail.getDescription());
        response.setQuantity(detail.getQuantity());
        response.setCostPrice(detail.getCostPrice());
        response.setSalePrice(detail.getSalePrice());
        response.setProductId(detail.getProduct().getId());
        response.setProductName(detail.getProduct().getName());
        response.setSizeId(detail.getSize().getId());
        response.setSizeName(detail.getSize().getName());
        response.setColorId(detail.getColor().getId());
        response.setColorName(detail.getColor().getName());
        response.setImages(this.getImages(detail));
        return response;
    }

    public ProductDetailResponse createProductDetail(ProductDetailRequest request) {
        return buildResponse(buildProductDetail(new ProductDetail(), request));
    }

    public ProductDetail buildProductDetail(ProductDetail detail, ProductDetailRequest request) {
        detail.setName(request.getName());
        detail.setDescription(request.getDescription());
        detail.setCostPrice(request.getCostPrice());
        detail.setSalePrice(request.getSalePrice());
        detail.setQuantity(request.getQuantity());
        detail.setProduct(this.productService.getById(request.getProductId()));
        detail.setColor((this.colorService.getById(request.getColorId())));
        detail.setSize(this.sizeService.getById(request.getSizeId()));
        detail.setDeleteFlag(request.isDeleteFlag());
        return this.productDetailRepo.save(detail);
    }

    public ProductDetailResponse updateProduct(ProductDetailRequest request) {
        return buildResponse(buildProductDetail(this.getById(request.getId()), request));
    }

    public ProductDetail getById(UUID id) {
        Optional<ProductDetail> detail = this.productDetailRepo.findById(id);
        if (detail.isPresent()) {
            return detail.get();
        }
        return null;
    }

    public void delete(UUID id) {
        ProductDetail detail = this.getById(id);
        detail.setDeleteFlag(true);
    }

    public List<ProductDetailResponse> getListDetail(boolean deleteFlag) {
        List<ProductDetail> list = this.productDetailRepo.getProductDetailByDeleteFlag(deleteFlag);
        List<ProductDetailResponse> listResponse = new ArrayList<>();
        for (ProductDetail detail : list) {
            listResponse.add(buildResponse(detail));
        }
        return listResponse;
    }

    public List<ProductDetailResponse> getListByProductId(UUID productId) {
        List<ProductDetail> list = this.productDetailRepo.getProductDetailByProductId(productId);
        List<ProductDetailResponse> listResponse = new ArrayList<>();
        for (ProductDetail detail : list) {
            listResponse.add(buildResponse(detail));
        }
        return listResponse;
    }

    public List<ProductDetail> getByName(String name) {
        return this.productDetailRepo.getProductDetailByName(name);
    }

    public Boolean isNameExit(String name) {
        return this.productDetailRepo.existsByName(name);
    }

    public Boolean isProductExit(Product product, Color color, Size size) {
        return this.productDetailRepo.existsByProductAndColorAndSize(product, color, size);
    }

    private List<String> getImages(ProductDetail detail) {
        List<String> images = new ArrayList<>();
        List<Image> list = imageService.getImageByProductDetail(detail);
        for (int i = 0; i < list.size(); i++) {
            images.add(list.get(i).getUrl());
        }
        return images;
    }

    public List<ProductDetailResponse> search(String name, UUID colorId, UUID sizeId, Double salePrice) {
        List<ProductDetail> list = this.productDetailRepo.searchProductDetail(name, colorId, sizeId, salePrice);
        List<ProductDetailResponse> listResponse = new ArrayList<>();
        for (ProductDetail detail : list) {
            listResponse.add(buildResponse(detail));
        }
        return listResponse;
    }

    public void updateQuantity(UUID id, int status, int quantity) {
        ProductDetail productDetail = getById(id);
        if(productDetail.getQuantity() < quantity) {
            return;
        }
        if(status == 1) {
            productDetail.setQuantity(productDetail.getQuantity() -1);
        } else if(status == 5){
            productDetail.setQuantity(productDetail.getQuantity() + 1);
        }
    }

}
