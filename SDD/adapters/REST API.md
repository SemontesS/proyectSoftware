# REST API – NexusMarket

Adaptador de entrada REST (`application.adapters.rest`). Hay **un controlador por rol**,
igual que en el proyecto del banco; cada uno depende únicamente de su puerto de entrada
(`domain/ports/in`). El usuario autenticado llega a cada método con
`@AuthenticationPrincipal(expression = "user")`; el filtro JWT lo carga en cada petición
con `LoadAuthenticatedUserService`.

**Capas:** `controllers` → `mappers` (DTO ↔ dominio) → `dtos` (`requests`, `responses`,
`shared`) y `exception/GlobalExceptionHandler`. Los DTO son `record` con validación de
Jakarta; los mappers son estáticos y construyen los value objects del dominio
(`Email`, `RawPassword`, `Sku`, `Money`...), de modo que el formato de un valor lo
valida el dominio y no el controlador.

## Convenciones

| Aspecto | Regla |
|---|---|
| Prefijo | `/api/v1/<rol>` (`auth`, `buyer`, `seller`, `logistics`, `supervisor`, `admin`) |
| Autenticación | `Authorization: Bearer <token>` (JWT) salvo `login` y el registro de compradores |
| Autorización | Por prefijo de ruta (rol) y, dentro del dominio, por permiso y propiedad del recurso |
| Enumerados | Se envían como texto, sin distinguir mayúsculas (`"physical"` = `PHYSICAL`) |
| Dinero | `{ "amount": 50000, "currency": "COP" }` |
| Creación | `201 Created` con el recurso; acciones sin cuerpo de respuesta devuelven `204` |

## Errores

Todas las respuestas de error usan `ErrorResponseDTO`
(`timestamp`, `status`, `error`, `message`, `path`, `requestId`, `fieldErrors`).

| HTTP | Causa | Excepción |
|---|---|---|
| 400 | Cuerpo mal formado, validación fallida o valor inválido (correo, contraseña, enumerado) | `MethodArgumentNotValidException`, `IllegalArgumentException` |
| 401 | Credenciales o token inválidos | `InvalidCredentialsException` |
| 403 | Rol sin permiso, recurso ajeno o cuenta inactiva | `UnauthorizedOperationException` |
| 404 | La entidad no existe (o no es visible para el usuario) | `EntityNotFoundException` |
| 409 | Duplicado o regla de negocio violada | `DuplicateEntityException`, `BusinessRuleViolationException` |
| 500 | Error inesperado; la causa solo se registra en el log | `Exception` |

Un login con correo o contraseña mal formados responde 401, igual que cualquier credencial
incorrecta, para no revelar la política de contraseñas.

## Acceso público y sesión – `/api/v1/auth` (público, salvo logout) – 3 endpoints

| Método | Ruta | Cuerpo | Respuesta | HTTP |
|---|---|---|---|---|
| POST | `/api/v1/auth/login` | `LoginRequestDTO` | `LoginResponseDTO` | 200 |
| POST | `/api/v1/auth/logout` | — | — | 204 |
| POST | `/api/v1/auth/register/buyer` | `RegisterBuyerRequestDTO` | `BuyerResponseDTO` | 201 |

## Comprador – `/api/v1/buyer` (BUYER) – 21 endpoints

