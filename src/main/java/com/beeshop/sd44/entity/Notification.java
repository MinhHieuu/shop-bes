package com.beeshop.sd44.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "thong_bao")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tieu_de")
    private String title;

    @Column(name = "noi_dung", length = 500)
    private String message;

    /**
     * Loại thông báo.
     * Hiện chỉ có: "NEW_ORDER"
     */
    @Column(name = "loai")
    private String type;

    /**
     * Trạng thái: 0 = chưa xử lý, 1 = đã xử lý
     */
    @Column(name = "trang_thai")
    private Integer status;

    /**
     * ID hóa đơn liên quan
     */
    @Column(name = "hoa_don_id")
    private UUID orderId;

    @Column(name = "ngay_tao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Notification() {}

    // ───── Getters & Setters ─────

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
