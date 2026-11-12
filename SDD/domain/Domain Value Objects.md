# Domain Value Objects --- NexusMarket

## 1. Introducción

Los **Value Objects** representan conceptos del dominio cuyo significado
depende del valor que contienen y no de una identidad propia.

En NexusMarket permiten representar de manera clara conceptos como
roles, estados, tipos de producto, direcciones, dinero y tipos de
movimientos de inventario.

## 2. Value Objects identificados

``` text
Domain Value Objects
│
├── UserRole
├── UserStatus
├── BuyerStatus
├── SellerStatus
├── ProductType
├── ProductStatus
├── ProductVariant
├── WarehouseType
├── InventoryMovementType
├── CartStatus
├── OrderStatus
├── ShipmentStatus
├── ReturnStatus
├── RefundStatus
├── AddressType
├── Address
├── Currency
├── Money
├── Email
├── IdentityDocument
├── Sku
└── InvoiceNumber
```

## 3. UserRole

### Descripción

`UserRole` representa la responsabilidad que tiene un usuario dentro
de NexusMarket.

Cada usuario posee un único rol.

### Valores definidos

  -----------------------------------------------------------------------
  Código                  Nombre                  Responsabilidad
  ----------------------- ----------------------- -----------------------
  BUYER               Buyer               Adquiere productos
                                                  publicados.

  SELLER                Seller                Registra y administra
                                                  productos.

  ADMINISTRATOR           Administrador           Administra vendedores y
                                                  bodegas.

  LOGISTICS_OPERATOR      Operador Logístico      Gestiona la operación
                                                  física y los despachos.

  SUPERVISOR              Supervisor              Realiza consultas y
                                                  seguimiento operativo.
  -----------------------------------------------------------------------

### Uso

``` text
User
   │
   └── rol : UserRole
```

## 4. UserStatus

### Descripción

`UserStatus` representa la condición operativa del usuario.

La especificación contempla estados como:

``` text
ACTIVE
BLOCKED
```

### Uso

``` text
User
   │
   └── status : UserStatus
```

## 5. BuyerStatus

### Descripción

`BuyerStatus` representa la situación comercial del comprador.

### Uso

``` text
Buyer
   │
   └── commercialStatus : BuyerStatus
```

La especificación establece que este estado es obligatorio, pero no
proporciona un catálogo detallado de valores. Por ello no se inventan
valores adicionales.

## 6. SellerStatus

### Descripción

`SellerStatus` representa la situación actual del vendedor dentro de
NexusMarket.

### Uso

``` text
Seller
   │
   └── status : SellerStatus
```

La especificación contempla el estado del vendedor, pero no define un
catálogo completo de valores.

## 7. ProductType

### Descripción

`ProductType` diferencia los productos según la forma en que se
entregan.

### Valores

  Código    Nombre    Descripción
  --------- --------- -------------------------------------------
  PHYSICAL    Físico    Requiere inventario y despacho.
  DIGITAL   Digital   Tiene entrega inmediata después del pago.

### Uso

``` text
Product
   │
   └── productType : ProductType
```

## 8. ProductStatus

### Descripción

`ProductStatus` representa la situación del producto dentro del
catálogo.

### Valores

  -----------------------------------------------------------------------
  Código                  Nombre                  Descripción
  ----------------------- ----------------------- -----------------------
  PUBLISHED               Publicado               Producto disponible en
                                                  el catálogo.

  SUSPENDED              Suspendido              Producto temporalmente
                                                  suspendido.

  DISCONTINUED           Descontinuado           Producto que dejó de
                                                  estar disponible.
  -----------------------------------------------------------------------

### Uso

``` text
Product
   │
   └── status : ProductStatus
```

## 9. ProductVariant

### Descripción

`ProductVariant` representa una característica que diferencia una
presentación de un producto.

Puede representar:

``` text
Color
Talla
Modelo
```

### Ejemplo

``` text
ProductVariant
├── sku = "CAM-M-AZ"
├── attribute = "Color"
└── value = "Negro"
```

Otro ejemplo:

``` text
ProductVariant
├── sku = "CAM-L-AZ"
├── attribute = "Talla"
└── value = "M"
```

### Uso

``` text
Product
   │
   └── variantes : List<ProductVariant>
```

La estructura definitiva podrá ajustarse si los requisitos detallan cómo
las variantes afectan el inventario.

## 10. WarehouseType

### Descripción

`WarehouseType` identifica el tipo de bodega utilizada.

### Valores

``` text
MARKETPLACE
SELLER
```

### Uso

``` text
Warehouse
   │
   └── type : WarehouseType
```

## 11. InventoryMovementType

### Descripción

Representa el tipo de operación realizada sobre las existencias.

