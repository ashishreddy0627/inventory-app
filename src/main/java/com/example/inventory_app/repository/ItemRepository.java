package com.example.inventory_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventory_app.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // For now we just use the default CRUD methods.
    // Later we can add findByStoreId(Long storeId) if needed.
List<Item> findByStoreId(Long storeId);
    // NEW: find a single item by barcode within a store
Optional<Item> findByBarcode(String barcode);

}
