# Domain Services - NexusMarket

## Catálogo General de Servicios de Dominio

Este documento presenta el catálogo general de **Domain Services** propuestos para **NexusMarket**, agrupados por subdominio funcional.

---

# 1. User Management Services
    
| Servicio | Responsabilidad |
|-----------|----------------|
| Register User | Registra la información de un usuario respetando la unicidad del correo electrónico y del documento de identidad. |
| Consult User | Consulta la información de un usuario conforme a los permisos del solicitante. |
| Update User | Actualiza la información administrada de un usuario respetando las restricciones del sistema. |
| Change User Status | Modifica el estado operativo del usuario conforme a los estados admitidos. |

---

# 2. Buyer Management Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Register Buyer | Registra un comprador con su dirección principal y su estado comercial. |
| Update Buyer Information | Actualiza la información comercial y las direcciones del comprador conforme a las reglas aplicables. |
| Change Buyer Commercial Status | Cambia el estado comercial del comprador que determina si puede realizar compras. |
| Consult Buyer | Consulta la información del comprador respetando la restricción de acceso a información propia y los permisos del solicitante. |

---

# 3. Seller Management Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Register Seller | Coordina la incorporación administrativa de un vendedor y el registro de su primera bodega. |
| Consult Seller | Consulta la información de un vendedor según los permisos del solicitante. |
| Update Seller Information | Actualiza la información administrada del vendedor conforme a las reglas del negocio. |
| Change Seller Status | Cambia el estado operativo del vendedor respetando las restricciones aplicables. |

---

# 4. Warehouse Management Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Register Warehouse | Registra una bodega con su información y clasificación correspondiente. |
| Consult Warehouse | Consulta la información de una bodega según los permisos del solicitante. |
| Update Warehouse | Actualiza la información administrada de una bodega conforme a las reglas aplicables. |
| Change Warehouse Status | Cambia el estado de una bodega respetando las restricciones sobre bodegas inactivas. |

---

# 5. Product Catalog Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Register Product | Registra un producto con su tipo, características y variantes. |
| Update Product | Actualiza las características de un producto respetando la propiedad del vendedor. |
| Publish Product | Coordina la publicación de un producto en el catálogo. |
| Suspend Product | Cambia el estado del producto a suspendido. |
| Discontinue Product | Cambia el estado del producto a descontinuado. |
| Consult Product | Consulta los productos visibles según su estado y las reglas de acceso correspondientes. |

---

# 6. Inventory Management Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Register Initial Inventory | Registra las existencias iniciales de un producto en una bodega. |
| Register Inventory Entry | Registra el ingreso de existencias al inventario. |
| Reserve Inventory | Reserva existencias disponibles para una operación comercial. |
| Register Sale Dispatch | Registra la salida de inventario correspondiente a una venta despachada. |
| Adjust Inventory | Registra un ajuste de existencias respetando las restricciones del inventario. |
| Register Inventory Return | Registra el movimiento de devolución al inventario cuando corresponda. |
| Consult Inventory | Consulta las existencias asociadas a un producto y una bodega. |

---

# 7. Shopping Cart Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Add Product to Cart | Coordina la incorporación de un producto al carrito del comprador. |
| Update Cart Item | Coordina la actualización de un elemento seleccionado en el carrito. |
| Remove Product from Cart | Coordina la eliminación de un producto seleccionado. |
| Consult Cart | Recupera el contenido del carrito correspondiente al comprador autorizado. |

---

# 8. Order Management Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Create Order | Coordina la creación del pedido formal a partir de la compra confirmada. |
| Validate Order Payment | Coordina la validación del pago asociado al pedido. |
| Update Order Status | Coordina los cambios de estado del pedido. |
| Consult Order | Consulta la información de un pedido conforme a los permisos del solicitante. |

---

# 9. Billing Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Generate Invoice | Coordina la generación de la información de facturación de una compra. |
| Consult Invoice | Consulta la información de una factura conforme a los permisos correspondientes. |

---

# 10. Logistics Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Prepare Shipment | Coordina la preparación logística de un pedido físico pagado. |
| Dispatch Shipment | Coordina el despacho del envío desde la bodega correspondiente. |
| Confirm Delivery | Coordina la confirmación de entrega y el cierre del pedido. |
| Consult Shipment | Consulta la información y el estado de un envío. |

