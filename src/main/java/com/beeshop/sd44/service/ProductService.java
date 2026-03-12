package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.ProductRequest;
import com.beeshop.sd44.dto.response.ProductResponse;
import com.beeshop.sd44.entity.Brand;
import com.beeshop.sd44.entity.Marterial;
import com.beeshop.sd44.entity.Product;
import com.beeshop.sd44.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {
    private final ProductRepo repo;
    private final BrandService brandService;
    private final MarterialService marterialService;

    public ProductService(ProductRepo repo, BrandService brandService, MarterialService marterialService) {
        this.repo = repo;
        this.brandService = brandService;
        this.marterialService = marterialService;
    }

    public List<ProductResponse> getAll() {
       List<Product> list = this.repo.findAll();
       List<ProductResponse> listResponse = new ArrayList<>();
       for(Product product : list) {
            listResponse.add(hanldeResponse(product));
       }
       return listResponse;
    }

    public boolean isNameExit(String name) {
        return this.repo.existsByName(name);
    }

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setCreatedAt(new Date());
        return this.repo.save(buildProduct(product, request));
    }

    public ProductResponse hanldeResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrandId(product.getBrand().getId());
        response.setBrand(product.getBrand().getName());
        response.setMarterialId(product.getMarterial().getId());
        response.setMarterial(product.getMarterial().getName());
        response.setCreatedAt(product.getCreatedAt());
        response.setImage(product.getImage());
        response.setStatus(product.getStatus() == 1 ? "hoat dong" : "khong hoat dong");
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    public Product getById(UUID id) {
        Optional<Product> product = this.repo.findById(id);
        if(product.isPresent()) {
            return product.get();
        }
        return null;
    }

    public Product updateProduct(ProductRequest request) {
        Product product = this.getById(request.getId());
        product.setUpdatedAt(new Date());
        return this.repo.save(buildProduct(product, request));
    }

    public Product buildProduct(Product product, ProductRequest request) {
        Brand brand = brandService.getById(request.getBrandId());
        Marterial marterial = marterialService.getById(request.getMarterialId());
        product.setName(request.getName());
        product.setBrand(brand);
        product.setMarterial(marterial);
        product.setImage(request.getImage());
        product.setStatus(request.getStatus());
        return product;
    }
}
