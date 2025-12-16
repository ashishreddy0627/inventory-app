package com.example.inventory_app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory_app.dto.PurchaseRequest;
import com.example.inventory_app.dto.ReorderItemResponse;
import com.example.inventory_app.dto.SaleRequest;
import com.example.inventory_app.entity.Item;
import com.example.inventory_app.entity.Store;
import com.example.inventory_app.repository.ItemRepository;
import com.example.inventory_app.repository.StoreRepository;

@RestController
@RequestMapping("/api")
public class InventoryController {

    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;

    public InventoryController(ItemRepository itemRepository,
                               StoreRepository storeRepository) {
        this.itemRepository = itemRepository;
        this.storeRepository = storeRepository;
        System.out.println(">>> InventoryController bean created");
    }

    // ========= STORE ENDPOINTS =========

    // POST /api/stores - create a new store
    @PostMapping("/stores")
    public Store createStore(@RequestBody Store store) {
        if (store.getIsActive() == null) {
            store.setIsActive(true);
        }
        return storeRepository.save(store);
    }

    // GET /api/stores - list all stores
    @GetMapping("/stores")
    public List<Store> getStores() {
        return storeRepository.findAll();
    }

    // ========= ITEM ENDPOINTS =========

    // GET /api/items - list all items (across all stores)
    @GetMapping("/items")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // GET /api/stores/{storeId}/items - list items for one store
    @GetMapping("/stores/{storeId}/items")
    public List<Item> getItemsByStore(@PathVariable Long storeId) {
        return itemRepository.findAll()
                .stream()
                .filter(i -> storeId.equals(i.getStoreId()))
                .toList();
    }

    // POST /api/items - create a new item
    @PostMapping("/items")
    public Item createItem(@RequestBody Item item) {
        // if no storeId provided, default to store 1 (single store case)
        if (item.getStoreId() == null) {
            item.setStoreId(1L);
        }
        if (item.getIsActive() == null) {
            item.setIsActive(true);
        }
        if (item.getCurrentStock() == null) {
            item.setCurrentStock(0);
        }
        if (item.getReorderLevel() == null) {
            item.setReorderLevel(0);
        }
        if (item.getTargetStock() == null) {
            item.setTargetStock(0);
        }

        return itemRepository.save(item);
    }

    // ========= TRANSACTION ENDPOINTS =========

    // POST /api/transactions/sale - record a sale and reduce stock
    @PostMapping("/transactions/sale")
    public ResponseEntity<?> recordSale(@RequestBody SaleRequest request) {
        if (request.getItemId() == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("itemId and positive quantity are required");
        }

        Optional<Item> optItem = itemRepository.findById(request.getItemId());
        if (optItem.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found: " + request.getItemId());
        }

        Item item = optItem.get();

        if (item.getCurrentStock() < request.getQuantity()) {
            return ResponseEntity.badRequest().body("Not enough stock for item: " + item.getName());
        }

        item.setCurrentStock(item.getCurrentStock() - request.getQuantity());
        Item updated = itemRepository.save(item);

        return ResponseEntity.ok(updated);
    }

    // POST /api/transactions/purchase - record a purchase and increase stock
    @PostMapping("/transactions/purchase")
    public ResponseEntity<?> recordPurchase(@RequestBody PurchaseRequest request) {
        if (request.getItemId() == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("itemId and positive quantity are required");
        }

        Optional<Item> optItem = itemRepository.findById(request.getItemId());
        if (optItem.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found: " + request.getItemId());
        }

        Item item = optItem.get();

        item.setCurrentStock(item.getCurrentStock() + request.getQuantity());
        Item updated = itemRepository.save(item);

        return ResponseEntity.ok(updated);
    }

    // ========= REORDER ENDPOINTS =========

    // GET /api/reorder-list - global reorder list (all stores)
    @GetMapping("/reorder-list")
    public List<ReorderItemResponse> getReorderList() {
        List<Item> items = itemRepository.findAll();
        return buildReorderList(items);
    }

    // GET /api/stores/{storeId}/reorder-list - reorder list for one store
    @GetMapping("/stores/{storeId}/reorder-list")
    public List<ReorderItemResponse> getReorderListForStore(@PathVariable Long storeId) {
        List<Item> items = itemRepository.findAll()
                .stream()
                .filter(i -> storeId.equals(i.getStoreId()))
                .toList();

        return buildReorderList(items);
    }

    // Helper: common reorder list logic
    private List<ReorderItemResponse> buildReorderList(List<Item> items) {
        List<ReorderItemResponse> result = new ArrayList<>();

        for (Item item : items) {
            Integer current = item.getCurrentStock();
            Integer reorderLevel = item.getReorderLevel();
            Integer target = item.getTargetStock();

            if (current == null || reorderLevel == null || target == null) {
                continue;
            }

            if (current <= reorderLevel) {
                int qty = target - current;
                if (qty <= 0) {
                    continue;
                }

                ReorderItemResponse row = new ReorderItemResponse();
                row.setItemId(item.getId());
                row.setName(item.getName());
                row.setCurrentStock(current);
                row.setReorderLevel(reorderLevel);
                row.setTargetStock(target);
                row.setReorderQuantity(qty);

                result.add(row);
            }
        }

        return result;
    }
}