| Método | Ruta | Cuerpo | Respuesta | HTTP |
|---|---|---|---|---|
| GET | `/api/v1/buyer/profile` | — | `BuyerResponseDTO` | 200 |
| PUT | `/api/v1/buyer/profile/addresses` | `UpdateBuyerAddressesRequestDTO` | `BuyerResponseDTO` | 200 |
| GET | `/api/v1/buyer/account` | — | `UserResponseDTO` | 200 |
| PUT | `/api/v1/buyer/account` | `UpdateUserRequestDTO` | `UserResponseDTO` | 200 |
| PUT | `/api/v1/buyer/password` | `ChangePasswordRequestDTO` | — | 204 |
| GET | `/api/v1/buyer/products` | — | `Lista de ProductResponseDTO` | 200 |
| GET | `/api/v1/buyer/products/{productId}` | — | `ProductResponseDTO` | 200 |
| GET | `/api/v1/buyer/cart` | — | `CartResponseDTO` | 200 |
| POST | `/api/v1/buyer/cart/items` | `AddCartItemRequestDTO` | `CartResponseDTO` | 201 |
| PUT | `/api/v1/buyer/cart/items/{productId}` | `UpdateCartItemRequestDTO` | `CartResponseDTO` | 200 |
| DELETE | `/api/v1/buyer/cart/items/{productId}` | — | `CartResponseDTO` | 200 |
| POST | `/api/v1/buyer/orders` | — | `OrderResponseDTO` | 201 |
| GET | `/api/v1/buyer/orders` | — | `Lista de OrderResponseDTO` | 200 |
| GET | `/api/v1/buyer/orders/{orderId}` | — | `OrderResponseDTO` | 200 |
| POST | `/api/v1/buyer/orders/{orderId}/payment` | — | `OrderResponseDTO` | 200 |
| POST | `/api/v1/buyer/orders/{orderId}/cancel` | — | `OrderResponseDTO` | 200 |
| POST | `/api/v1/buyer/orders/{orderId}/invoice` | — | `InvoiceResponseDTO` | 201 |
| GET | `/api/v1/buyer/orders/{orderId}/invoice` | — | `InvoiceResponseDTO` | 200 |
| GET | `/api/v1/buyer/orders/{orderId}/shipments` | — | `Lista de ShipmentResponseDTO` | 200 |
| GET | `/api/v1/buyer/shipments/{shipmentId}` | — | `ShipmentResponseDTO` | 200 |
| POST | `/api/v1/buyer/orders/{orderId}/returns` | `RequestReturnRequestDTO` | `ReturnResponseDTO` | 201 |

## Vendedor – `/api/v1/seller` (SELLER) – 17 endpoints

| Método | Ruta | Cuerpo | Respuesta | HTTP |
|---|---|---|---|---|
| GET | `/api/v1/seller/profile` | — | `SellerResponseDTO` | 200 |
| GET | `/api/v1/seller/account` | — | `UserResponseDTO` | 200 |
| PUT | `/api/v1/seller/account` | `UpdateUserRequestDTO` | `UserResponseDTO` | 200 |
| PUT | `/api/v1/seller/password` | `ChangePasswordRequestDTO` | — | 204 |
| GET | `/api/v1/seller/warehouses/{warehouseId}` | — | `WarehouseResponseDTO` | 200 |
| POST | `/api/v1/seller/products` | `RegisterProductRequestDTO` | `ProductResponseDTO` | 201 |
| PUT | `/api/v1/seller/products/{productId}` | `UpdateProductRequestDTO` | `ProductResponseDTO` | 200 |
| POST | `/api/v1/seller/products/{productId}/publish` | — | `ProductResponseDTO` | 200 |
| POST | `/api/v1/seller/products/{productId}/suspend` | — | `ProductResponseDTO` | 200 |
| POST | `/api/v1/seller/products/{productId}/discontinue` | — | `ProductResponseDTO` | 200 |
| GET | `/api/v1/seller/products` | — | `Lista de ProductResponseDTO` | 200 |
| GET | `/api/v1/seller/products/{productId}` | — | `ProductResponseDTO` | 200 |
| POST | `/api/v1/seller/inventory` | `RegisterInitialInventoryRequestDTO` | `InventoryResponseDTO` | 201 |
| POST | `/api/v1/seller/inventory/{inventoryId}/entries` | `QuantityRequestDTO` | `InventoryResponseDTO` | 200 |
| POST | `/api/v1/seller/inventory/{inventoryId}/adjustments` | `AdjustmentRequestDTO` | `InventoryResponseDTO` | 200 |
| GET | `/api/v1/seller/products/{productId}/inventory` | — | `Lista de InventoryResponseDTO` | 200 |
| GET | `/api/v1/seller/orders/{orderId}` | — | `OrderResponseDTO` | 200 |

