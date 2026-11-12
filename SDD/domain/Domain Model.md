# Domain Model --- NexusMarket


## 1. Introducción

El Modelo de Dominio de **NexusMarket
** representa los principales
conceptos del negocio que participan en la operación de la plataforma y
las relaciones existentes entre ellos.

NexusMarket
 funciona como un marketplace que actúa como intermediario
entre compradores y vendedores. La plataforma permite gestionar
usuarios, vendedores, compradores, productos, bodegas, inventario,
carritos, pedidos, facturación, envíos, devoluciones y reembolsos.

El modelo se construye utilizando conceptos de **Domain-Driven Design
(DDD)**, buscando que las entidades y objetos representen conceptos
reales del negocio y que las reglas importantes sean protegidas dentro
del dominio.

## 2. Principales conceptos del dominio

-   User
-   Buyer
-   Seller
-   Product
-   ProductVariant
-   Warehouse
-   Inventory
-   InventoryMovement
-   Cart
-   CartItem
-   Order
-   OrderLine
-   Invoice
-   Shipment
-   Return
-   Refund

## 3. Clasificación DDD

### Entidades

-   User
-   Buyer
-   Seller
-   Product
-   Warehouse
-   Inventory
-   Cart
-   Order
-   Invoice
-   Shipment
-   Return
-   Refund

### Elementos internos de agregados

-   CartItem
-   OrderLine
-   InventoryMovement

### Value Objects

-   UserRole
-   UserStatus
-   BuyerStatus
-   SellerStatus
-   ProductType
-   ProductStatus
-   ProductVariant
-   WarehouseType
-   InventoryMovementType
-   CartStatus
-   OrderStatus
-   ShipmentStatus
-   ReturnStatus
-   RefundStatus
-   Address
-   Money

## 4. User

### Descripción

`User` representa a una persona que participa en la plataforma.
Contiene la información común necesaria para identificar al participante
y determinar su rol y estado dentro de NexusMarket
.

### Atributos

  Atributo             Tipo conceptual   Descripción
  -------------------- ----------------- --------------------------------------------
  userId               Identificador     Identifica de forma única al usuario.
  name                 String            Nombre completo del usuario.
  email                Email Correo electrónico válido y normalizado.
  identityDocument     IdentityDocument Documento de identificación.
  role                 UserRole        Rol que desempeña dentro de la plataforma.
  status               UserStatus     Estado operativo del usuario.

### Reglas

-   El identificador del usuario debe ser único.
-   El correo electrónico debe ser único.
-   El documento de identidad debe ser único.
-   Cada usuario tiene un único rol.
-   El usuario opera de acuerdo con las responsabilidades de su rol.

## 5. Buyer

### Descripción

`Buyer` representa al participante que adquiere productos dentro de
NexusMarket
.

### Atributos

  ---------------------------------------------------------------------------
  Atributo                 Tipo conceptual            Descripción
  ------------------------ -------------------------- -----------------------
  primaryAddress           Address                  Dirección principal
                                                      utilizada para las
                                                      entregas.

  additionalAddresses      List<Address>   Direcciones adicionales
                                                      registradas.

  commercialStatus         BuyerStatus            Situación comercial del
                                                      comprador.
  ---------------------------------------------------------------------------

### Relaciones

``` text
Buyer
   │
   ├── utiliza ──> Cart
   │
   └── realiza ──> Order
```

### Reglas

-   El comprador debe contar con una dirección principal.
-   Puede registrar direcciones adicionales.
-   Puede utilizar un carrito activo.
-   Puede realizar pedidos.

## 6. Seller

### Descripción

`Seller` representa al participante encargado de registrar y
administrar los productos que comercializa dentro de NexusMarket
.

### Atributos

  Atributo         Tipo conceptual   Descripción
  ---------------- ----------------- --------------------------------
  sellerStatus     SellerStatus    Situación actual del vendedor.

### Relaciones

``` text
Seller
   │
   ├── publica ──> Product
   │
   └── trabaja con ──> Warehouse
```

### Reglas

-   El vendedor debe ser incorporado por el Administrador.
-   Puede registrar productos.
-   Puede administrar los productos que comercializa.
-   Puede trabajar con las bodegas asociadas.