---

# 11. Return and Refund Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Request Return | Coordina la solicitud de devolución realizada por el comprador. |
| Receive Return | Coordina el registro de la recepción de los productos devueltos. |
| Approve Refund | Coordina la aprobación del reembolso por parte del administrador. |
| Reject Refund | Coordina el rechazo del reembolso por parte del administrador. |
| Generate Refund | Coordina la generación del reembolso cuando se cumplen las condiciones establecidas. |

---

# 12. Administrative Reporting Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Consult Administrative Reports | Permite consultar información administrativa consolidada. |

---

# 13. Authorization Services

| Servicio | Responsabilidad |
|-----------|----------------|
| Validate User Permissions | Determina si el rol del usuario permite ejecutar una operación. |
| Validate Resource Access | Determina si el usuario puede acceder al recurso solicitado respetando las restricciones de propiedad y rol. |


## 14. Implementación de los servicios de dominio

Los 51 servicios del catálogo están implementados en
`application.domain.services.<grupo>`, una clase por servicio. Cada servicio
recibe al usuario solicitante (salvo el autorregistro del comprador), valida
su permiso, carga los aggregates mediante **puertos de salida** (ver
`Output-ports.md`), aplica las reglas y persiste. Los errores se expresan con
las excepciones de `application.domain.exceptions`:
`EntityNotFoundException` (404), `DuplicateEntityException` (409),
`BusinessRuleViolationException` (409/422) y `UnauthorizedOperationException` (403).

### 14.1 Catálogo implementado