## Operador logístico – `/api/v1/logistics` (LOGISTICS_OPERATOR) – 20 endpoints

| Método | Ruta | Cuerpo | Respuesta | HTTP |
|---|---|---|---|---|
| GET | `/api/v1/logistics/users/{userId}` | — | `UserResponseDTO` | 200 |
| PUT | `/api/v1/logistics/account` | `UpdateUserRequestDTO` | `UserResponseDTO` | 200 |
| PUT | `/api/v1/logistics/password` | `ChangePasswordRequestDTO` | — | 204 |
| GET | `/api/v1/logistics/warehouses/{warehouseId}` | — | `WarehouseResponseDTO` | 200 |
| GET | `/api/v1/logistics/products` | — | `Lista de ProductResponseDTO` | 200 |
| GET | `/api/v1/logistics/products/{productId}` | — | `ProductResponseDTO` | 200 |
| POST | `/api/v1/logistics/inventory` | `RegisterInitialInventoryRequestDTO` | `InventoryResponseDTO` | 201 |
| POST | `/api/v1/logistics/inventory/{inventoryId}/entries` | `QuantityRequestDTO` | `InventoryResponseDTO` | 200 |
| POST | `/api/v1/logistics/inventory/reservations` | `ReserveInventoryRequestDTO` | `Lista de InventoryAllocationResponseDTO` | 200 |
| POST | `/api/v1/logistics/inventory/{inventoryId}/sale-dispatches` | `QuantityRequestDTO` | `InventoryResponseDTO` | 200 |
| POST | `/api/v1/logistics/inventory/{inventoryId}/adjustments` | `AdjustmentRequestDTO` | `InventoryResponseDTO` | 200 |
| POST | `/api/v1/logistics/inventory/{inventoryId}/returns` | `QuantityRequestDTO` | `InventoryResponseDTO` | 200 |
| GET | `/api/v1/logistics/products/{productId}/inventory` | — | `Lista de InventoryResponseDTO` | 200 |
| GET | `/api/v1/logistics/orders/{orderId}` | — | `OrderResponseDTO` | 200 |
| POST | `/api/v1/logistics/orders/{orderId}/shipments` | — | `Lista de ShipmentResponseDTO` | 201 |
| GET | `/api/v1/logistics/orders/{orderId}/shipments` | — | `Lista de ShipmentResponseDTO` | 200 |
| GET | `/api/v1/logistics/shipments/{shipmentId}` | — | `ShipmentResponseDTO` | 200 |
| POST | `/api/v1/logistics/shipments/{shipmentId}/dispatch` | — | `ShipmentResponseDTO` | 200 |
| POST | `/api/v1/logistics/shipments/{shipmentId}/delivery` | — | `ShipmentResponseDTO` | 200 |
| POST | `/api/v1/logistics/returns/{returnId}/receive` | — | `ReturnResponseDTO` | 200 |

## Supervisor – `/api/v1/supervisor` (SUPERVISOR) – 17 endpoints

