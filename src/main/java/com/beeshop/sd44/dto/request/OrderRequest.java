package com.beeshop.sd44.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OrderRequest {
    @NotEmpty(message = "Gio hang khong duoc de trong")
    @Valid
    private List<ProductDetailRequest> productDetail;

    private String note;

    @NotBlank(message = "Phuong thuc thanh toan khong duoc de trong")
    private String paymentMethod;

    private String voucherCode;
    private String address;

    @JsonProperty("isCounter")
    @JsonAlias("counter")
    private boolean counter;


    public boolean isCounter() {
        return counter;
    }

    public void setCounter(boolean counter) {
        this.counter = counter;
    }

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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
