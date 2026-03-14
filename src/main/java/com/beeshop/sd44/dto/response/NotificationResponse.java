package com.beeshop.sd44.dto.response;

import java.util.Date;
import java.util.UUID;

public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private String type;
    private Integer status;
    private UUID orderId;
    private Date createdAt;

    public NotificationResponse() {}

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
