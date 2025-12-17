# Inventory App – Design & Code Map

This document is for **future me** so I can quickly remember:

- What this app does
- How the data is structured
- Where important code lives
- How to run backend + UI
- How to add new features

---

## 1. What this app does (current scope)

This app is a **simple inventory system** for a **gas station / grocery store**.

For each store, I can:

- Keep a list of items (e.g., Coke 500ml, Chips, etc.)
- Track **current stock**, **reorder level**, and **target stock**
- Record **sales** (stock goes down)
- Record **purchases** (stock goes up)
- Get a **reorder list** that tells me:
  - Which items are low
  - How many units I should order to reach the target stock

Right now it supports:

- ✅ Multiple stores (via `Store` entity)
- ✅ Items tied to a store (via `storeId` field)
- ✅ REST API (Spring Boot)
- ✅ Simple UI (React) to view stores, inventory, and reorder list
- ✅ PostgreSQL database for persistent data

---

## 2. Tech stack

### Backend (API)

- **Language**: Java
- **Framework**: Spring Boot
- **Build tool**: Maven
- **DB**: PostgreSQL
- **Port**: `8082`

Main goal: expose REST APIs that can be used by UI or other systems.

### Frontend (UI)

- **Framework**: React
- **Tooling**: create-react-app
- **Port**: `3000`

Main goal: give a simple dashboard for store owners to see stock and reorder list.

---

## 3. Project structure – important files

### 3.1 Backend – package `com.example.inventory_app`

**Main app class:**

- `src/main/java/com/example/inventory_app/InventoryAppApplication.java`  
  - Standard Spring Boot main class.
  - Starts the application and web server.

**Global CORS config:**

- `src/main/java/com/example/inventory_app/GlobalCorsConfig.java`
  - Enables CORS so the React app at `http://localhost:3000` can call the backend.
  - Applies to `/api/**` endpoints.

**Entities (database tables):**

- `src/main/java/com/example/inventory_app/entity/Store.java`
  - Represents a store (gas station, grocery, etc.)
  - Fields (main ones):
    - `Long id`
    - `String name`
    - `String location`
    - `Boolean isActive`

- `src/main/java/com/example/inventory_app/entity/Item.java`
  - Represents an inventory item.
  - Fields (main ones):
    - `Long id`
    - `String name`
    - `String sku`
    - `String barcode`
    - `String unit`
    - `Integer currentStock`
    - `Integer reorderLevel`
    - `Integer targetStock`
    - `Boolean isActive`
    - `Long storeId` → links the item to a specific `Store`

**Repositories (DB access):**

- `src/main/java/com/example/inventory_app/repository/StoreRepository.java`
  - Extends `JpaRepository<Store, Long>`
  - Used for CRUD operations on `Store`.

- `src/main/java/com/example/inventory_app/repository/ItemRepository.java`
  - Extends `JpaRepository<Item, Long>`
  - Used for CRUD operations on `Item`.

**DTOs (request/response models):**

- `src/main/java/com/example/inventory_app/dto/SaleRequest.java`
  - Used for `/transactions/sale` endpoint.
  - Fields:
    - `Long itemId`
    - `Integer quantity`

- `src/main/java/com/example/inventory_app/dto/PurchaseRequest.java`
  - Used for `/transactions/purchase` endpoint.
  - Fields:
    - `Long itemId`
    - `Integer quantity`

- `src/main/java/com/example/inventory_app/dto/ReorderItemResponse.java`
  - Used in `/reorder-list` endpoints.
  - Fields:
    - `Long itemId`
    - `String name`
    - `Integer currentStock`
    - `Integer reorderLevel`
    - `Integer targetStock`
    - `Integer reorderQuantity` (how many units to order)

**Controller (REST API):**

- `src/main/java/com/example/inventory_app/controller/InventoryController.java`

  This class exposes all main API endpoints.  
  It is annotated with:

  ```java
  @CrossOrigin(origins = "*")
  @RestController
  @RequestMapping("/api")
  public class InventoryController { ... }
