package com.example.inventory_app.dto;

public class ReorderItemResponse {

    private Long itemId;
    private String name;
    private Integer currentStock;
    private Integer targetStock;
    private Integer reorderLevel;
    private Integer reorderQuantity; // how many to order

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getTargetStock() {
        return targetStock;
    }

    public void setTargetStock(Integer targetStock) {
        this.targetStock = targetStock;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Integer getReorderQuantity() {
        return reorderQuantity;
    }

    public void setReorderQuantity(Integer reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }
}
