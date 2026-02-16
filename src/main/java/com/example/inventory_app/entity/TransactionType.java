package com.example.inventory_app.entity;

public enum TransactionType {
    SALE,           // Stock decreased due to sale
    DELIVERY,       // Stock increased due to delivery/purchase
    ADJUSTMENT      // Manual stock adjustment (correction, damage, etc.)
}