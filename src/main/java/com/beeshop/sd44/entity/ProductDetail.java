package com.beeshop.sd44.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "san_pham_chi_tiet")
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "mo_ta")
    private String description;
    @Column(name = "so_luong")
    private Integer quantity;
    @Column(name = "gia_nhap")
    private Double costPrice;
    @Column(name = "gia_ban")
    private Double salePrice;
    private boolean deleteFlag;
    @ManyToOne
    @JoinColumn(name = "san_pham_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;
    @ManyToOne
    @JoinColumn(name = "mau_sac_id")
    private Color color;
    @OneToMany(mappedBy = "productDetail")
    private List<Image> images;
    @OneToMany(mappedBy = "productDetail")
    private List<CartDetail> listCartDetail;
    @OneToMany(mappedBy = "productDetail")
    private List<OrderDetail> detailList;
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> imageList) {
        this.images = imageList;
    }

    public List<CartDetail> getListCartDetail() {
        return listCartDetail;
    }

    public void setListCartDetail(List<CartDetail> listCartDetail) {
        this.listCartDetail = listCartDetail;
    }
}
