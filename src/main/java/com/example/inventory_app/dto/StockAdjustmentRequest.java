package com.example.inventory_app.dto;

import com.example.inventory_app.entity.TransactionType;

public class StockAdjustmentRequest {
    
    private Long itemId;
    private Integer quantity;  // Can be positive or negative
    private TransactionType type;  // SALE, DELIVERY, or ADJUSTMENT
    private String notes;

    public StockAdjustmentRequest() {
    }

    // Getters and setters

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}