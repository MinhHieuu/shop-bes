package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.ProductRequest;
import com.beeshop.sd44.dto.response.ProductResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Product;
import com.beeshop.sd44.service.ProductDetailService;
import com.beeshop.sd44.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class ProductController {
    private final ProductService productService;
    private final ProductDetailService productDetailService;

    public ProductController(ProductService productService,
                             ProductDetailService productDetailService) {
        this.productService = productService;
        this.productDetailService = productDetailService;
    }

    @GetMapping("san-pham")
    public ResponseEntity<?> getAllProduct() {
        List<ProductResponse> list = this.productService.getAll();
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", list));
    }

    @GetMapping("san-pham/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable("id") UUID id) {
        Product product = this.productService.getById(id);
        if (product == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay san pham", null));
        }
        ProductResponse response = this.productService.hanldeResponse(product);
        response.setDetailList(this.productDetailService.getListByProductId(id));
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", response));
    }

    @PostMapping("san-pham")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        if(this.productService.isNameExit(productRequest.getName())) {
            return ResponseEntity.status(409).body(new ApiResponse<>("trung ten san pham", null));
        }
        Product product = this.productService.createProduct(productRequest);
        ProductResponse productResponse = this.productService.hanldeResponse(product);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", productResponse));
    }

    @PutMapping("san-pham")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product product = this.productService.getById(productRequest.getId());
        if(product == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay san pham", null));
        }
        if(this.productService.isNameExit(productRequest.getName()) && !product.getName().equals(productRequest.getName())) {
            return ResponseEntity.status(409).body(new ApiResponse<>("trung ten san pham", null));
        }
        Product newProduct = this.productService.updateProduct(productRequest);
        ProductResponse productResponse = this.productService.hanldeResponse(newProduct);
        return ResponseEntity.status(200).body(new ApiResponse<>("sua thanh cong", productResponse));
    }
}
