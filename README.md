# Inventory App (Backend)

A simple **multi-store inventory management backend** for gas stations or small shops.

This service lets an owner:

- Create stores
- Add items per store
- Track stock levels
- Automatically compute a **reorder list** so they know what to buy and how much

There is a companion React frontend (`inventory-ui`) that talks to this backend via REST APIs.

---

## Features

- **Multi-store support**  
  Manage multiple gas stations / stores from a single backend.

- **Store management**
  - Create stores with `name`, `location`, and `isActive` flag.
  - List all stores.

- **Item management**
  - Create items under a store.
  - Track barcode, SKU, unit (bottle, pack, etc.).
  - Maintain `currentStock`, `reorderLevel`, and `targetStock`.

- **Reorder list**
  - For each store, calculate which items are **below the reorder level**.
  - Compute how much to order:  
    `reorderQuantity = targetStock - currentStock`.

- **REST API**
  - JSON-based endpoints designed to be consumed by web or mobile clients.
  - CORS enabled so the React UI on `http://localhost:3000` can call the backend on `http://localhost:8082`.

---

## Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Build:** Maven
- **Database:** PostgreSQL
- **API Style:** REST + JSON

---

## Project Structure

High-level structure:

```text
inventory-app/
  ├── src/main/java/...   # Spring Boot application & controllers
  ├── src/main/resources/ # application.properties, DB config
  ├── pom.xml             # Maven build
  └── README.md