## 7. Product

### Descripción

`Product` representa un bien físico o digital que puede ser ofrecido
dentro del catálogo de NexusMarket
.

### Atributos

  ---------------------------------------------------------------------------------
  Atributo                Tipo conceptual                   Descripción
  ----------------------- --------------------------------- -----------------------
  productId               Identificador                     Identifica el producto.
  sellerId        Identificador     Vendedor propietario del producto.

  productName             String                            Nombre del producto.

  productType             ProductType                      Indica si es físico o
                                                            digital.

  variants                List<ProductVariant>   Características que
                                                            diferencian las
                                                            presentaciones.

  status                  ProductStatus                    Estado comercial dentro
                                                            del catálogo.

  currentPrice            Money                            Precio actual del
                                                            producto.
  ---------------------------------------------------------------------------------

### Relaciones

``` text
Seller
   │
   └── publica ──> Product

Product
   │
   ├── puede tener ──> ProductVariant
   │
   └── puede estar disponible en ──> Inventory
```

### Reglas

-   Puede ser físico o digital.
-   Puede tener variantes.
-   Tiene un estado dentro del catálogo.
-   Los productos físicos requieren inventario y despacho.
-   Los productos digitales tienen entrega inmediata después del pago.

## 8. ProductVariant

### Descripción

`ProductVariant` representa una característica que diferencia una
presentación de un producto.

Ejemplos:

``` text
Color = Negro
Talla = M
Modelo = 2026
```

Se implementa como Value Object con tres atributos: `sku` (identifica la
variante), `attribute` (por ejemplo `Color`) y `value` (por ejemplo `Negro`).
Su estructura podrá ajustarse si los requisitos detallan cómo las variantes
afectan el inventario.

## 9. Warehouse

### Descripción

`Warehouse` representa un lugar físico donde se almacenan productos.

### Atributos

  Atributo    Tipo conceptual   Descripción
  ----------- ----------------- -----------------------------
  warehouseId  Identificador     Identificador único.
  location    String            Ubicación de la bodega.
  sellerId    Identificador     Vendedor dueño (nulo en bodegas del marketplace).
  type        WarehouseType        Clasificación de la bodega.
  status      WarehouseStatus   Estado operativo de la bodega.

### Relaciones

``` text
Warehouse
   │
   ├── almacena ──> Inventory
   │
   └── puede despachar ──> Shipment
```

## 10. Inventory

### Descripción

`Inventory` representa las existencias de un producto dentro de una
bodega específica.

### Atributos

  Atributo             Tipo conceptual   Descripción
  -------------------- ----------------- -------------------------------
  inventoryId          Identificador     Identificador del inventario.
  productId            Identificador     Producto al que corresponde.
  sku                  Sku               Variante del producto almacenada.
  warehouseId          Identificador     Bodega donde se almacena.
  availableQuantity    Quantity          Existencias disponibles.
  reservedQuantity     Quantity          Existencias separadas por pedidos.
  damagedQuantity      Quantity          Existencias marcadas como dañadas.
  movements            List<InventoryMovement>  Historial de movimientos.

### Regla principal

``` text
availableQuantity >= 0
reservedQuantity  >= 0
damagedQuantity     >= 0
```

No se permiten existencias negativas. Un ajuste de inventario se aplica
como una diferencia con signo (`delta`) y no puede dejar la cantidad
disponible por debajo de cero.

## 11. InventoryMovement

### Descripción

`InventoryMovement` representa una operación que modifica o registra
el comportamiento de las existencias.

### Tipos

``` text
INBOUND
RESERVATION
RELEASE
SALE_OUTBOUND
ADJUSTMENT
MARK_DAMAGED
RETURN
```

`RELEASE` (devuelve a disponible una reserva cancelada) y
`MARK_DAMAGED` (mueve existencias disponibles a dañadas) se añadieron
al implementar el inventario con cantidades reservada y dañada.

### Relación

``` text
Inventory
   │
   └── registra ──> InventoryMovement
```

## 12. Cart

### Descripción

`Cart` representa el espacio temporal donde el comprador selecciona
productos antes de confirmar la compra.