| # | Servicio (código) | Permiso | Reglas principales |
|---|---|---|---|
| 1 | `authorization.ValidateUserPermissionsService` | — | El usuario debe estar `ACTIVO` y su rol debe tener el permiso (matriz de la sección 14.3). |
| 2 | `authorization.ValidateResourceAccessService` | — | Propietario del recurso o rol privilegiado; un vendedor solo accede al inventario de sus productos. |
| 3 | `user.RegisterUserService` | `USER_REGISTER` | Registra personal interno (administrador, operador logístico, supervisor). Correo y documento únicos. |
| 4 | `user.ConsultUserService` | `USER_CONSULT` | Cada usuario consulta sus datos; administrador y supervisor consultan cualquiera. |
| 5 | `user.UpdateUserService` | `USER_UPDATE` | Nombre y/o correo; propio o administrador; correo único. |
| 6 | `user.ChangeUserStatusService` | `USER_CHANGE_STATUS` | Solo administrador. |
| 7 | `buyer.RegisterBuyerService` | pública | Autorregistro; nace `ACTIVO`/`HABILITADO`; correo y documento únicos. |
| 8 | `buyer.UpdateBuyerInformationService` | `BUYER_UPDATE` | Dirección principal y/o direcciones adicionales; propio o administrador. |
| 9 | `buyer.ChangeBuyerCommercialStatusService` | `BUYER_CHANGE_COMMERCIAL_STATUS` | Solo administrador; un comprador `RESTRINGIDO` no compra. |
| 10 | `buyer.ConsultBuyerService` | `BUYER_CONSULT` | Propio, administrador o supervisor. |
| 11 | `seller.RegisterSellerService` | `SELLER_REGISTER` | Solo administrador; crea el vendedor y su primera bodega. |
| 12 | `seller.ConsultSellerService` | `SELLER_CONSULT` | Propio, administrador o supervisor. |
| 13 | `seller.UpdateSellerInformationService` | `SELLER_UPDATE` | Solo administrador; correo único. |
| 14 | `seller.ChangeSellerStatusService` | `SELLER_CHANGE_STATUS` | Un vendedor suspendido no registra ni publica productos ni vende. |
| 15 | `warehouse.RegisterWarehouseService` | `WAREHOUSE_REGISTER` | Solo administrador; bodega de vendedor exige vendedor activo; bodega del marketplace no tiene vendedor. |
| 16 | `warehouse.ConsultWarehouseService` | `WAREHOUSE_CONSULT` | El vendedor solo ve las suyas. |
| 17 | `warehouse.UpdateWarehouseService` | `WAREHOUSE_UPDATE` | Solo administrador (ubicación). |
| 18 | `warehouse.ChangeWarehouseStatusService` | `WAREHOUSE_CHANGE_STATUS` | No se desactiva una bodega con stock reservado. |
| 19 | `product.RegisterProductService` | `PRODUCT_REGISTER` | Vendedor activo; al menos una variante (SKU); nace `SUSPENDIDO`. |
| 20 | `product.UpdateProductService` | `PRODUCT_UPDATE` | Solo el vendedor propietario; un producto descontinuado no se modifica. |
| 21 | `product.PublishProductService` | `PRODUCT_PUBLISH` | `SUSPENDIDO → PUBLICADO`; vendedor activo; con variantes. |
| 22 | `product.SuspendProductService` | `PRODUCT_SUSPEND` | `PUBLICADO → SUSPENDIDO`. |
| 23 | `product.DiscontinueProductService` | `PRODUCT_DISCONTINUE` | Estado terminal. |
| 24 | `product.ConsultProductService` | `PRODUCT_CONSULT` | El comprador solo ve `PUBLICADO`; el vendedor ve los propios y los publicados; personal ve todo. |
| 25 | `inventory.RegisterInitialInventoryService` | `INVENTORY_REGISTER_INITIAL` | Solo productos físicos; SKU del producto; bodega activa y del mismo vendedor; sin duplicados (producto, SKU, bodega). |
| 26 | `inventory.RegisterInventoryEntryService` | `INVENTORY_REGISTER_ENTRY` | Bodega activa; movimiento `INGRESO`. |
| 27 | `inventory.ReserveInventoryService` | `INVENTORY_RESERVE` | Reparte la reserva entre bodegas activas; `plan` (sin efectos) y `apply`. |
| 28 | `inventory.RegisterSaleDispatchService` | `INVENTORY_REGISTER_SALE_DISPATCH` | Consume primero la reserva; movimiento `SALIDA_VENTA`. |
| 29 | `inventory.AdjustInventoryService` | `INVENTORY_ADJUST` | Diferencia con signo; nunca deja el stock negativo. |
| 30 | `inventory.RegisterInventoryReturnService` | `INVENTORY_REGISTER_RETURN` | Movimiento `DEVOLUCION`. |
| 31 | `inventory.ConsultInventoryService` | `INVENTORY_CONSULT` | Por producto y, opcionalmente, bodega; el vendedor solo el suyo. |
| 32 | `cart.AddProductToCartService` | `CART_MANAGE` | Comprador habilitado; producto `PUBLICADO`; SKU válido; misma variante suma cantidades; una sola moneda. |
| 33 | `cart.UpdateCartItemService` | `CART_MANAGE` | Cantidad mayor que cero. |
| 34 | `cart.RemoveProductFromCartService` | `CART_MANAGE` | Quita todas las variantes del producto. |
| 35 | `cart.ConsultCartService` | `CART_CONSULT` | Carrito activo propio. |
| 36 | `order.CreateOrderService` | `ORDER_CREATE` | Valida comprador, productos y vendedores antes de modificar; reserva inventario (puede dividirse en varias bodegas); crea el pedido `PENDIENTE_PAGO`. |
| 37 | `order.ValidateOrderPaymentService` | `ORDER_VALIDATE_PAYMENT` | `PENDIENTE_PAGO → PAGADO`; si solo hay productos digitales pasa a `FINALIZADO`. |
| 38 | `order.UpdateOrderStatusService` | `ORDER_UPDATE_STATUS` | Solo `CANCELADO` (libera reservas) y `FINALIZADO` (administrador); el resto lo mueven pago y logística. |
| 39 | `order.ConsultOrderService` | `ORDER_CONSULT` | Comprador los suyos; vendedor los que contienen sus productos; personal todos. |
| 40 | `billing.GenerateInvoiceService` | `INVOICE_GENERATE` | Pedido pagado; una factura por pedido; subtotal = total (impuestos sin definir). |
| 41 | `billing.ConsultInvoiceService` | `INVOICE_CONSULT` | Comprador del pedido, administrador, supervisor. |
| 42 | `logistics.PrepareShipmentService` | `SHIPMENT_PREPARE` | Pedido `PAGADO` con productos físicos; un envío por bodega; dirección = principal del comprador. |
| 43 | `logistics.DispatchShipmentService` | `SHIPMENT_DISPATCH` | `EN_PREPARACION → DESPACHADO`; registra la salida de inventario; con todos los envíos despachados el pedido pasa a `DESPACHADO`. |
| 44 | `logistics.ConfirmDeliveryService` | `SHIPMENT_CONFIRM_DELIVERY` | `DESPACHADO → ENTREGADO`; con todos entregados el pedido se entrega y finaliza. |
| 45 | `logistics.ConsultShipmentService` | `SHIPMENT_CONSULT` | Por envío o por pedido; comprador del pedido y personal. |
| 46 | `returns.RequestReturnService` | `RETURN_REQUEST` | Solo el comprador del pedido; pedido `ENTREGADO` o `FINALIZADO`; nace `SOLICITADA`. |
| 47 | `returns.ReceiveReturnService` | `RETURN_RECEIVE` | `SOLICITADA → RECIBIDA`. |
| 48 | `returns.GenerateRefundService` | `REFUND_GENERATE` | Devolución `RECIBIDA`; misma moneda del pedido; reembolsos acumulados (sin los rechazados) ≤ total; nace `PENDIENTE`; máximo uno por devolución. |
| 49 | `returns.ApproveRefundService` | `REFUND_APPROVE` | Reembolso `APROBADO` y devolución `APROBADA`. |
| 50 | `returns.RejectRefundService` | `REFUND_REJECT` | Reembolso `RECHAZADO` y devolución `RECHAZADA`. |
| 51 | `reporting.ConsultAdministrativeReportsService` | `REPORT_CONSULT` | Administrador y supervisor: usuarios por rol, productos y pedidos por estado, ventas por moneda. |

