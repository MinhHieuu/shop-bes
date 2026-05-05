package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.ProductDetailRequest;
import com.beeshop.sd44.dto.request.ProductRequest;
import com.beeshop.sd44.dto.response.ProductDetailResponse;
import com.beeshop.sd44.dto.response.ProductResponse;
import com.beeshop.sd44.entity.*;
import com.beeshop.sd44.repository.ImageRepo;
import com.beeshop.sd44.repository.ProductDetailRepo;
import com.beeshop.sd44.repository.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepo repo;
    private final BrandService brandService;
    private final MarterialService marterialService;
    private final ProductDetailRepo productDetailRepo;
    private final ImageRepo imageRepo;
    public ProductService(ProductRepo repo, ImageRepo imageRepo, ProductDetailRepo productDetailRepo, BrandService brandService, MarterialService marterialService) {
        this.repo = repo;
        this.brandService = brandService;
        this.marterialService = marterialService;
        this.productDetailRepo = productDetailRepo;
        this.imageRepo = imageRepo;
    }

    public List<ProductResponse> getAll() {
       List<Product> list = this.repo.findAll();
       List<ProductResponse> listResponse = new ArrayList<>();
       for(Product product : list) {
            listResponse.add(hanldeResponse(product));
       }
       return listResponse;
    }

    public List<ProductResponse> getByStatus() {
        List<Product> list = this.repo.findByStatus(1);
        List<ProductResponse> listResponse = new ArrayList<>();
        for(Product product : list) {
            listResponse.add(hanldeResponse(product));
        }
        return listResponse;
    }

    public boolean isNameExit(String name) {
        return this.repo.existsByName(name);
    }

    @Transactional
    public void createProduct(ProductRequest request) {
        Product product = new Product();
        product.setCreatedAt(new Date());
        product = buildProduct(product, request);
        this.repo.save(product);
        List<Image> images = saveDetail(product, request.getProductDetails(), null);

        this.imageRepo.saveAll(images);
    }

    public ProductResponse hanldeResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCode(product.getCode());
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

    @Transactional
    public void updateProduct(ProductRequest request) {
        Product product = this.getById(request.getId());
        product.setUpdatedAt(new Date());
        product = buildProduct(product, request);

        this.repo.save(product);
        List<Image> images = saveDetail(product, request.getProductDetails(), request.getProductDetailsUpdate());
        this.imageRepo.saveAll(images);
    }

    public Product buildProduct(Product product, ProductRequest requestBase) {
        Brand brand = brandService.getById(requestBase.getBrandId());
        Marterial marterial = marterialService.getById(requestBase.getMarterialId());
        product.setName(requestBase.getName());
        product.setCode(requestBase.getCode());
        product.setBrand(brand);
        product.setMarterial(marterial);
        product.setImage(requestBase.getImage());
        product.setStatus(requestBase.getStatus());
        return product;
    }

    private List<Image> saveDetail(Product product, List<ProductDetailRequest> details, List<ProductDetailRequest> listUpdate) {
        // tao detail
        List<ProductDetail> list = product.getList();
        List<Image> imagesAfterProductSaved = new ArrayList<>();
        //update cu
        if(listUpdate != null && !listUpdate.isEmpty()){
            for(ProductDetailRequest request: listUpdate){
                for(ProductDetail detail: list){
                    if(detail.getId().equals(request.getId())){
                        detail.setDescription(request.getDescription());
                        detail.setCostPrice(request.getCostPrice());
                        detail.setSalePrice(request.getSalePrice());
                        detail.setQuantity(request.getQuantity());

                        Color color = new Color();
                        color.setId(request.getColorId());
                        Size size = new Size();
                        size.setId(request.getSizeId());
                        detail.setColor(color);
                        detail.setSize(size);
                        imagesAfterProductSaved.addAll(this.saveImage(request.getImages(), request.getImagesDelete(), detail));
                    }
                }
            }
        }

        // them moi
        if(details != null && !details.isEmpty()){
            for(ProductDetailRequest request: details){
                ProductDetail detail = new ProductDetail();
                detail.setDescription(request.getDescription());
                detail.setCostPrice(request.getCostPrice());
                detail.setSalePrice(request.getSalePrice());
                detail.setQuantity(request.getQuantity());
                detail.setProduct(product);
                detail.setCode( getFirst6Chars(product.getId())+ '-'
                        + getFirst6Chars(request.getSizeId()) + '-' + getFirst6Chars(request.getColorId()));

                Color color = new Color();
                color.setId(request.getColorId());
                Size size = new Size();
                size.setId(request.getSizeId());
                detail.setColor(color);
                detail.setSize(size);
                detail.setDeleteFlag(request.isDeleteFlag());
                productDetailRepo.save(detail);
                list.add(detail);
                imagesAfterProductSaved.addAll(this.saveImage(request.getImages(), request.getImagesDelete(), detail));
            }
        }

        product.setList(list);
        return imagesAfterProductSaved;
    }
    private String getFirst6Chars(UUID id) {
        return id.toString().substring(0, 6);
    }
    List<Image> saveImage(List<String> imagesUrl, List<String> imagesDelete, ProductDetail detail){
        // xu ly luu anh
        if (!CollectionUtils.isEmpty(imagesDelete)) {
            List<Image> images = this.imageRepo.findByUrlIn(imagesDelete);
            this.imageRepo.deleteAll(images);
        }

        List<Image> images = new ArrayList<>();
        for (String imageUrl :imagesUrl) {
            Image image = new Image();
            image.setUrl(imageUrl);
            image.setProductDetail(detail);
            images.add(image);
        }
        return images;
    }
}
