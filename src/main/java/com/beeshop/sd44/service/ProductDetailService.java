package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.ProductDetailRequest;
import com.beeshop.sd44.dto.response.ProductDetailResponse;
import com.beeshop.sd44.dto.response.ProductSale;
import com.beeshop.sd44.entity.*;
import com.beeshop.sd44.repository.ImageRepo;
import com.beeshop.sd44.repository.OrderRepo;
import com.beeshop.sd44.repository.ProductDetailRepo;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    private final ImageRepo imageRepo;
    private final OrderRepo orderRepo;

    public ProductDetailService(ProductDetailRepo productDetailRepo, ProductService productService,
            ColorService colorService, SizeService sizeService, ImageRepo imageRepo, OrderRepo orderRepo,
            ImageService imageService) {
        this.productDetailRepo = productDetailRepo;
        this.productService = productService;
        this.colorService = colorService;
        this.sizeService = sizeService;
        this.imageService = imageService;
        this.imageRepo = imageRepo;
        this.orderRepo = orderRepo;
    }

    public List<ProductDetailResponse> getAll() {
        List<ProductDetail> detailList = this.productDetailRepo.findAll();
        List<ProductDetailResponse> responseList = new ArrayList<>();
        for (ProductDetail detail : detailList) {
            responseList.add(buildResponse(detail));
        }
        return responseList;
    }

    public List<ProductSale> getListSaler(String productId) {
        return this.orderRepo.getListSaler(productId);
    }

    public List<ProductSale> getListSalerByProductId(String productId) {
        return this.orderRepo.getListSalerByProductId(productId);
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

    @Transactional
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

        // xu ly luu anh
        if (!CollectionUtils.isEmpty(request.getImagesDelete())) {
            List<Image> images = this.imageRepo.findByUrlIn(request.getImagesDelete());
            this.imageRepo.deleteAll(images);
        }

        List<Image> images = new ArrayList<>();
        for (String imageUrl : request.getImages()) {
            Image image = new Image();
            image.setUrl(imageUrl);
            image.setProductDetail(detail);
            images.add(image);
        }
        this.imageRepo.saveAll(images);

        return this.productDetailRepo.save(detail);
    }

    @Transactional
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

    public Boolean isNameExit(String name, UUID excludeId) {
        return this.productDetailRepo.existsByNameAndIdNot(name, excludeId);
    }

    public Boolean isProductExit(Product product, Color color, Size size) {
        return this.productDetailRepo.existsByProductAndColorAndSize(product, color, size);
    }

    public Boolean isProductExit(Product product, Color color, Size size, UUID excludeId) {
        return this.productDetailRepo.existsByProductAndColorAndSizeAndIdNot(product, color, size, excludeId);
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
        if (productDetail == null)
            return;
        if (productDetail.getQuantity() < quantity)
            return;
        // If order is confirmed/processed (status == 1), decrease stock by ordered
        // quantity
        if (status == 1) {
            productDetail.setQuantity(productDetail.getQuantity() - quantity);
        }
        // If order is cancelled (status == 3), restore stock by ordered quantity
        else if (status == 3) {
            productDetail.setQuantity(productDetail.getQuantity() + quantity);
        }

        this.productDetailRepo.save(productDetail);
    }

}
