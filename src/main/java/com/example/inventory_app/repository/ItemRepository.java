package com.example.inventory_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventory_app.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // For now we just use the default CRUD methods.
    // Later we can add findByStoreId(Long storeId) if needed.
}
