# Output Ports – NexusMarket

Puertos de salida del dominio (`application.domain.ports.out`). Los adaptadores
de persistencia los implementarán. Cada repositorio genera sus identificadores
con `nextId()` porque las entidades exigen un ID desde su construcción.

| Puerto | Métodos |
|---|---|
| `UserRepositoryPort` | `nextId`, `save`, `findById`, `findByEmail`, `findBuyerById`, `findSellerById`, `existsByEmail`, `existsByIdentityDocument`, `countByRole` |
| `WarehouseRepositoryPort` | `nextId`, `save`, `findById`, `findBySellerId` |
| `ProductRepositoryPort` | `nextId`, `save`, `findById`, `findAll`, `findByStatus`, `countByStatus` |
| `InventoryRepositoryPort` | `nextId`, `save`, `findById`, `findByProductId`, `findByProductAndSku`, `findByProductSkuAndWarehouse`, `findByWarehouseId` |
| `CartRepositoryPort` | `nextId`, `save`, `findActiveByBuyerId` |
| `OrderRepositoryPort` | `nextId`, `save`, `findById`, `findByBuyerId`, `countByStatus`, `salesTotalsByCurrency` |
| `InvoiceRepositoryPort` | `nextId`, `nextInvoiceNumber`, `save`, `findByOrderId` |
| `ShipmentRepositoryPort` | `nextId`, `save`, `findById`, `findByOrderId` |
| `ReturnRepositoryPort` | `nextId`, `nextRefundId`, `save`, `findById`, `findByOrderId` |
| `CredentialsRepositoryPort` | `save`, `findByUserId` |
| `AuditLogRepositoryPort` | `save`, `findByUserId`, `findByEntity` (MongoDB) |
| `PasswordServicePort` | `encrypt`, `matches` (BCrypt) |
| `JwtServicePort` | `generateToken(user, tokenVersion)`, `parseToken` |

El reembolso (`Refund`) pertenece al aggregate `Return`: se persiste con él.
Además, los servicios que generan fechas reciben un `java.time.Clock`
(bean definido en `infrastructure.config.ClockConfig`).
