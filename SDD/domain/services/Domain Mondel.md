# Domain Model --- NexusMarket

## 1. Introducción

El Modelo de Dominio de **NexusMarket** representa los principales
conceptos del negocio que participan en la operación de la plataforma y
las relaciones existentes entre ellos.

NexusMarket funciona como un marketplace que actúa como intermediario
entre compradores y vendedores. La plataforma permite gestionar
usuarios, vendedores, compradores, productos, bodegas, inventario,
carritos, pedidos, facturación, envíos, devoluciones y reembolsos.

El modelo se construye utilizando conceptos de **Domain-Driven Design
(DDD)**, buscando que las entidades y objetos representen conceptos
reales del negocio y que las reglas importantes sean protegidas dentro
del dominio.

## 2. Principales conceptos del dominio

-   Usuario
-   Comprador
-   Vendedor
-   Producto
-   VarianteProducto
-   Bodega
-   Inventario
-   MovimientoInventario
-   Carrito
-   ItemCarrito
-   Pedido
-   LineaPedido
-   Factura
-   Envio
-   Devolucion
-   Reembolso

## 3. Clasificación DDD

### Entidades

-   Usuario
-   Comprador
-   Vendedor
-   Producto
-   Bodega
-   Inventario
-   Carrito
-   Pedido
-   Factura
-   Envio
-   Devolucion
-   Reembolso

### Elementos internos de agregados

-   ItemCarrito
-   LineaPedido
-   MovimientoInventario

### Value Objects

-   RolUsuario
-   EstadoUsuario
-   EstadoComprador
-   EstadoVendedor
-   TipoProducto
-   EstadoProducto
-   VarianteProducto
-   TipoBodega
-   TipoMovimientoInventario
-   EstadoCarrito
-   EstadoPedido
-   EstadoEnvio
-   EstadoDevolucion
-   EstadoReembolso
-   Direccion
-   Dinero

## 4. Usuario

### Descripción

`Usuario` representa a una persona que participa en la plataforma.
Contiene la información común necesaria para identificar al participante
y determinar su rol y estado dentro de NexusMarket.

### Atributos

  Atributo             Tipo conceptual   Descripción
  -------------------- ----------------- --------------------------------------------
  idUsuario            Identificador     Identifica de forma única al usuario.
  nombre               String            Nombre completo del usuario.
  correoElectronico    String            Correo electrónico del usuario.
  documentoIdentidad   String            Documento de identificación.
  rol                  RolUsuario        Rol que desempeña dentro de la plataforma.
  estado               EstadoUsuario     Estado operativo del usuario.

### Reglas

-   El identificador del usuario debe ser único.
-   El correo electrónico debe ser único.
-   El documento de identidad debe ser único.
-   Cada usuario tiene un único rol.
-   El usuario opera de acuerdo con las responsabilidades de su rol.

## 5. Comprador

### Descripción

`Comprador` representa al participante que adquiere productos dentro de
NexusMarket.

### Atributos

  ---------------------------------------------------------------------------
  Atributo                 Tipo conceptual            Descripción
  ------------------------ -------------------------- -----------------------
  direccionPrincipal       Direccion                  Dirección principal
                                                      utilizada para las
                                                      entregas.

  direccionesAdicionales   List`<Direccion>`{=html}   Direcciones adicionales
                                                      registradas.

  estadoComercial          EstadoComprador            Situación comercial del
                                                      comprador.
  ---------------------------------------------------------------------------

### Relaciones

``` text
Comprador
   │
   ├── utiliza ──> Carrito
   │
   └── realiza ──> Pedido
```

### Reglas

-   El comprador debe contar con una dirección principal.
-   Puede registrar direcciones adicionales.
-   Puede utilizar un carrito activo.
-   Puede realizar pedidos.

## 6. Vendedor

### Descripción

`Vendedor` representa al participante encargado de registrar y
administrar los productos que comercializa dentro de NexusMarket.

### Atributos

  Atributo         Tipo conceptual   Descripción
  ---------------- ----------------- --------------------------------
  estadoVendedor   EstadoVendedor    Situación actual del vendedor.

### Relaciones

``` text
Vendedor
   │
   ├── publica ──> Producto
   │
   └── trabaja con ──> Bodega
```

### Reglas

-   El vendedor debe ser incorporado por el Administrador.
-   Puede registrar productos.
-   Puede administrar los productos que comercializa.
-   Puede trabajar con las bodegas asociadas.

## 7. Producto

### Descripción

`Producto` representa un bien físico o digital que puede ser ofrecido
dentro del catálogo de NexusMarket.

### Atributos

  ---------------------------------------------------------------------------------
  Atributo                Tipo conceptual                   Descripción
  ----------------------- --------------------------------- -----------------------
  idProducto              Identificador                     Identifica el producto.

  nombreProducto          String                            Nombre del producto.

  tipoProducto            TipoProducto                      Indica si es físico o
                                                            digital.

  variantes               List`<VarianteProducto>`{=html}   Características que
                                                            diferencian las
                                                            presentaciones.

  estado                  EstadoProducto                    Estado comercial dentro
                                                            del catálogo.

  precioActual            Dinero                            Precio actual del
                                                            producto.
  ---------------------------------------------------------------------------------