### Atributos

  Atributo        Tipo conceptual   Descripción
  --------------- ----------------- ----------------------------
  cartId          Identificador     Identificador del carrito.
  buyerId         Identificador     Comprador propietario.
  createdAt       Fecha             Fecha de creación.
  currency        Currency            Currency única del carrito.
  total           Money            Valor acumulado (calculado).
  status          CartStatus     Estado actual.

### Relaciones

``` text
Buyer
    │
    └── utiliza ──> Cart
                       │
                       └── contiene ──> CartItem
```

### Regla

Un comprador puede tener cero o un carrito activo.

## 13. CartItem

### Descripción

`CartItem` representa un producto seleccionado dentro de un carrito y
es un elemento interno del agregado `Cart`.

### Atributos

  Atributo         Tipo conceptual   Descripción
  ---------------- ----------------- -------------------------------
  productId        Identificador     Producto seleccionado.
  sku              Sku               Variante seleccionada.
  quantity         Quantity (> 0)    Unidades seleccionadas.
  unitPrice        Money            Precio aplicado al agregarlo.

## 14. Order

### Descripción

`Order` representa el compromiso comercial generado después de que el
comprador confirma su carrito.

### Atributos

  Atributo        Tipo conceptual              Descripción
  --------------- ---------------------------- ---------------------------
  orderId         Identificador                Identificador del pedido.
  buyerId         Identificador                Comprador que lo realiza.
  createdAt       Fecha                        Fecha de creación.
  currency        Currency                       Currency única del pedido.
  status          OrderStatus                 Estado actual.
  total           Money                       Valor total (calculado).
  lines           List<OrderLine>   Productos incluidos.

### Relaciones

``` text
Buyer
   │
   └── realiza ──> Order
                       │
                       └── contiene ──> OrderLine
                                             │
                                             └── corresponde ──> Product
```

### Ciclo de vida

``` text
Cart
   │
   │ confirmar
   ↓
Order
   ↓
PENDING_PAYMENT
   ↓
PAID
   ↓
SHIPPED
   ↓
DELIVERED
   ↓
COMPLETED
```

Transiciones válidas:

``` text
PENDING_PAYMENT → PAID | CANCELLED
PAID         → SHIPPED | CANCELLED | COMPLETED (productos digitales)
SHIPPED     → DELIVERED
DELIVERED      → COMPLETED
COMPLETED     → (ninguna)
CANCELLED      → (ninguna)
```

`CANCELLED` solo es posible antes del despacho.

`Cart` no es un estado de `Order`; es un concepto independiente que
precede a la creación del pedido.

### Regla

Un pedido finalizado o cancelado no puede modificarse.

## 15. OrderLine

### Descripción

`OrderLine` representa cada producto incluido en un pedido y es un
elemento interno del agregado `Order`.

### Atributos

  Atributo         Tipo conceptual   Descripción
  ---------------- ----------------- ------------------------------------------
  productId        Identificador     Producto adquirido.
  sku              Sku               Variante adquirida.
  sellerId         Identificador     Vendedor (se asigna al preparar el despacho; puede ser nulo al crear el pedido).
  warehouseId      Identificador     Bodega de despacho (ídem).
  quantity         Quantity (> 0)    Unidades solicitadas.
  unitPrice        Money            Precio aplicado al momento de la compra.

## 16. Invoice

### Descripción

`Invoice` representa la información comercial generada como resultado
del pedido.

### Atributos

  Atributo    Tipo conceptual   Descripción
  ----------- ----------------- ------------------------------
  invoiceId   Identificador     Identificador de la factura.
  orderId     Identificador     Pedido facturado.
  number      InvoiceNumber     Número de factura.
  date        Fecha             Fecha de emisión.
  subtotal    Money            Valor antes del total final.
  total       Money            Valor final.

### Relación

``` text
Order
   │
   └── genera ──> Invoice
```

## 17. Shipment

### Descripción

`Shipment` representa el proceso logístico mediante el cual un pedido es
preparado, despachado, transportado y entregado.

