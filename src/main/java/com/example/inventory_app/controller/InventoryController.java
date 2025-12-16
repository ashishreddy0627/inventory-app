package com.example.inventory_app.controller;

import com.example.inventory_app.dto.PurchaseRequest;
import com.example.inventory_app.dto.ReorderItemResponse;
import com.example.inventory_app.dto.SaleRequest;
import com.example.inventory_app.entity.Item;
import com.example.inventory_app.repository.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class InventoryController {

    private final ItemRepository itemRepository;

    public InventoryController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        System.out.println(">>> InventoryController bean created");
    }

    // GET /api/items - list all items
    @GetMapping("/items")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // POST /api/items - create a new item
    @PostMapping("/items")
    public Item createItem(@RequestBody Item item) {
        if (item.getStoreId() == null) {
            item.setStoreId(1L); // single store for now
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

    // GET /api/reorder-list - items that need restocking and how many to order
    @GetMapping("/reorder-list")
    public List<ReorderItemResponse> getReorderList() {
        List<Item> items = itemRepository.findAll();
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
