package com.beeshop.sd44.controller;

import com.beeshop.sd44.dto.request.ProductRequest;
import com.beeshop.sd44.dto.response.ProductResponse;
import com.beeshop.sd44.entity.ApiResponse;
import com.beeshop.sd44.entity.Product;
import com.beeshop.sd44.service.MarterialService;
import com.beeshop.sd44.service.FileService;
import com.beeshop.sd44.service.ProductService;
import com.beeshop.sd44.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class ProductController {
    private final ProductService productService;
    private final BrandService brandService;
    private final MarterialService marterialService;
    private final FileService fileService;

    public ProductController(ProductService productService,
                             BrandService brandService,
                             MarterialService marterialService, FileService fileService) {
        this.productService = productService;
        this.brandService = brandService;
        this.marterialService = marterialService;
        this.fileService = fileService;
    }

    @GetMapping("san-pham")
    public ResponseEntity<?> getAllProduct() {
        List<ProductResponse> list = this.productService.getAll();
        return ResponseEntity.ok(new ApiResponse<>("lay thanh cong", list));
    }

    @PostMapping("san-pham")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest productRequest, BindingResult result) {
        List<FieldError> errors = result.getFieldErrors();
        Boolean exitName = this.productService.isNameExit(productRequest.getName());
        if(result.hasErrors()) {
            String errorMessages = errors.stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ApiResponse<>(errorMessages, null));
        }
        if(exitName == true) {
            return ResponseEntity.status(409).body(new ApiResponse<>("trung ten san pham", null));
        }
        Product product = this.productService.createProduct(productRequest);
        ProductResponse productResponse = this.productService.hanldeResponse(product);
        return ResponseEntity.status(201).body(new ApiResponse<>("tao moi thanh cong", productResponse));
    }

    @PutMapping("san-pham")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@Valid @RequestBody ProductRequest productRequest,
                                                                      BindingResult result) {
        Product product = this.productService.getById(productRequest.getId());
        if(product == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("khong tim thay san pham", null));
        }
        List<FieldError> errors = result.getFieldErrors();
        Boolean exitName = this.productService.isNameExit(productRequest.getName());
        if(result.hasErrors()) {
            String errorMessages = errors.stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ApiResponse<>(errorMessages, null));
        }
        if(exitName == true && !product.getName().equals(productRequest.getName())) {
            return ResponseEntity.status(409).body(new ApiResponse<>("trung ten san pham", null));
        }
        Product newProduct = this.productService.updateProduct(productRequest);
        ProductResponse productResponse = this.productService.hanldeResponse(newProduct);
        return ResponseEntity.status(200).body(new ApiResponse<>("sua thanh cong", productResponse));
    }
}