### Atributos

  Atributo           Tipo conceptual   Descripción
  ------------------ ----------------- --------------------------
  shipmentId         Identificador     Identificador del envío.
  orderId            Identificador     Pedido que se despacha.
  warehouseId        Identificador     Bodega que despacha.
  status             ShipmentStatus       Estado logístico.
  deliveryAddress    Address         Dirección de entrega.

### Relaciones

``` text
Order
   │
   └── requiere ──> Shipment
                       │
                       └── es despachado por ──> Warehouse
```

## 18. Return

### Descripción

`Return` representa el proceso mediante el cual un comprador
solicita devolver un pedido o parte de él de acuerdo con las condiciones
del negocio.

### Atributos

  Atributo       Tipo conceptual    Descripción
  -------------- ------------------ --------------------------
  returnId       Identificador      Identificador.
  orderId        Identificador      Pedido que origina la devolución.
  reason         String             Motivo de la devolución.
  date           Fecha              Fecha de solicitud.
  status         ReturnStatus   Estado actual.
  refund         Refund             Reembolso asociado (opcional).

### Relación

``` text
Order
   │
   └── puede originar ──> Return
                              │
                              └── puede generar ──> Refund
```

## 19. Refund

### Descripción

`Refund` representa la devolución del dinero asociada a una
devolución procesada.

### Atributos

  Atributo      Tipo conceptual   Descripción
  ------------- ----------------- ----------------------
  refundId      Identificador     Identificador.
  returnId      Identificador     Devolución a la que pertenece.
  amount        Money            Valor reembolsado.
  date          Fecha             Fecha del reembolso.
  status        RefundStatus   Estado actual.

### Regla

Una devolución puede generar como máximo un reembolso.

## 20. Agregados iniciales

``` text
Cart
└── CartItem

Order
└── OrderLine

Inventory
└── InventoryMovement
```

Aggregate Roots iniciales:

``` text
Cart
Order
Inventory
```

La definición de otros agregados relacionados con facturación, logística
y posventa queda sujeta al análisis posterior de sus reglas.

## 21. Reglas principales

### Usuarios

-   Identificador único.
-   Correo único.
-   Documento único.
-   Un único rol.
-   Operación de acuerdo con el rol.

### Compradores

-   Dirección principal.
-   Direcciones adicionales opcionales.
-   Cart activo.
-   Realización de pedidos.

### Vendedores

-   Incorporados por Administrador.
-   Registran productos.
-   Administran productos.
-   Trabajan con bodegas asociadas.

### Productos

-   Físicos o digitales.
-   Pueden tener variantes.
-   Tienen estado de catálogo.
-   Los físicos requieren inventario y despacho.
-   Los digitales tienen entrega inmediata después del pago.

### Inventory

-   Asociado a producto y bodega.
-   No permite existencias negativas.
-   Registra movimientos.

### Cart

-   Cero o un carrito activo por comprador.
-   Contiene elementos.
-   Puede confirmarse para generar un pedido.

### Order

-   Contiene una o más líneas.
-   Tiene ciclo de vida.
-   Un pedido finalizado no puede modificarse.

### Devoluciones

-   Un pedido puede originar devoluciones.
-   Una devolución puede generar como máximo un reembolso.

## 22. Relaciones generales

``` text
                           User
                              │
                ┌─────────────┴─────────────┐
                │                           │
           Buyer                     Seller
                │                           │
        ┌───────┴───────┐             ┌────┴─────┐
        │               │             │          │
     Cart          Order       Product    Warehouse
        │               │             │          │
 CartItem      OrderLine         │     Inventory
        │               │             │          │
        └───────┬───────┘             │          │
                │                      └────┬─────┘
                │                           │
              Product                 InventoryMovement
                                         Inventory

Order
 ├── Invoice
 ├── Shipment
 └── Return
        │
        └── Refund
```

## 23. Ciclo general

``` text
Administrador
      │
      └── incorpora
             ↓
          Seller
             │
             ├── registra
             ↓
          Product
             │
             ↓
         Inventory
             │
             ↓
           Warehouse

Buyer
   │
   └── selecciona productos
          ↓
       Cart
          │
          └── contiene
                ↓
           CartItem
                │
                ↓
             Product
                │
                │ confirmar
                ↓
              Order
                │
       ┌────────┼─────────┐
       ↓        ↓         ↓
    Invoice    Shipment   Return
                         │
                         ↓
                     Refund
```

