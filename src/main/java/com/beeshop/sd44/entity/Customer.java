package com.beeshop.sd44.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "khach_hang")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String ten;
    private String sdt;
    private Date ngayTao;
    private String diaChi;
    @OneToMany(mappedBy = "customer")
    private List<Order> listOrder;
    @OneToOne
    @JoinColumn(name = "nguoi_dung_id")
    private User user;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public List<Order> getListOrder() { return listOrder; }
    public void setListOrder(List<Order> listOrder) { this.listOrder = listOrder; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