### Relaciones

``` text
Vendedor
   │
   └── publica ──> Producto

Producto
   │
   ├── puede tener ──> VarianteProducto
   │
   └── puede estar disponible en ──> Inventario
```

### Reglas

-   Puede ser físico o digital.
-   Puede tener variantes.
-   Tiene un estado dentro del catálogo.
-   Los productos físicos requieren inventario y despacho.
-   Los productos digitales tienen entrega inmediata después del pago.

## 8. VarianteProducto

### Descripción

`VarianteProducto` representa una característica que diferencia una
presentación de un producto.

Ejemplos:

``` text
Color = Negro
Talla = M
Modelo = 2026
```

Se mantiene como concepto asociado al producto. Su estructura definitiva
podrá ajustarse si los requisitos detallan cómo las variantes afectan el
inventario.

## 9. Bodega

### Descripción

`Bodega` representa un lugar físico donde se almacenan productos.

### Atributos

  Atributo    Tipo conceptual   Descripción
  ----------- ----------------- -----------------------------
  idBodega    Identificador     Identificador único.
  ubicacion   String            Ubicación de la bodega.
  tipo        TipoBodega        Clasificación de la bodega.

### Relaciones

``` text
Bodega
   │
   ├── almacena ──> Inventario
   │
   └── puede despachar ──> Envio
```

## 10. Inventario

### Descripción

`Inventario` representa las existencias de un producto dentro de una
bodega específica.

### Atributos

  Atributo             Tipo conceptual   Descripción
  -------------------- ----------------- -------------------------------
  idInventario         Identificador     Identificador del inventario.
  cantidadDisponible   Cantidad          Existencias disponibles.
  producto             Producto          Producto al que corresponde.
  bodega               Bodega            Bodega donde se almacena.

### Regla principal

``` text
cantidadDisponible >= 0
```

No se permiten existencias negativas.

## 11. MovimientoInventario

### Descripción

`MovimientoInventario` representa una operación que modifica o registra
el comportamiento de las existencias.

### Tipos

``` text
INGRESO
RESERVA
SALIDA_VENTA
AJUSTE
DEVOLUCION
```

### Relación

``` text
Inventario
   │
   └── registra ──> MovimientoInventario
```

## 12. Carrito

### Descripción

`Carrito` representa el espacio temporal donde el comprador selecciona
productos antes de confirmar la compra.

### Atributos

  Atributo        Tipo conceptual   Descripción
  --------------- ----------------- ----------------------------
  idCarrito       Identificador     Identificador del carrito.
  fechaCreacion   Fecha             Fecha de creación.
  total           Dinero            Valor acumulado.
  estado          EstadoCarrito     Estado actual.

### Relaciones

``` text
Comprador
    │
    └── utiliza ──> Carrito
                       │
                       └── contiene ──> ItemCarrito
```

### Regla

Un comprador puede tener cero o un carrito activo.

## 13. ItemCarrito

### Descripción

`ItemCarrito` representa un producto seleccionado dentro de un carrito y
es un elemento interno del agregado `Carrito`.

### Atributos

  Atributo         Tipo conceptual   Descripción
  ---------------- ----------------- -------------------------------
  cantidad         Cantidad          Unidades seleccionadas.
  precioUnitario   Dinero            Precio aplicado al agregarlo.
  producto         Producto          Producto seleccionado.

## 14. Pedido

### Descripción

`Pedido` representa el compromiso comercial generado después de que el
comprador confirma su carrito.

### Atributos

  Atributo        Tipo conceptual              Descripción
  --------------- ---------------------------- ---------------------------
  idPedido        Identificador                Identificador del pedido.
  fechaCreacion   Fecha                        Fecha de creación.
  estado          EstadoPedido                 Estado actual.
  total           Dinero                       Valor total.
  lineas          List`<LineaPedido>`{=html}   Productos incluidos.

### Relaciones

``` text
Comprador
   │
   └── realiza ──> Pedido
                       │
                       └── contiene ──> LineaPedido
                                             │
                                             └── corresponde ──> Producto
```

### Ciclo de vida

``` text
Carrito
   │
   │ confirmar
   ↓
Pedido
   ↓
PENDIENTE_PAGO
   ↓
PAGADO
   ↓
DESPACHADO
   ↓
ENTREGADO / FINALIZADO
```

`Carrito` no es un estado de `Pedido`; es un concepto independiente que
precede a la creación del pedido.

### Regla

Un pedido finalizado no puede modificarse.

## 15. LineaPedido

### Descripción

`LineaPedido` representa cada producto incluido en un pedido y es un
elemento interno del agregado `Pedido`.

