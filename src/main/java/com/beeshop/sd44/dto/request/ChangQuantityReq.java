package com.beeshop.sd44.dto.request;

import java.util.UUID;

public class ChangQuantityReq {
    private UUID id;
    private int quantity;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
