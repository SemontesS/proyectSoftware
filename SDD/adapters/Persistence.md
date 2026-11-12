# Persistence – NexusMarket

Adaptadores de salida (`application.adapters.persistence`). Implementan los 13 puertos de
salida del dominio: **MySQL** (JPA/Hibernate) para los datos de negocio y credenciales, y
**MongoDB** para la auditoría, igual que el proyecto del banco.

## Principios

| Decisión | Motivo |
|---|---|
| Entidades JPA separadas del dominio | El dominio no importa JPA; cada agregado tiene su `*JpaEntity` y un mapper estático. |
| Sin `@Setter` en el dominio | Los agregados se reconstruyen con fábricas `restore(...)` (`Product`, `Inventory`, `Cart`, `Order`, `Return`) o con sus constructores; así se conservan las reglas de cada entidad. |
| IDs asignados por el dominio | Las entidades exigen su ID al construirse. `IdSequenceGenerator` los emite desde la tabla `id_sequences` en una transacción corta (`REQUIRES_NEW`) con bloqueo de fila; un rollback deja huecos. |
| Concurrencia | Cada raíz tiene `@Version` (bloqueo optimista): dos reservas simultáneas del mismo inventario no pueden confirmarse ambas. |
| Movimientos de inventario | Historial de solo-adición: al guardar solo se insertan los movimientos que aún no existen, así guardar dos veces no duplica filas. |
| Transacciones | Los casos de uso son `@Transactional`; los adaptadores se unen a esa transacción. La auditoría en MongoDB queda fuera de ella. |
| Reembolso | Pertenece al agregado `Return`: sus columnas están en `product_returns` (`refund_*`, nulas si no hay reembolso). |

## Tablas de MySQL

### `carts` (`CartJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `buyer_id` | `Long` | obligatorio |
| `created_at` | `LocalDateTime` | obligatorio |
| `status` | `String` | obligatorio |
| `currency` | `String` | obligatorio |

Colección ordenada en la tabla `cart_items`.

### `user_credentials` (`CredentialsJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `user_id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `password_hash` | `String` | obligatorio |
| `token_version` | `int` | obligatorio |

### `id_sequences` (`IdSequenceJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `name` | `String` | PK |
| `next_value` | `long` | obligatorio |

### `inventories` (`InventoryJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `product_id` | `Long` | obligatorio |
| `sku` | `String` | obligatorio |
| `warehouse_id` | `Long` | obligatorio |
| `available_quantity` | `int` | obligatorio |
| `reserved_quantity` | `int` | obligatorio |
| `damaged_quantity` | `int` | obligatorio |

Movimientos en la tabla `inventory_movements` (1 a N, en cascada).

Restricción única: `(product_id, sku, warehouse_id)`.

### `inventory_movements` (`InventoryMovementJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `inventory_id` | `InventoryJpaEntity` | FK |
| `type` | `String` | obligatorio |
| `quantity` | `int` | obligatorio |
| `occurred_at` | `LocalDateTime` | obligatorio |

### `invoices` (`InvoiceJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `order_id` | `Long` | único, obligatorio |
| `invoice_number` | `String` | único, obligatorio |
| `invoice_date` | `LocalDate` | obligatorio |
| `subtotal_amount` | `BigDecimal` | obligatorio |
| `total_amount` | `BigDecimal` | obligatorio |
| `currency` | `String` | obligatorio |

### `orders` (`OrderJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `buyer_id` | `Long` | obligatorio |
| `created_at` | `LocalDateTime` | obligatorio |
| `status` | `String` | obligatorio |
| `currency` | `String` | obligatorio |

Colección ordenada en la tabla `order_lines`.

### `products` (`ProductJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `seller_id` | `Long` | obligatorio |
| `name` | `String` | obligatorio |
| `type` | `String` | obligatorio |
| `status` | `String` | obligatorio |
| `price_amount` | `BigDecimal` | obligatorio |
| `price_currency` | `String` | obligatorio |

Colección ordenada en la tabla `product_variants`.

### `product_returns` (`ReturnJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `order_id` | `Long` | obligatorio |
| `reason` | `String` | obligatorio |
| `return_date` | `LocalDate` | obligatorio |
| `status` | `String` | obligatorio |
| `refund_id` | `Long` |  |
| `refund_amount` | `BigDecimal` |  |
| `refund_currency` | `String` |  |
| `refund_date` | `LocalDate` |  |
| `refund_status` | `String` |  |

### `shipments` (`ShipmentJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `order_id` | `Long` | obligatorio |
| `warehouse_id` | `Long` | obligatorio |
| `status` | `String` | obligatorio |
| `delivery_address_type` | `AddressEmbeddable` |  |

### `users` (`UserJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `role` | `String` | obligatorio |
| `name` | `String` | obligatorio |
| `email` | `String` | único, obligatorio |
| `identity_document` | `String` | único, obligatorio |
| `status` | `String` | obligatorio |
| `commercial_status` | `String` |  |
| `primary_address_type` | `AddressEmbeddable` |  |
| `seller_status` | `String` |  |

Colección ordenada en la tabla `buyer_additional_addresses`.

### `warehouses` (`WarehouseJpaEntity`)

| Columna | Tipo Java | Notas |
|---|---|---|
| `id` | `Long` | PK |
| `version` | `Long` | versión (bloqueo optimista) |
| `seller_id` | `Long` |  |
| `type` | `String` | obligatorio |
| `status` | `String` | obligatorio |
| `location_type` | `AddressEmbeddable` |  |

## MongoDB

Colección `audit_logs` (`AuditLogDocument`): `id`, `userId`, `action` (un `Permission`),
`entityType` (nombre del enum `AuditEntityType`), `entityId`, `detail`, `timestamp`. Solo se inserta; se consulta por usuario o por entidad.

## Puertos y adaptadores

| Puerto de salida | Adaptador |
|---|---|
| `UserRepositoryPort` | `UserRepositoryAdapter` |
| `CredentialsRepositoryPort` | `CredentialsRepositoryAdapter` |
| `WarehouseRepositoryPort` | `WarehouseRepositoryAdapter` |
| `ProductRepositoryPort` | `ProductRepositoryAdapter` |
| `InventoryRepositoryPort` | `InventoryRepositoryAdapter` |
| `CartRepositoryPort` | `CartRepositoryAdapter` |
| `OrderRepositoryPort` | `OrderRepositoryAdapter` |
| `InvoiceRepositoryPort` | `InvoiceRepositoryAdapter` |
| `ShipmentRepositoryPort` | `ShipmentRepositoryAdapter` |
| `ReturnRepositoryPort` | `ReturnRepositoryAdapter` |
| `AuditLogRepositoryPort` | `AuditLogRepositoryAdapter` (MongoDB) |
| `PasswordServicePort` | `BCryptPasswordServiceAdapter` (seguridad) |
| `JwtServicePort` | `JwtServiceAdapter` (seguridad) |
