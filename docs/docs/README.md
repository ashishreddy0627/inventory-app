# Inventory App – Gas Station / Grocery Store

This project is a simple inventory management system for a gas station or grocery store.
It tracks:

- Stores (locations)
- Items under each store
- Current stock, reorder levels, and target stock
- Sales and purchases (stock goes up/down)
- Barcode lookup using either a scanner or the laptop camera

Phase 1 is designed for a single store. The structure already supports multiple stores.

---

## Tech Stack

**Backend**

- Java 17
- Spring Boot 4
- Spring Web
- Spring Data JPA
- PostgreSQL (dev) or H2 (early version)
- Maven

**Frontend**

- React (functional components + hooks)
- Simple HTML/CSS (no design library)
- `html5-qrcode` for camera-based barcode scanning

The React app is built and served as static files from Spring Boot
(`src/main/resources/static`).

---

## Backend Structure

**Main application**

- `src/main/java/com/example/inventory_app/InventoryAppApplication.java`  
  Bootstraps the Spring Boot application.

**Entities**

- `Store` – `src/main/java/com/example/inventory_app/entity/Store.java`
  - `id` (Long)
  - `name` (String)
  - `location` (String)
  - `isActive` (Boolean)

- `Item` – `src/main/java/com/example/inventory_app/entity/Item.java`
  - `id` (Long)
  - `storeId` (Long) – the store this item belongs to
  - `name` (String)
  - `sku` (String)
  - `barcode` (String)
  - `unit` (String), e.g. "bottle", "pack"
  - `currentStock` (Integer)
  - `reorderLevel` (Integer)
  - `targetStock` (Integer)
  - `isActive` (Boolean)

**Repositories**

- `StoreRepository` – `src/main/java/com/example/inventory_app/repository/StoreRepository.java`
  - Extends `JpaRepository<Store, Long>`

- `ItemRepository` – `src/main/java/com/example/inventory_app/repository/ItemRepository.java`
  - `List<Item> findByStoreId(Long storeId)`
  - `Optional<Item> findByBarcode(String barcode)`

**DTOs**

- `SaleRequest` – `src/main/java/com/example/inventory_app/dto/SaleRequest.java`
  - `itemId`, `quantity`

- `PurchaseRequest` – `src/main/java/com/example/inventory_app/dto/PurchaseRequest.java`
  - `itemId`, `quantity`

- `ReorderItemResponse` – `src/main/java/com/example/inventory_app/dto/ReorderItemResponse.java`
  - Minimal fields needed for reorder list:
    - `itemId`, `name`, `currentStock`, `reorderLevel`, `targetStock`, `reorderQuantity`

**Controller**

- `InventoryController` – `src/main/java/com/example/inventory_app/controller/InventoryController.java`

  Key endpoints:

  - **Stores**
    - `GET /api/stores` – list all stores
    - `POST /api/stores` – create a store

  - **Items**
    - `GET /api/items` – list all items (across stores)
    - `GET /api/stores/{storeId}/items` – list items for one store
    - `POST /api/items` – create an item (defaults to store 1 if no storeId is set)

  - **Transactions**
    - `POST /api/transactions/sale`  
      Body: `{ "itemId": 1, "quantity": 3 }`  
      Decreases `currentStock` if enough stock is available.

    - `POST /api/transactions/purchase`  
      Body: `{ "itemId": 1, "quantity": 10 }`  
      Increases `currentStock`.

  - **Reorder**
    - `GET /api/reorder-list`  
      Calculates reorder list across all stores.

    - `GET /api/stores/{storeId}/reorder-list`  
      Reorder list for a single store.

  - **Barcode lookup**
    - `GET /api/stores/{storeId}/items/by-barcode/{barcode}`  
      Returns the item for a given barcode.
      Currently uses `ItemRepository.findByBarcode(barcode)` (barcodes treated as unique).

**Database config**

- `src/main/resources/application.properties`  
  Contains:
  - DB URL, username, password (PostgreSQL)
  - JPA settings (DDL auto, show SQL)
  - Server port (e.g. `server.port=8082`)
  - CORS configuration if needed

---

## Frontend Structure (React)

Located in `inventory-ui/` (source), built into `inventory-app/src/main/resources/static` for deployment.

**Main file**

- `inventory-ui/src/App.js`

Key UI blocks:

- **Add Store**
  - Form to create a new store via `POST /api/stores`.

- **Stores List & Selector**
  - Table of existing stores.
  - Dropdown to select the active store – the selection drives items, inventory, reorder list, and barcode scan.

- **Add Item to Selected Store**
  - Form fields: name, SKU, barcode, unit, current stock, reorder level, target stock.
  - Sends `POST /api/items` with `storeId` from the selected store.

- **Inventory Table**
  - Calls `GET /api/stores/{storeId}/items`.
  - Shows items only for the selected store.

- **Reorder List**
  - Calls `GET /api/stores/{storeId}/reorder-list`.
  - Shows items at/below reorder level and how many units to order.

- **Barcode Scan / Lookup**
  - Text input: manual typing or hardware scanner input.
  - Button: `Lookup` → calls `GET /api/stores/{storeId}/items/by-barcode/{barcode}`.
  - Shows:
    - item name, sku, current stock, reorder level, target stock.

- **Sell / Add Stock (for scanned item)**
  - Quantity input.
  - `Sell` → calls `POST /api/transactions/sale`.
  - `Add Stock` → calls `POST /api/transactions/purchase`.
  - Refreshes inventory and reorder list after each transaction.

- **Camera Scan (Mac)**
  - Uses `html5-qrcode`.
  - Button: `Scan with Camera`
  - Opens camera on `http://localhost:8082` (Mac).
  - Decoded barcode fills the input and triggers lookup.
  - Note: phone camera will require HTTPS or localhost to work due to browser security rules.

---

## Dev Commands

**Backend only (API + built UI)**

```bash
cd inventory-app
mvn -DskipTests spring-boot:run