### Atributos

  Atributo         Tipo conceptual   Descripción
  ---------------- ----------------- ------------------------------------------
  cantidad         Cantidad          Unidades solicitadas.
  precioUnitario   Dinero            Precio aplicado al momento de la compra.
  producto         Producto          Producto adquirido.

## 16. Factura

### Descripción

`Factura` representa la información comercial generada como resultado
del pedido.

### Atributos

  Atributo    Tipo conceptual   Descripción
  ----------- ----------------- ------------------------------
  idFactura   Identificador     Identificador de la factura.
  numero      String            Número de factura.
  fecha       Fecha             Fecha de emisión.
  subtotal    Dinero            Valor antes del total final.
  total       Dinero            Valor final.

### Relación

``` text
Pedido
   │
   └── genera ──> Factura
```

## 17. Envio

### Descripción

`Envio` representa el proceso logístico mediante el cual un pedido es
preparado, despachado, transportado y entregado.

### Atributos

  Atributo           Tipo conceptual   Descripción
  ------------------ ----------------- --------------------------
  idEnvio            Identificador     Identificador del envío.
  estado             EstadoEnvio       Estado logístico.
  direccionEntrega   Direccion         Dirección de entrega.

### Relaciones

``` text
Pedido
   │
   └── requiere ──> Envio
                       │
                       └── es despachado por ──> Bodega
```

## 18. Devolucion

### Descripción

`Devolucion` representa el proceso mediante el cual un comprador
solicita devolver un pedido o parte de él de acuerdo con las condiciones
del negocio.

### Atributos

  Atributo       Tipo conceptual    Descripción
  -------------- ------------------ --------------------------
  idDevolucion   Identificador      Identificador.
  motivo         String             Motivo de la devolución.
  fecha          Fecha              Fecha de solicitud.
  estado         EstadoDevolucion   Estado actual.

### Relación

``` text
Pedido
   │
   └── puede originar ──> Devolucion
                              │
                              └── puede generar ──> Reembolso
```

## 19. Reembolso

### Descripción

`Reembolso` representa la devolución del dinero asociada a una
devolución procesada.

### Atributos

  Atributo      Tipo conceptual   Descripción
  ------------- ----------------- ----------------------
  idReembolso   Identificador     Identificador.
  monto         Dinero            Valor reembolsado.
  fecha         Fecha             Fecha del reembolso.
  estado        EstadoReembolso   Estado actual.

### Regla

Una devolución puede generar como máximo un reembolso.

## 20. Agregados iniciales

``` text
Carrito
└── ItemCarrito

Pedido
└── LineaPedido

Inventario
└── MovimientoInventario
```

Aggregate Roots iniciales:

``` text
Carrito
Pedido
Inventario
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
-   Carrito activo.
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

### Inventario

-   Asociado a producto y bodega.
-   No permite existencias negativas.
-   Registra movimientos.

### Carrito

-   Cero o un carrito activo por comprador.
-   Contiene elementos.
-   Puede confirmarse para generar un pedido.

### Pedido

-   Contiene una o más líneas.
-   Tiene ciclo de vida.
-   Un pedido finalizado no puede modificarse.

### Devoluciones

-   Un pedido puede originar devoluciones.
-   Una devolución puede generar como máximo un reembolso.

## 22. Relaciones generales

``` text
                           Usuario
                              │
                ┌─────────────┴─────────────┐
                │                           │
           Comprador                     Vendedor
                │                           │
        ┌───────┴───────┐             ┌────┴─────┐
        │               │             │          │
     Carrito          Pedido       Producto    Bodega
        │               │             │          │
 ItemCarrito      LineaPedido         │     Inventario
        │               │             │          │
        └───────┬───────┘             │          │
                │                      └────┬─────┘
                │                           │
              Producto                 Movimiento
                                         Inventario

Pedido
 ├── Factura
 ├── Envio
 └── Devolucion
        │
        └── Reembolso
```

## 23. Ciclo general

``` text
Administrador
      │
      └── incorpora
             ↓
          Vendedor
             │
             ├── registra
             ↓
          Producto
             │
             ↓
         Inventario
             │
             ↓
           Bodega

Comprador
   │
   └── selecciona productos
          ↓
       Carrito
          │
          └── contiene
                ↓
           ItemCarrito
                │
                ↓
             Producto
                │
                │ confirmar
                ↓
              Pedido
                │
       ┌────────┼─────────┐
       ↓        ↓         ↓
    Factura    Envio   Devolucion
                         │
                         ↓
                     Reembolso
```

## 24. Resumen

El modelo de dominio de NexusMarket se organiza alrededor de usuarios,
compradores, vendedores, productos, bodegas, inventario, carritos,
pedidos y los procesos de facturación, logística y posventa.

Los agregados iniciales identificados son `Carrito`, `Pedido` e
`Inventario`, cada uno protegiendo sus elementos internos y reglas
principales.

Este modelo constituye la base para la implementación posterior de las
entidades, Value Objects, casos de uso y puertos de la Arquitectura
Hexagonal.
