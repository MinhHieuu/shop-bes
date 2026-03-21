package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.ImageRequest;
import com.beeshop.sd44.dto.response.ImageResponse;
import com.beeshop.sd44.entity.Image;
import com.beeshop.sd44.entity.ProductDetail;
import com.beeshop.sd44.repository.ImageRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {
    private ImageRepo repo;

    public ImageService (ImageRepo repo) {
        this.repo = repo;

    }

    public List<ImageResponse> getAllImage(){
        List<ImageResponse> listResponse = new ArrayList<>();
        List<Image> list = repo.findAll();
        for(Image image : list) {
            ImageResponse imgRes = new ImageResponse();
            imgRes.setId(image.getId());
            imgRes.setUrl(image.getUrl());
//            imgRes.setProductDetailName(image.getProductDetail().getName());
            listResponse.add(imgRes);
        }
        return listResponse;
    }

    public List<ImageResponse> buildResponse(List<Image> images) {
        List<ImageResponse> responseList = new ArrayList<>();
        for(Image image : images) {
            ImageResponse response = new ImageResponse();
            response.setId(image.getId());
            response.setUrl(image.getUrl());
//            response.setProductDetailName(image.getProductDetail().getName());
            responseList.add(response);
        }
        return responseList;
    }

    public List<Image> buildImage(ImageRequest imageRequest) {
        ProductDetail detail = new ProductDetail();
        detail.setId(imageRequest.getProductDetailId());
        List<Image> images = new ArrayList<>();
        for(String s : imageRequest.getUrl()){
            Image image = new Image(s, detail);
            images.add(this.repo.save(image));
        }
        return images;
    }

    public List<ImageResponse> handleSaveImage(ImageRequest imageRequest) {
        return buildResponse(buildImage(imageRequest));
    }

    public List<Image> getImageByProductDetail(ProductDetail productDetail) {
        return this.repo.getImageByProductDetail(productDetail);
    }
}
