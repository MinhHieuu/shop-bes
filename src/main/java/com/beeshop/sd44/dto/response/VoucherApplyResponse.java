package com.beeshop.sd44.dto.response;

public class VoucherApplyResponse {
    private String ma;
    private String ten;
    private Integer loaiGiam;       // 0: %, 1: tiền
    private Integer giaTriGiam;
    private Double discountAmount;  // số tiền thực tế được giảm
    private Double subTotal;        // tổng tiền hàng trước giảm
    private Double totalAfterDiscount; // tổng tiền sau giảm (chưa + ship)

    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public Integer getLoaiGiam() { return loaiGiam; }
    public void setLoaiGiam(Integer loaiGiam) { this.loaiGiam = loaiGiam; }

    public Integer getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(Integer giaTriGiam) { this.giaTriGiam = giaTriGiam; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getSubTotal() { return subTotal; }
    public void setSubTotal(Double subTotal) { this.subTotal = subTotal; }

    public Double getTotalAfterDiscount() { return totalAfterDiscount; }
    public void setTotalAfterDiscount(Double totalAfterDiscount) { this.totalAfterDiscount = totalAfterDiscount; }
}

