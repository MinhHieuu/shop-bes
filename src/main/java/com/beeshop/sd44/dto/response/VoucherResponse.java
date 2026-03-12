package com.beeshop.sd44.dto.response;

import java.util.Date;
import java.util.UUID;

public class VoucherResponse {
    private UUID id;
    private String ma;
    private String ten;
    private Integer loaiGiam;
    private Integer toiDa;
    private Integer trangThai;
    private Date ngayBatDau;
    private Date ngayKetThuc;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
