package com.beeshop.sd44.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "san_pham")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotBlank(message = "ten khong duoc de trong")
    @Column(name = "ten")
    private String name;
    @Column(name = "ngay_tao")
    private Date createdAt;
    @Column(name = "ngay_sua")
    private Date updatedAt;
    @Column(name = "hinh_anh")
    private String image;
    @NotNull(message = "phai chon trang thai")
    @Column(name = "trang_thai")
    private Integer status;
    @NotNull(message = "phai chon chat lieu")
    @ManyToOne
    @JoinColumn(name = "chat_lieu_id")
    private Marterial marterial;
    @NotNull(message = "phai chon thuong hieu")
    @ManyToOne
    @JoinColumn(name = "thuong_hieu_id")
    private Brand brand;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> list = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public Marterial getMarterial() {
        return marterial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setMarterial(Marterial marterial) {
        this.marterial = marterial;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public List<ProductDetail> getList() {
        return list;
    }

    public void setList(List<ProductDetail> list) {
        this.list = list;
    }
}