## 24. Resumen

El modelo de dominio de NexusMarket
 se organiza alrededor de usuarios,
compradores, vendedores, productos, bodegas, inventario, carritos,
pedidos y los procesos de facturación, logística y posventa.

Los agregados iniciales identificados son `Cart`, `Order` e
`Inventory`, cada uno protegiendo sus elementos internos y reglas
principales.

Este modelo constituye la base para la implementación posterior de las
entidades, Value Objects, casos de uso y puertos de la Arquitectura
Hexagonal.

## 25. Glosario español ↔ código Java

Los nombres de clases, atributos y valores de este documento coinciden 1:1 con el
código Java (100 % en inglés); la prosa está en español. Esta tabla recuerda el
concepto de negocio en español de cada clase.

| Concepto de negocio (español) | Código Java (`application.domain...`) |
|---|---|
| Usuario | `models.User` |
| Comprador | `models.Buyer` |
| Vendedor | `models.Seller` |
| Producto | `models.Product` |
| Bodega | `models.Warehouse` |
| Inventario | `models.Inventory` |
| MovimientoInventario | `models.InventoryMovement` |
| Carrito / ItemCarrito | `models.Cart` / `models.CartItem` |
| Pedido / LineaPedido | `models.Order` / `models.OrderLine` |
| Factura | `models.Invoice` |
| Envio | `models.Shipment` |
| Devolucion | `models.Return` |
| Reembolso | `models.Refund` |
| idUsuario, idProducto, ... | `userId`, `productId`, ... |
| nombre, correoElectronico, documentoIdentidad | `name`, `email`, `identityDocument` |
| estado, rol, tipo | `status`, `role`, `type` |
| cantidadDisponible / Reservada / Danada | `availableQuantity` / `reservedQuantity` / `damagedQuantity` |
| precioUnitario, precioActual | `unitPrice`, `currentPrice` |
| direccionPrincipal, direccionesAdicionales | `primaryAddress`, `additionalAddresses` |

## 26. Ajustes del modelo derivados de los servicios

- **InternalUser** (`InternalUser`): usuario del personal con rol
  `ADMINISTRATOR`, `LOGISTICS_OPERATOR` o `SUPERVISOR`. Compradores y vendedores
  tienen sus propias clases.
- **Product:** ciclo `SUSPENDED → PUBLISHED ⇄ SUSPENDED` y `DISCONTINUED`
  (terminal). Métodos `publish`, `suspend`, `discontinue`; ya no existe un cambio de estado libre.
- **Shipment:** `PENDING → PREPARING → SHIPPED → DELIVERED`. Hay un envío
  por pedido y bodega.
- **Return:** `REQUESTED → RECEIVED → APPROVED | REJECTED`. El reembolso se
  genera solo con la devolución `RECEIVED` y nace `PENDING`; aprobarlo o
  rechazarlo fija el estado final de la devolución.
- **Refund:** `PENDING → APPROVED | REJECTED`. Solo se modifica a través de
  la devolución (aggregate root).
- **Cart:** agregar la misma variante suma las cantidades; quitar un producto
  que no está en el carrito es un error.
- **OrderLine:** `warehouseId` es nulo en productos digitales; en productos físicos
  identifica la bodega que despacha.

## 27. Credenciales, autenticación y auditoría

- **UserCredentials** (`UserCredentials`): userId, hash de la contraseña y
  versión del token. Está separada de `User` para que el hash nunca viaje con
  la entidad. Cambiar la contraseña o cerrar sesión incrementa la versión e invalida
  todos los tokens emitidos.
- **AuthenticationResult** (`AuthenticationResult`): token y usuario autenticado.
- **AuditLog** (`AuditLog`): userId, operación (un `Permission`), tipo (`AuditEntityType`) e id
  de la entidad afectada, detalle y fecha. Se guarda en MongoDB.
- **AdministrativeReport** (`AdministrativeReport`), **SellerRegistration**
  (`SellerRegistration`) y **InventoryAllocation** (`InventoryAllocation`): resultados
  de servicios, ubicados en el dominio para que los puertos no dependan de los servicios.