Es diferente de un estado porque representa una operación o movimiento.

### Valores

  Código         Nombre             Descripción
  -------------- ------------------ ----------------------------
  INBOUND        Ingreso            Entrada de existencias.
  RESERVATION        Reserva            Separación de existencias.
  RELEASE     Liberación         Una reserva vuelve a disponible.
  SALE_OUTBOUND   Salida por venta   Disminución por una venta.
  ADJUSTMENT         Ajuste             Corrección de existencias.
  MARK_DAMAGED Marcado dañado     Existencias disponibles pasan a dañadas.
  RETURN     Devolución         Reingreso de existencias.

### Uso

``` text
InventoryMovement
   │
   └── type : InventoryMovementType
```

## 12. CartStatus

### Descripción

`CartStatus` representa la situación actual del carrito.

### Valores conceptuales

``` text
ACTIVE
CONFIRMED
ABANDONED
```

El catálogo definitivo debe mantenerse alineado con las reglas
funcionales que se establezcan para el carrito.

### Uso

``` text
Cart
   │
   └── status : CartStatus
```

## 13. OrderStatus

### Descripción

`OrderStatus` representa la etapa del pedido dentro de su ciclo
comercial.

### Valores

``` text
PENDING_PAYMENT
PAID
SHIPPED
DELIVERED
COMPLETED
CANCELLED
```

### Ciclo

``` text
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

El enunciado lista "Carrito" como primer estado del pedido, pero en el código no existe un
valor `CART` en `OrderStatus`: el carrito es el agregado independiente `Cart` (con su propio
`CartStatus`) que precede a la creación del `Order`.

### Regla

``` text
COMPLETED → no puede modificarse
CANCELLED  → no puede modificarse
```

## 14. ShipmentStatus

### Descripción

`ShipmentStatus` representa la situación del proceso logístico.

El proceso contempla preparación, despacho, transporte y confirmación de
entrega.

### Uso

``` text
Shipment
   │
   └── status : ShipmentStatus
```

El catálogo exacto de estados debe mantenerse alineado con las reglas
funcionales de logística.

## 15. ReturnStatus

### Descripción

`ReturnStatus` representa la situación de una devolución y permite
realizar seguimiento desde la solicitud hasta su finalización.

### Uso

``` text
Return
   │
   └── status : ReturnStatus
```

La especificación contempla devoluciones, pero no proporciona un
catálogo detallado de estados. Por esta razón no se inventan valores.

## 16. RefundStatus

### Descripción

`RefundStatus` representa la situación de un reembolso asociado a una
devolución.

### Uso

``` text
Refund
   │
   └── status : RefundStatus
```

La especificación incluye reembolsos, pero no define un catálogo
detallado de estados. Los valores concretos quedan pendientes de
definición funcional.

## 17. Address

### Descripción

`Address` representa la información necesaria para identificar un
lugar de entrega.

### Estructura conceptual

  Atributo   Tipo     Descripción
  ---------- -------- ------------------------------
  type       AddressType   Propósito de la dirección.
  detail     String          Información de la dirección.
  city       String          Ciudad de la dirección.

### Uso

``` text
Buyer
├── primaryAddress : Address
└── additionalAddresses : List<Address>

Shipment
└── deliveryAddress : Address
```

## 18. Money

### Descripción

`Money` representa un valor monetario dentro del dominio.

Permite evitar que conceptos monetarios importantes sean manejados
únicamente como números sin significado de negocio.

### Estructura conceptual

  Atributo   Tipo      Descripción
  ---------- --------- -------------------
  amount     Decimal   Valor monetario (no negativo).
  currency   Currency   Moneda utilizada.

### Uso

``` text
Product
└── currentPrice : Money

CartItem
└── unitPrice : Money

OrderLine
└── unitPrice : Money

Order
└── total : Money

Invoice
├── subtotal : Money
└── total : Money

