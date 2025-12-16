package com.example.inventory_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // For now, we assume ONE store, but keep the field for future multi-store
    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private String name;

    private String sku;        // internal code
    private String barcode;    // for scanning
    private String unit;       // bottle, packet, gallon, etc.

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 0;

    @Column(name = "target_stock", nullable = false)
    private Integer targetStock = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Integer getTargetStock() {
        return targetStock;
    }

    public void setTargetStock(Integer targetStock) {
        this.targetStock = targetStock;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
