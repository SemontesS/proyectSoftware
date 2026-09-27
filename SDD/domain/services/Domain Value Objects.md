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
├── RolUsuario
├── EstadoUsuario
├── EstadoComprador
├── EstadoVendedor
├── TipoProducto
├── EstadoProducto
├── VarianteProducto
├── TipoBodega
├── TipoMovimientoInventario
├── EstadoCarrito
├── EstadoPedido
├── EstadoEnvio
├── EstadoDevolucion
├── EstadoReembolso
├── Direccion
└── Dinero
```

## 3. RolUsuario

### Descripción

`RolUsuario` representa la responsabilidad que tiene un usuario dentro
de NexusMarket.

Cada usuario posee un único rol.

### Valores definidos

  -----------------------------------------------------------------------
  Código                  Nombre                  Responsabilidad
  ----------------------- ----------------------- -----------------------
  COMPRADOR               Comprador               Adquiere productos
                                                  publicados.

  VENDEDOR                Vendedor                Registra y administra
                                                  productos.

  ADMINISTRADOR           Administrador           Administra vendedores y
                                                  bodegas.

  OPERADOR_LOGISTICO      Operador Logístico      Gestiona la operación
                                                  física y los despachos.

  SUPERVISOR              Supervisor              Realiza consultas y
                                                  seguimiento operativo.
  -----------------------------------------------------------------------

### Uso

``` text
Usuario
   │
   └── rol : RolUsuario
```

## 4. EstadoUsuario

### Descripción

`EstadoUsuario` representa la condición operativa del usuario.

La especificación contempla estados como:

``` text
ACTIVO
BLOQUEADO
```

### Uso

``` text
Usuario
   │
   └── estado : EstadoUsuario
```

## 5. EstadoComprador

### Descripción

`EstadoComprador` representa la situación comercial del comprador.

### Uso

``` text
Comprador
   │
   └── estadoComercial : EstadoComprador
```

La especificación establece que este estado es obligatorio, pero no
proporciona un catálogo detallado de valores. Por ello no se inventan
valores adicionales.

## 6. EstadoVendedor

### Descripción

`EstadoVendedor` representa la situación actual del vendedor dentro de
NexusMarket.

### Uso

``` text
Vendedor
   │
   └── estado : EstadoVendedor
```

La especificación contempla el estado del vendedor, pero no define un
catálogo completo de valores.

## 7. TipoProducto

### Descripción

`TipoProducto` diferencia los productos según la forma en que se
entregan.

### Valores

  Código    Nombre    Descripción
  --------- --------- -------------------------------------------
  FISICO    Físico    Requiere inventario y despacho.
  DIGITAL   Digital   Tiene entrega inmediata después del pago.

### Uso

``` text
Producto
   │
   └── tipoProducto : TipoProducto
```

## 8. EstadoProducto

### Descripción

`EstadoProducto` representa la situación del producto dentro del
catálogo.

### Valores

  -----------------------------------------------------------------------
  Código                  Nombre                  Descripción
  ----------------------- ----------------------- -----------------------
  PUBLICADO               Publicado               Producto disponible en
                                                  el catálogo.

  SUSPENDIDO              Suspendido              Producto temporalmente
                                                  suspendido.

  DESCONTINUADO           Descontinuado           Producto que dejó de
                                                  estar disponible.
  -----------------------------------------------------------------------

### Uso

``` text
Producto
   │
   └── estado : EstadoProducto
```

## 9. VarianteProducto

### Descripción

`VarianteProducto` representa una característica que diferencia una
presentación de un producto.

Puede representar:

``` text
Color
Talla
Modelo
```

### Ejemplo

``` text
VarianteProducto
├── atributo = "Color"
└── valor = "Negro"
```

Otro ejemplo:

``` text
VarianteProducto
├── atributo = "Talla"
└── valor = "M"
```

### Uso

``` text
Producto
   │
   └── variantes : List<VarianteProducto>
```

La estructura definitiva podrá ajustarse si los requisitos detallan cómo
las variantes afectan el inventario.

## 10. TipoBodega

### Descripción

`TipoBodega` identifica el tipo de bodega utilizada.

### Valores

``` text
MARKETPLACE
VENDEDOR
```

### Uso

``` text
Bodega
   │
   └── tipo : TipoBodega
```

## 11. TipoMovimientoInventario

### Descripción

Representa el tipo de operación realizada sobre las existencias.

Es diferente de un estado porque representa una operación o movimiento.

### Valores

  Código         Nombre             Descripción
  -------------- ------------------ ----------------------------
  INGRESO        Ingreso            Entrada de existencias.
  RESERVA        Reserva            Separación de existencias.
  SALIDA_VENTA   Salida por venta   Disminución por una venta.
  AJUSTE         Ajuste             Corrección de existencias.
  DEVOLUCION     Devolución         Reingreso de existencias.

### Uso

``` text
MovimientoInventario
   │
   └── tipo : TipoMovimientoInventario
```

## 12. EstadoCarrito

### Descripción

`EstadoCarrito` representa la situación actual del carrito.

### Valores conceptuales

``` text
ACTIVO
CONFIRMADO
ABANDONADO
```

El catálogo definitivo debe mantenerse alineado con las reglas
funcionales que se establezcan para el carrito.

### Uso

``` text
Carrito
   │
   └── estado : EstadoCarrito
