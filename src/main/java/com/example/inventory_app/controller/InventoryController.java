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

    public InventoryController(ItemRepository itemRepository, StoreRepository storeRepository) {
        this.itemRepository = itemRepository;
        this.storeRepository = storeRepository;
    }

    // ---------- STORES ----------

    @GetMapping("/stores")
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @PostMapping("/stores")
    public Store createStore(@RequestBody Store store) {
        if (store.getIsActive() == null) {
            store.setIsActive(true);
        }
        return storeRepository.save(store);
    }

    // ---------- ITEMS ----------

    @GetMapping("/items")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/stores/{storeId}/items")
    public List<Item> getItemsByStore(@PathVariable Long storeId) {
        return itemRepository.findByStoreId(storeId);
    }

    @PostMapping("/items")
    public Item createItem(@RequestBody Item item) {
        if (item.getStoreId() == null) {
            item.setStoreId(1L); // default to store 1 if not provided
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

    // ---------- TRANSACTIONS ----------

    @PostMapping("/transactions/sale")
    public ResponseEntity<?> recordSale(@RequestBody SaleRequest request) {
        if (request.getItemId() == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("itemId and positive quantity are required");
        }

        Optional<Item> itemOpt = itemRepository.findById(request.getItemId());
        if (itemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found");
        }

        Item item = itemOpt.get();
        if (item.getCurrentStock() == null) {
            item.setCurrentStock(0);
        }

        if (item.getCurrentStock() < request.getQuantity()) {
            return ResponseEntity.badRequest().body("Not enough stock");
        }

        item.setCurrentStock(item.getCurrentStock() - request.getQuantity());
        itemRepository.save(item);

        return ResponseEntity.ok(item);
    }

    @PostMapping("/transactions/purchase")
    public ResponseEntity<?> recordPurchase(@RequestBody PurchaseRequest request) {
        if (request.getItemId() == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("itemId and positive quantity are required");
        }

        Optional<Item> itemOpt = itemRepository.findById(request.getItemId());
        if (itemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found");
        }

        Item item = itemOpt.get();
        if (item.getCurrentStock() == null) {
            item.setCurrentStock(0);
        }

        item.setCurrentStock(item.getCurrentStock() + request.getQuantity());
        itemRepository.save(item);

        return ResponseEntity.ok(item);
    }

    // ---------- REORDER LIST ----------

    @GetMapping("/reorder-list")
    public List<ReorderItemResponse> getReorderListForAllStores() {
        List<Item> items = itemRepository.findAll();
        return buildReorderList(items);
    }

    @GetMapping("/stores/{storeId}/reorder-list")
    public List<ReorderItemResponse> getReorderListForStore(@PathVariable Long storeId) {
        List<Item> items = itemRepository.findByStoreId(storeId);
        return buildReorderList(items);
    }

    private List<ReorderItemResponse> buildReorderList(List<Item> items) {
        List<ReorderItemResponse> result = new ArrayList<>();

        for (Item item : items) {
            Integer current = item.getCurrentStock() != null ? item.getCurrentStock() : 0;
            Integer reorderLevel = item.getReorderLevel() != null ? item.getReorderLevel() : 0;
            Integer target = item.getTargetStock() != null ? item.getTargetStock() : 0;

            if (current <= reorderLevel && target > current) {
                ReorderItemResponse dto = new ReorderItemResponse();
                dto.setItemId(item.getId());
                dto.setName(item.getName());
                dto.setCurrentStock(current);
                dto.setReorderLevel(reorderLevel);
                dto.setTargetStock(target);
                dto.setReorderQuantity(target - current);
                result.add(dto);
            }
        }

        return result;
    }

    // ---------- BARCODE LOOKUP ----------

    @GetMapping("/stores/{storeId}/items/by-barcode/{barcode}")
    public ResponseEntity<Item> getItemByBarcode(
            @PathVariable Long storeId,
            @PathVariable String barcode) {

        Optional<Item> itemOpt = itemRepository.findByBarcode(barcode);
        return itemOpt.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
