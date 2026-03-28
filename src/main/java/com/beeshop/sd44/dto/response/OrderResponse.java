package com.beeshop.sd44.dto.response;

import java.util.Date;
import java.util.List;

public class OrderResponse {
    private String id;
    private String code;
    private String note;
    private Date createdAt;
    private Date paymentDate;
    private String paymentMethod;
    private Integer shippingFee;
    private Double subTotal;       // tổng tiền hàng (trước giảm)
    private Double discount;       // số tiền giảm
    private Double total;          // tổng thanh toán cuối cùng
    private Integer type;
    private Integer status;
    private Integer paymentStatus;
    private String voucherCode;
    private UserResponse userResponse;
    private UserResponse customerResponse;
    private List<ProductDetailResponse> productDetailResponses;
    private String address;
    // private Integer quantity;
    // private Integer price;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Integer getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(Integer shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UserResponse getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(UserResponse userResponse) {
        this.userResponse = userResponse;
    }

    public UserResponse getCustomerResponse() {
        return customerResponse;
    }

    public void setCustomerResponse(UserResponse customerResponse) {
        this.customerResponse = customerResponse;
    }

    public List<ProductDetailResponse> getProductDetailResponses() {
        return productDetailResponses;
    }

    public void setProductDetailResponses(List<ProductDetailResponse> productDetailResponses) {
        this.productDetailResponses = productDetailResponses;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