### 14.2 Decisiones de diseño y supuestos a confirmar

La especificación funcional no precisa los siguientes puntos; se adoptó el
criterio indicado y **debe confirmarse con el docente**:

1. **Producto nuevo:** `ProductStatus` no tiene borrador, por lo que un producto
   registrado nace `SUSPENDIDO` (no visible) hasta que se publica. Un producto
   necesita al menos una variante (SKU), porque el SKU es la unidad vendible.
2. **Inventario distribuido:** el inventario es por (producto, SKU, bodega). Una
   reserva puede repartirse entre varias bodegas activas; por eso un pedido puede
   tener varias líneas del mismo producto y un envío **por bodega**.
3. **Devoluciones:** solo se piden sobre pedidos `ENTREGADO` o `FINALIZADO`; un
   pedido puede originar varias devoluciones. El ciclo es
   `SOLICITADA → RECIBIDA → APROBADA | RECHAZADA`, donde el último paso es el
   resultado de la decisión sobre el reembolso. Devolver las existencias al
   inventario es un paso aparte (`RegisterInventoryReturnService`), porque la
   devolución no guarda el detalle de líneas.
4. **Pago:** no se modela una pasarela; quien llama afirma que el pago se recibió.
5. **Facturación:** sin impuestos definidos, subtotal y total son iguales.
6. **Cancelación de un pedido pagado:** libera el inventario, pero no genera
   reembolso automático (queda fuera de la especificación).
7. **Permisos por rol** (14.3): se infirieron de las responsabilidades de cada rol.
8. **Auditoría y autenticación (contraseñas/JWT):** no están en el catálogo; se
   abordan en los adaptadores.
9. **Atomicidad:** varios servicios modifican más de un aggregate (carrito,
   inventarios, pedido). El caso de uso que los invoque debe ejecutarse en una
   transacción, y el inventario necesitará control de concurrencia (versión
   optimista) en la persistencia.

### 14.3 Matriz de permisos por rol

El propietario del recurso se valida aparte (`ValidateResourceAccessService`):
por ejemplo, un comprador solo accede a sus pedidos.

