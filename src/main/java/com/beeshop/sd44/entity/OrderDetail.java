package com.beeshop.sd44.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "hoa_don_chi_tiet")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "hoa_don_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "san_pham_chi_tiet_id")
    private ProductDetail productDetail;
    @Column(name = "so_luong")
    private Integer quantity;
    @Column(name = "gia")
    private Double price;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ProductDetail getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(ProductDetail productDetail) {
        this.productDetail = productDetail;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