```

## 13. EstadoPedido

### Descripción

`EstadoPedido` representa la etapa del pedido dentro de su ciclo
comercial.

### Valores

``` text
PENDIENTE_PAGO
PAGADO
DESPACHADO
ENTREGADO
FINALIZADO
```

### Ciclo

``` text
PENDIENTE_PAGO
      ↓
   PAGADO
      ↓
 DESPACHADO
      ↓
  ENTREGADO
      ↓
  FINALIZADO
```

`CARRITO` no pertenece a `EstadoPedido`, porque el carrito es un
concepto independiente que precede a la creación del pedido.

### Regla

``` text
FINALIZADO → no puede modificarse
```

## 14. EstadoEnvio

### Descripción

`EstadoEnvio` representa la situación del proceso logístico.

El proceso contempla preparación, despacho, transporte y confirmación de
entrega.

### Uso

``` text
Envio
   │
   └── estado : EstadoEnvio
```

El catálogo exacto de estados debe mantenerse alineado con las reglas
funcionales de logística.

## 15. EstadoDevolucion

### Descripción

`EstadoDevolucion` representa la situación de una devolución y permite
realizar seguimiento desde la solicitud hasta su finalización.

### Uso

``` text
Devolucion
   │
   └── estado : EstadoDevolucion
```

La especificación contempla devoluciones, pero no proporciona un
catálogo detallado de estados. Por esta razón no se inventan valores.

## 16. EstadoReembolso

### Descripción

`EstadoReembolso` representa la situación de un reembolso asociado a una
devolución.

### Uso

``` text
Reembolso
   │
   └── estado : EstadoReembolso
```

La especificación incluye reembolsos, pero no define un catálogo
detallado de estados. Los valores concretos quedan pendientes de
definición funcional.

## 17. Direccion

### Descripción

`Direccion` representa la información necesaria para identificar un
lugar de entrega.

### Estructura conceptual

  Atributo   Tipo     Descripción
  ---------- -------- ------------------------------
  tipo       String   Tipo de dirección.
  detalle    String   Información de la dirección.
  ciudad     String   Ciudad de la dirección.

### Uso

``` text
Comprador
├── direccionPrincipal : Direccion
└── direccionesAdicionales : List<Direccion>

Envio
└── direccionEntrega : Direccion
```

## 18. Dinero

### Descripción

`Dinero` representa un valor monetario dentro del dominio.

Permite evitar que conceptos monetarios importantes sean manejados
únicamente como números sin significado de negocio.

### Estructura conceptual

  Atributo   Tipo      Descripción
  ---------- --------- -------------------
  monto      Decimal   Valor monetario.
  moneda     String    Moneda utilizada.

### Uso

``` text
Producto
└── precioActual : Dinero

ItemCarrito
└── precioUnitario : Dinero

LineaPedido
└── precioUnitario : Dinero

Pedido
└── total : Dinero

Factura
├── subtotal : Dinero
└── total : Dinero

Reembolso
└── monto : Dinero
```

## 19. Relación con las entidades

``` text
Usuario
├── rol : RolUsuario
└── estado : EstadoUsuario

Comprador
├── estadoComercial : EstadoComprador
├── direccionPrincipal : Direccion
└── direccionesAdicionales : List<Direccion>

Vendedor
└── estado : EstadoVendedor

Producto
├── tipoProducto : TipoProducto
├── variantes : List<VarianteProducto>
├── estado : EstadoProducto
└── precioActual : Dinero

Bodega
└── tipo : TipoBodega

MovimientoInventario
└── tipo : TipoMovimientoInventario

Carrito
└── estado : EstadoCarrito

Pedido
├── estado : EstadoPedido
└── total : Dinero

LineaPedido
└── precioUnitario : Dinero

ItemCarrito
└── precioUnitario : Dinero

Envio
├── estado : EstadoEnvio
└── direccionEntrega : Direccion

Devolucion
└── estado : EstadoDevolucion

Reembolso
├── estado : EstadoReembolso
└── monto : Dinero

Factura
├── subtotal : Dinero
└── total : Dinero
```

## 20. Diferencia entre Entidad y Value Object

### Entidad

Una entidad posee identidad propia.

Ejemplo:

``` text
Pedido
idPedido = 1001
```

Aunque cambie su estado, continúa siendo el mismo pedido.

### Value Object

Un Value Object se identifica por sus valores.

Ejemplo:

``` text
Dinero
monto = 50000
moneda = COP
```

Lo importante es el valor que representa y no un identificador propio.

## 21. Diferencia entre estado y movimiento

### Estado

Representa la situación actual:

``` text
Pedido
estado = PAGADO
```

### Movimiento

Representa una operación:

``` text
MovimientoInventario
tipo = SALIDA_VENTA
```

Por esta razón, `EstadoInventario` no se utiliza para representar los
movimientos `INGRESO`, `RESERVA`, `SALIDA_VENTA`, `AJUSTE` y
`DEVOLUCION`.

## 22. Reglas de los Value Objects

### Valores controlados

Los valores que tienen un catálogo definido deben mantenerse
consistentes.

Ejemplo:

``` text
PUBLICADO
```

debe representar siempre el estado publicado de un producto.

### Significado

Cada valor debe tener un significado claro dentro del negocio.

Por ejemplo:

``` text
FISICO
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
RolUsuario
EstadoUsuario
EstadoComprador
EstadoVendedor
TipoProducto
EstadoProducto
VarianteProducto
TipoBodega
TipoMovimientoInventario
EstadoCarrito
EstadoPedido
EstadoEnvio
EstadoDevolucion
EstadoReembolso
Direccion
Dinero
```

Estos objetos permiten representar conceptos del negocio de forma
explícita y reducen el uso de tipos genéricos como `String` y `double`
cuando existe un significado de dominio específico.