Refund
└── amount : Money
```

## 18.1 Valores adoptados en la implementación

La especificación funcional no define estos catálogos; se adoptaron los
siguientes valores mínimos en el código y **deben confirmarse** con el
docente o la especificación:

| Value Object | Valores |
|---|---|
| UserStatus | `ACTIVE`, `BLOCKED` |
| BuyerStatus | `ENABLED`, `RESTRICTED` |
| SellerStatus | `ACTIVE`, `SUSPENDED` |
| WarehouseStatus | `ACTIVE`, `INACTIVE` |
| ShipmentStatus | `PENDING`, `PREPARING`, `SHIPPED`, `DELIVERED` |
| ReturnStatus | `REQUESTED`, `APPROVED`, `REJECTED`, `RECEIVED` |
| RefundStatus | `PENDING`, `APPROVED`, `PAID`, `REJECTED` |
| AddressType | `HOME`, `WORK`, `OTHER` |
| Currency | `COP`, `USD`, `EUR` |

## 18.2 Value Objects de identificación y formato

Reemplazan `String` abiertos por tipos con validación propia.

| Value Object | Código Java | Regla |
|---|---|---|
| Currency | `Currency` (enum) | Solo monedas soportadas. |
| Email | `Email` | Obligatorio, formato `x@dominio.tld`, se normaliza a minúsculas. La unicidad la garantiza el repositorio. |
| IdentityDocument | `IdentityDocument` | Obligatorio, sin espacios sobrantes. La unicidad la garantiza el repositorio. |
| Sku | `Sku` | Obligatorio, se normaliza a mayúsculas. Identifica una variante. |
| InvoiceNumber | `InvoiceNumber` | Obligatorio. |
| Quantity | `Quantity` | Entero no negativo (`ZERO`, `positive(n)`, `plus`, `minus`). Reemplaza al `int` en `Inventory`, `InventoryMovement`, `CartItem`, `OrderLine` e `InventoryAllocation`. |
| AuditEntityType | `AuditEntityType` (enum) | Entidad a la que se refiere un `AuditLog`: `BUYER`, `CART`, `INVENTORY`, `INVOICE`, `ORDER`, `PRODUCT`, `RETURN`, `SELLER`, `SHIPMENT`, `USER`, `WAREHOUSE`. |

## 19. Relación con las entidades

``` text
User
├── role : UserRole
└── status : UserStatus

Buyer
├── commercialStatus : BuyerStatus
├── primaryAddress : Address
└── additionalAddresses : List<Address>

Seller
└── status : SellerStatus

Product
├── productType : ProductType
├── variants : List<ProductVariant>
├── status : ProductStatus
└── currentPrice : Money

Warehouse
└── type : WarehouseType

InventoryMovement
└── type : InventoryMovementType

Cart
└── status : CartStatus

Order
├── status : OrderStatus
└── total : Money

OrderLine
└── unitPrice : Money

CartItem
└── unitPrice : Money

Shipment
├── status : ShipmentStatus
└── deliveryAddress : Address

Return
└── status : ReturnStatus

Refund
├── status : RefundStatus
└── amount : Money

Invoice
├── subtotal : Money
└── total : Money
```

## 20. Diferencia entre Entidad y Value Object

### Entidad

Una entidad posee identidad propia.

Ejemplo:

``` text
Order
orderId = 1001
```

Aunque cambie su estado, continúa siendo el mismo pedido.

### Value Object

Un Value Object se identifica por sus valores.

Ejemplo:

``` text
Money
amount = 50000
currency = COP
```

Lo importante es el valor que representa y no un identificador propio.

## 21. Diferencia entre estado y movimiento

### Estado

Representa la situación actual:

``` text
Order
status = PAID
```

### InventoryMovement

Representa una operación:

``` text
InventoryMovement
type = SALE_OUTBOUND
```

Por esta razón no existe un `InventoryStatus` para representar los
movimientos `INBOUND`, `RESERVATION`, `SALE_OUTBOUND`, `ADJUSTMENT` y
`RETURN`.

## 22. Reglas de los Value Objects

### Valores controlados

Los valores que tienen un catálogo definido deben mantenerse
consistentes.

Ejemplo:

``` text
PUBLISHED
```

debe representar siempre el estado publicado de un producto.

### Significado

Cada valor debe tener un significado claro dentro del negocio.

Por ejemplo:

``` text
PHYSICAL
```

representa un producto que requiere inventario y despacho.

Mientras:

``` text
DIGITAL
```

representa un producto cuya entrega es inmediata después del pago.

### Inmutabilidad conceptual

Los Value Objects deben tratarse como valores y no como objetos con
identidad independiente.

## 23. Resumen

Los Value Objects definidos para NexusMarket son:

``` text
UserRole
UserStatus
BuyerStatus
SellerStatus
ProductType
ProductStatus
ProductVariant
WarehouseType
InventoryMovementType
CartStatus
OrderStatus
ShipmentStatus
ReturnStatus
RefundStatus
AddressType
Address
Currency
Money
Email
IdentityDocument
Sku
InvoiceNumber
Quantity
AuditEntityType
```

Estos objetos permiten representar conceptos del negocio de forma
explícita y reducen el uso de tipos genéricos como `String` y `double`
cuando existe un significado de dominio específico.

## 24. Permission

Enum que lista cada operación que un usuario puede ejecutar y los roles que la
tienen permitida (código: `Permission`). La matriz completa está en
`Domain Services.md`, sección 14.3. Se evalúa en `ValidateUserPermissionsService`.
