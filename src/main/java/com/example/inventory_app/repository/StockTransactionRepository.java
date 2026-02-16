package com.example.inventory_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventory_app.entity.StockTransaction;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    
    // Find all transactions for a specific item
    List<StockTransaction> findByItemIdOrderByTransactionDateDesc(Long itemId);
    
    // Find all transactions for a specific store
    List<StockTransaction> findByStoreIdOrderByTransactionDateDesc(Long storeId);
    
    // Find all transactions for a specific store and item
    List<StockTransaction> findByStoreIdAndItemIdOrderByTransactionDateDesc(Long storeId, Long itemId);
}