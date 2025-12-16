# Inventory App – Design Notes

## Goal

Inventory app for a single gas station / grocery store:
- Track all items in the store
- Decrease stock when an item is sold
- Increase stock when new stock arrives
- When it’s time to order, show:
  - which items are low
  - how many units to order for each item

Later I will extend this to handle multiple stores.

---

## Tech Stack

- Java 17
- Spring Boot (REST API)
- Spring Data JPA
- H2 in-memory DB (for now)
- JSON APIs (tested with curl / Postman)

App URL (local): `http://localhost:8082`

---

## Data Model

### Entity: `Item`  
Package: `com.example.inventory_app.entity`

Represents one product in the store.

Fields:
- `id` (Long) – primary key
- `name` (String) – item name (e.g. "Coke 500ml")
- `sku` (String) – internal code
- `barcode` (String) – barcode on the physical product
- `unit` (String) – e.g. "bottle", "can", "piece"
- `currentStock` (Integer) – how many units I currently have
- `reorderLevel` (Integer) – when stock is at or below this number, item should be reordered
- `targetStock` (Integer) – what stock I want to have after reordering
- `isActive` (Boolean) – whether this item is active
- `storeId` (Long) – for now always 1 (single store), but ready for multi-store later

### Repositories

- `ItemRepository`  
  Package: `com.example.inventory_app.repository`  
  - Extends `JpaRepository<Item, Long>`  
  - Used by the controller to read and write item data.

### DTOs (in `com.example.inventory_app.dto`)

- `SaleRequest`
  - Fields: `itemId`, `quantity`, `notes`
  - Used when recording a sale (reduce stock).

- `PurchaseRequest`
  - Fields: `itemId`, `quantity`, `notes`
  - Used when recording a purchase / new stock (increase stock).

- `ReorderItemResponse`
  - Fields: `itemId`, `name`, `currentStock`, `reorderLevel`, `targetStock`, `reorderQuantity`
  - Used to return rows from `/api/reorder-list` so I know exactly how much to order.

---

## REST API (InventoryController)

Package: `com.example.inventory_app.controller`  
Class: `InventoryController`

Base path for all endpoints: `/api`

### 1. `GET /api/items`

- Returns a list of all items with current stock.  
- Use this to see what’s currently in the store.

Example:

```bash
curl http://localhost:8082/api/items
