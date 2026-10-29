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