| Permiso | COMPRADOR | VENDEDOR | ADMINISTRADOR | OP. LOGÍSTICO | SUPERVISOR |
|---|---|---|---|---|---|
| `USER_REGISTER` |  |  | ✔ |  |  |
| `USER_CONSULT` | ✔ | ✔ | ✔ | ✔ | ✔ |
| `USER_UPDATE` | ✔ | ✔ | ✔ | ✔ | ✔ |
| `USER_CHANGE_STATUS` |  |  | ✔ |  |  |
| `BUYER_UPDATE` | ✔ |  | ✔ |  |  |
| `BUYER_CHANGE_COMMERCIAL_STATUS` |  |  | ✔ |  |  |
| `BUYER_CONSULT` | ✔ |  | ✔ |  | ✔ |
| `SELLER_REGISTER` |  |  | ✔ |  |  |
| `SELLER_CONSULT` |  | ✔ | ✔ |  | ✔ |
| `SELLER_UPDATE` |  |  | ✔ |  |  |
| `SELLER_CHANGE_STATUS` |  |  | ✔ |  |  |
| `WAREHOUSE_REGISTER` |  |  | ✔ |  |  |
| `WAREHOUSE_CONSULT` |  | ✔ | ✔ | ✔ | ✔ |
| `WAREHOUSE_UPDATE` |  |  | ✔ |  |  |
| `WAREHOUSE_CHANGE_STATUS` |  |  | ✔ |  |  |
| `PRODUCT_REGISTER` |  | ✔ |  |  |  |
| `PRODUCT_UPDATE` |  | ✔ |  |  |  |
| `PRODUCT_PUBLISH` |  | ✔ | ✔ |  |  |
| `PRODUCT_SUSPEND` |  | ✔ | ✔ |  |  |
| `PRODUCT_DISCONTINUE` |  | ✔ | ✔ |  |  |
| `PRODUCT_CONSULT` | ✔ | ✔ | ✔ | ✔ | ✔ |
| `INVENTORY_REGISTER_INITIAL` |  | ✔ | ✔ | ✔ |  |
| `INVENTORY_REGISTER_ENTRY` |  | ✔ | ✔ | ✔ |  |
| `INVENTORY_RESERVE` |  |  | ✔ | ✔ |  |
| `INVENTORY_REGISTER_SALE_DISPATCH` |  |  | ✔ | ✔ |  |
| `INVENTORY_ADJUST` |  | ✔ | ✔ | ✔ |  |
| `INVENTORY_REGISTER_RETURN` |  |  | ✔ | ✔ |  |
| `INVENTORY_CONSULT` |  | ✔ | ✔ | ✔ | ✔ |
| `CART_MANAGE` | ✔ |  |  |  |  |
| `CART_CONSULT` | ✔ |  |  |  |  |
| `ORDER_CREATE` | ✔ |  |  |  |  |
| `ORDER_VALIDATE_PAYMENT` | ✔ |  | ✔ |  |  |
| `ORDER_UPDATE_STATUS` | ✔ |  | ✔ |  |  |
| `ORDER_CONSULT` | ✔ | ✔ | ✔ | ✔ | ✔ |
| `INVOICE_GENERATE` | ✔ |  | ✔ |  |  |
| `INVOICE_CONSULT` | ✔ |  | ✔ |  | ✔ |
| `SHIPMENT_PREPARE` |  |  | ✔ | ✔ |  |
| `SHIPMENT_DISPATCH` |  |  | ✔ | ✔ |  |
| `SHIPMENT_CONFIRM_DELIVERY` |  |  | ✔ | ✔ |  |
| `SHIPMENT_CONSULT` | ✔ |  | ✔ | ✔ | ✔ |
| `RETURN_REQUEST` | ✔ |  |  |  |  |
| `RETURN_RECEIVE` |  |  | ✔ | ✔ |  |
| `REFUND_GENERATE` |  |  | ✔ |  |  |
| `REFUND_APPROVE` |  |  | ✔ |  |  |
| `REFUND_REJECT` |  |  | ✔ |  |  |
| `REPORT_CONSULT` |  |  | ✔ |  | ✔ |
