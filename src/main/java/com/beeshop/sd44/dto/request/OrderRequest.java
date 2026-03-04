package com.beeshop.sd44.dto.request;


import java.util.List;

public class OrderRequest {
    private List<ProductDetailRequest> productDetail;
    private String note;
    private Double total;
    private String paymentMethod;

    public List<ProductDetailRequest> getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(List<ProductDetailRequest> productDetail) {
        this.productDetail = productDetail;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
