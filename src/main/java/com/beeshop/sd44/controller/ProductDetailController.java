package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.ProductDetailRequest;
import com.beeshop.sd44.dto.response.ProductDetailResponse;
import com.beeshop.sd44.entity.*;
import com.beeshop.sd44.service.ProductDetailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/product-detail")
public class ProductDetailController {
    private final ProductDetailService productDetailService;
    public ProductDetailController(ProductDetailService productDetailService) {
        this.productDetailService = productDetailService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ProductDetailResponse>>> getListProductDetail() {
        List<ProductDetailResponse> list = this.productDetailService.getListDetail(false);
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", list));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getDetailById(@PathVariable("id") UUID id) {
        ProductDetail detail = this.productDetailService.getById(id);
        if (detail == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay san pham", null));
        }
        return ResponseEntity.ok().body(new ApiResponse<>("lay thanh cong", this.productDetailService.buildResponse(detail)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProductDetail(@Valid @RequestBody ProductDetailRequest detail) {
        if(this.productDetailService.isNameExit(detail.getName())) {
            return ResponseEntity.status(409).body(new ApiResponse<>("ten da ton tai", null));
        }
        Color color = new Color(detail.getColorId());
        Size size = new Size(detail.getSizeId());
        Product product = new Product();
        product.setId(detail.getProductId());
        if(this.productDetailService.isProductExit(product, color, size)) {
            return ResponseEntity.status(409).body(new ApiResponse<>("san pham da ton tai", null));
        }
        ProductDetailResponse productDetail = this.productDetailService.createProductDetail(detail);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", productDetail));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<ProductDetail>> deleteProductDetail(@PathVariable("id")UUID id){
        this.productDetailService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse<>("xoa thanh cong", null));
    }

    @PutMapping("")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProductDetail(@Valid @RequestBody ProductDetailRequest request) {
        ProductDetail detail = this.productDetailService.getById(request.getId());
        if (detail == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay san pham chi tiet", null));
        }
        if (this.productDetailService.isNameExit(request.getName(), request.getId())) {
            return ResponseEntity.status(409).body(new ApiResponse<>("trung ten san pham", null));
        }
        Color color = new Color(request.getColorId());
        Size size = new Size(request.getSizeId());
        Product product = new Product();
        product.setId(request.getProductId());
        if (this.productDetailService.isProductExit(product, color, size, request.getId())) {
            return ResponseEntity.status(409).body(new ApiResponse<>("san pham da ton tai", null));
        }
        return ResponseEntity.ok().body(new ApiResponse<>("sua thanh cong", this.productDetailService.updateProduct(request)));
    }

    @GetMapping("search")
    public ResponseEntity<ApiResponse<List<ProductDetailResponse>>> search(@RequestParam(value = "name", required = false) String name,
                                                                           @RequestParam(value = "color", required = false) UUID colorId,
                                                                           @RequestParam(value = "size", required = false) UUID sizeId,
                                                                           @RequestParam(value = "salePrice", required = false) Double salePrice){
        List<ProductDetailResponse> listResponse = this.productDetailService.search(name, colorId, sizeId, salePrice);
        if(listResponse.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay san pham", null));
        }
        return ResponseEntity.ok().body(new ApiResponse<>("tim thanh cong", listResponse));
    }
}
