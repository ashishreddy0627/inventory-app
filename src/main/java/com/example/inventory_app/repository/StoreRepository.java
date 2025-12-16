package com.example.inventory_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventory_app.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
