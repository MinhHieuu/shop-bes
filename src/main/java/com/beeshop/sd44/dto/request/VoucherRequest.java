package com.beeshop.sd44.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class VoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    private String ma;

    @NotBlank(message = "Tên voucher không được để trống")
    private String ten;

    /**
     * 0 = giảm theo %
     * 1 = giảm theo tiền
     */
    @NotNull(message = "Loại giảm không được để trống")
    private Integer loaiGiam;

    @NotNull(message = "Giá trị giảm không được để trống")
    @Min(value = 1, message = "Giá trị giảm phải lớn hơn 0")
    private Integer giaTriGiam;

    @NotNull(message = "Giá trị tối thiểu không được để trống")
    @Min(value = 0, message = "Giá trị tối thiểu không được âm")
    private Integer toiThieu;

    @Min(value = 0, message = "Giảm tối đa không được âm")
    private Integer toiDa;

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;

    private Date ngayBatDau;

    private Date ngayKetThuc;

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public Integer getLoaiGiam() {
        return loaiGiam;
    }

    public void setLoaiGiam(Integer loaiGiam) {
        this.loaiGiam = loaiGiam;
    }

    public Integer getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(Integer giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public Integer getToiThieu() {
        return toiThieu;
    }

    public void setToiThieu(Integer toiThieu) {
        this.toiThieu = toiThieu;
    }

    public Integer getToiDa() {
        return toiDa;
    }

    public void setToiDa(Integer toiDa) {
        this.toiDa = toiDa;
    }

    public Integer getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }
}