| Método | Ruta | Cuerpo | Respuesta | HTTP |
|---|---|---|---|---|
| GET | `/api/v1/supervisor/users/{userId}` | — | `UserResponseDTO` | 200 |
| PUT | `/api/v1/supervisor/account` | `UpdateUserRequestDTO` | `UserResponseDTO` | 200 |
| PUT | `/api/v1/supervisor/password` | `ChangePasswordRequestDTO` | — | 204 |
| GET | `/api/v1/supervisor/buyers/{buyerId}` | — | `BuyerResponseDTO` | 200 |
| GET | `/api/v1/supervisor/sellers/{sellerId}` | — | `SellerResponseDTO` | 200 |
| GET | `/api/v1/supervisor/warehouses/{warehouseId}` | — | `WarehouseResponseDTO` | 200 |
| GET | `/api/v1/supervisor/products` | — | `Lista de ProductResponseDTO` | 200 |
| GET | `/api/v1/supervisor/products/{productId}` | — | `ProductResponseDTO` | 200 |
| GET | `/api/v1/supervisor/products/{productId}/inventory` | — | `Lista de InventoryResponseDTO` | 200 |
| GET | `/api/v1/supervisor/orders/{orderId}` | — | `OrderResponseDTO` | 200 |
| GET | `/api/v1/supervisor/buyers/{buyerId}/orders` | — | `Lista de OrderResponseDTO` | 200 |
| GET | `/api/v1/supervisor/orders/{orderId}/invoice` | — | `InvoiceResponseDTO` | 200 |
| GET | `/api/v1/supervisor/orders/{orderId}/shipments` | — | `Lista de ShipmentResponseDTO` | 200 |
| GET | `/api/v1/supervisor/shipments/{shipmentId}` | — | `ShipmentResponseDTO` | 200 |
| GET | `/api/v1/supervisor/reports` | — | `AdministrativeReportResponseDTO` | 200 |
| GET | `/api/v1/supervisor/audit/users/{userId}` | — | `Lista de AuditLogResponseDTO` | 200 |
| GET | `/api/v1/supervisor/audit/entities/{entityType}/{entityId}` | — | `Lista de AuditLogResponseDTO` | 200 |

> `{entityType}` acepta los valores del enum `AuditEntityType` (`BUYER`, `CART`, `INVENTORY`, `INVOICE`, `ORDER`, `PRODUCT`, `RETURN`, `SELLER`, `SHIPMENT`, `USER`, `WAREHOUSE`); cualquier otro valor responde 400.

## Administrador – `/api/v1/admin` (ADMINISTRATOR) – 46 endpoints

