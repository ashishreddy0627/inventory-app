# Inventory App – Notes

## 1. Goal of the Project

A simple inventory backend for ONE store that:
- Stores all items
- Updates stock on sales and purchases
- Shows which items need to be ordered (reorder list)

Later this will be extended to support multiple stores.

---

## 2. High-Level Design

- Framework: Spring Boot (Java)
- Database: H2 (file-based, local)
- Layers:
  - `entity`: database models
  - `repository`: DB access
  - `service`: business logic (update stock, compute reorder)
  - `controller`: REST API for outside world
  - `dto`: request/response objects

---

## 3. File Index (Where things live)

We will keep this updated as we add code:

- `src/main/java/.../InventoryAppApplication.java`  
  Main Spring Boot class, starts the app.

(We will add more entries as we create files.)

---

## 4. Planned APIs

- `GET /api/items` – list all items
- `POST /api/items` – add a new item
- `POST /api/transactions/sale` – record a sale, reduce stock
- `POST /api/transactions/purchase` – record a purchase, increase stock
- `GET /api/reorder` – items that need to be ordered

---

## 5. Git / GitHub Notes

For each change:
- `git status`
- `git add <files>`
- `git commit -m "Short description of what and why"`
- `git push`
