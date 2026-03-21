package com.beeshop.sd44.dto.request;

import jakarta.validation.constraints.*;

import java.util.Date;

public class VoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    private String ma;

    @NotBlank(message = "Tên voucher không được để trống")
    private String ten;

    @NotNull(message = "Loại giảm không được để trống")
    private Integer loaiGiam;

    @NotNull(message = "Giá trị giảm không được để trống")
    private Integer giaTriGiam;

    @NotNull(message = "Giá trị tối thiểu không được để trống")
    private Integer toiThieu;

    // nullable — nếu có thì phải >= toiThieu (kiểm tra bằng @ToiDaHopLe)
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

    public Integer getToiDa() {
        return toiDa;
    }

    public void setToiDa(Integer toiDa) {
        this.toiDa = toiDa;
    }

    public Integer getToiThieu() {
        return toiThieu;
    }

    public void setToiThieu(Integer toiThieu) {
        this.toiThieu = toiThieu;
    }

    public Integer getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(Integer giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
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