| Método | Ruta | Cuerpo | Respuesta | HTTP |
|---|---|---|---|---|
| POST | `/api/v1/admin/users` | `RegisterInternalUserRequestDTO` | `UserResponseDTO` | 201 |
| GET | `/api/v1/admin/users/{userId}` | — | `UserResponseDTO` | 200 |
| PUT | `/api/v1/admin/users/{userId}` | `UpdateUserRequestDTO` | `UserResponseDTO` | 200 |
| PATCH | `/api/v1/admin/users/{userId}/status` | `StatusRequestDTO` | `UserResponseDTO` | 200 |
| PUT | `/api/v1/admin/password` | `ChangePasswordRequestDTO` | — | 204 |
| GET | `/api/v1/admin/buyers/{buyerId}` | — | `BuyerResponseDTO` | 200 |
| PUT | `/api/v1/admin/buyers/{buyerId}/addresses` | `UpdateBuyerAddressesRequestDTO` | `BuyerResponseDTO` | 200 |
| PATCH | `/api/v1/admin/buyers/{buyerId}/commercial-status` | `StatusRequestDTO` | `BuyerResponseDTO` | 200 |
| POST | `/api/v1/admin/sellers` | `RegisterSellerRequestDTO` | `SellerRegistrationResponseDTO` | 201 |
| GET | `/api/v1/admin/sellers/{sellerId}` | — | `SellerResponseDTO` | 200 |
| PUT | `/api/v1/admin/sellers/{sellerId}` | `UpdateUserRequestDTO` | `SellerResponseDTO` | 200 |
| PATCH | `/api/v1/admin/sellers/{sellerId}/status` | `StatusRequestDTO` | `SellerResponseDTO` | 200 |
| POST | `/api/v1/admin/warehouses` | `RegisterWarehouseRequestDTO` | `WarehouseResponseDTO` | 201 |
| GET | `/api/v1/admin/warehouses/{warehouseId}` | — | `WarehouseResponseDTO` | 200 |
| PUT | `/api/v1/admin/warehouses/{warehouseId}` | `UpdateWarehouseRequestDTO` | `WarehouseResponseDTO` | 200 |
| PATCH | `/api/v1/admin/warehouses/{warehouseId}/status` | `StatusRequestDTO` | `WarehouseResponseDTO` | 200 |
| GET | `/api/v1/admin/products` | — | `Lista de ProductResponseDTO` | 200 |
| GET | `/api/v1/admin/products/{productId}` | — | `ProductResponseDTO` | 200 |
| POST | `/api/v1/admin/products/{productId}/publish` | — | `ProductResponseDTO` | 200 |
| POST | `/api/v1/admin/products/{productId}/suspend` | — | `ProductResponseDTO` | 200 |
| POST | `/api/v1/admin/products/{productId}/discontinue` | — | `ProductResponseDTO` | 200 |
| POST | `/api/v1/admin/inventory` | `RegisterInitialInventoryRequestDTO` | `InventoryResponseDTO` | 201 |
| POST | `/api/v1/admin/inventory/{inventoryId}/entries` | `QuantityRequestDTO` | `InventoryResponseDTO` | 200 |
| POST | `/api/v1/admin/inventory/reservations` | `ReserveInventoryRequestDTO` | `Lista de InventoryAllocationResponseDTO` | 200 |
| POST | `/api/v1/admin/inventory/{inventoryId}/sale-dispatches` | `QuantityRequestDTO` | `InventoryResponseDTO` | 200 |
| POST | `/api/v1/admin/inventory/{inventoryId}/adjustments` | `AdjustmentRequestDTO` | `InventoryResponseDTO` | 200 |
| POST | `/api/v1/admin/inventory/{inventoryId}/returns` | `QuantityRequestDTO` | `InventoryResponseDTO` | 200 |
| GET | `/api/v1/admin/products/{productId}/inventory` | — | `Lista de InventoryResponseDTO` | 200 |
| GET | `/api/v1/admin/orders/{orderId}` | — | `OrderResponseDTO` | 200 |
| GET | `/api/v1/admin/buyers/{buyerId}/orders` | — | `Lista de OrderResponseDTO` | 200 |
| POST | `/api/v1/admin/orders/{orderId}/payment` | — | `OrderResponseDTO` | 200 |
| PATCH | `/api/v1/admin/orders/{orderId}/status` | `StatusRequestDTO` | `OrderResponseDTO` | 200 |
| POST | `/api/v1/admin/orders/{orderId}/invoice` | — | `InvoiceResponseDTO` | 201 |
| GET | `/api/v1/admin/orders/{orderId}/invoice` | — | `InvoiceResponseDTO` | 200 |
| POST | `/api/v1/admin/orders/{orderId}/shipments` | — | `Lista de ShipmentResponseDTO` | 201 |
| GET | `/api/v1/admin/orders/{orderId}/shipments` | — | `Lista de ShipmentResponseDTO` | 200 |
| GET | `/api/v1/admin/shipments/{shipmentId}` | — | `ShipmentResponseDTO` | 200 |
| POST | `/api/v1/admin/shipments/{shipmentId}/dispatch` | — | `ShipmentResponseDTO` | 200 |
| POST | `/api/v1/admin/shipments/{shipmentId}/delivery` | — | `ShipmentResponseDTO` | 200 |
| POST | `/api/v1/admin/returns/{returnId}/receive` | — | `ReturnResponseDTO` | 200 |
| POST | `/api/v1/admin/returns/{returnId}/refund` | `GenerateRefundRequestDTO` | `RefundResponseDTO` | 201 |
| POST | `/api/v1/admin/returns/{returnId}/refund/approve` | — | `RefundResponseDTO` | 200 |
| POST | `/api/v1/admin/returns/{returnId}/refund/reject` | — | `RefundResponseDTO` | 200 |
| GET | `/api/v1/admin/reports` | — | `AdministrativeReportResponseDTO` | 200 |
| GET | `/api/v1/admin/audit/users/{userId}` | — | `Lista de AuditLogResponseDTO` | 200 |
| GET | `/api/v1/admin/audit/entities/{entityType}/{entityId}` | — | `Lista de AuditLogResponseDTO` | 200 |

**Total: 124 endpoints**, uno por cada operación de los puertos de entrada.
