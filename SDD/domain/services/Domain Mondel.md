# Domain Model 

## Introducción

El Modelo de Dominio de Ecommerce representa los principales elementos
que participan en la operación de la plataforma y la forma en que se
relacionan entre sí. El sistema funciona como un marketplace que conecta
compradores y vendedores, permitiendo gestionar productos, inventario,
carritos, pedidos, facturación, envíos, devoluciones y reembolsos.

El modelo permite visualizar la información principal del negocio y
entender cómo se desarrolla el proceso desde que un producto es
publicado hasta que una compra es entregada o, cuando corresponde, se
realiza una devolución y un reembolso.

La estructura se organiza alrededor de `Usuario`, `Producto`, `Bodega`,
`Inventario`, `Carrito` y `Pedido`, junto con los elementos que apoyan
el proceso comercial y logístico.

------------------------------------------------------------------------

# Jerarquía de clases de dominio

``` text
Usuario (Abstracto)
├── Comprador
└── Vendedor

Roles de Usuario:
├── Comprador
├── Vendedor
├── Administrador
├── Operador Logístico
└── Supervisor
```

`Usuario` concentra la información común de las personas que participan
en la plataforma. `Comprador` y `Vendedor` representan especializaciones
con información propia según la actividad que realizan dentro del
marketplace.

Los roles de Administrador, Operador Logístico y Supervisor representan
responsabilidades dentro del sistema y se encuentran definidos mediante
el atributo `rolUsuario`.

------------------------------------------------------------------------

# Relaciones de dominio

``` text
Usuario
   │
   ├── Comprador
   │      │
   │      ├── utiliza ─────────> Carrito
   │      │                         │
   │      │                         └── contiene ──> ItemCarrito
   │      │                                           │
   │      │                                           └── corresponde ──> Producto
   │      │
   │      └── realiza ─────────> Pedido
   │                                  │
   │                                  ├── contiene ──> LineaPedido
   │                                  │                    │
   │                                  │                    └── corresponde ──> Producto
   │                                  │
   │                                  ├── genera ─────> Factura
   │                                  ├── requiere ────> Envio
   │                                  └── puede originar ──> Devolucion
   │                                                           │
   │                                                           └── genera ──> Reembolso
   │
   └── Vendedor
          │
          ├── publica ──────> Producto
          │                     │
          │                     └── tiene ──> Inventario
          │
          └── tiene ────────> Bodega
                                  │
                                  ├── almacena ──> Inventario
                                  └── despacha ──> Envio
```

------------------------------------------------------------------------

# Entidades

## Usuario (Abstracto)

### Descripción

`Usuario` representa a las personas que interactúan con Ecommerce. Es
una clase abstracta porque reúne la información común que necesitan los
diferentes participantes de la plataforma.

La información del usuario permite identificarlo y conocer su rol y
estado dentro del sistema.

### Atributos

| Atributo   | Tipo   |Descripción                                              |
| ---------- | ------ | ------------------------------------------------------------ |
| idUsuario    | long   | Identifica de forma única al usuario.                        |
| name       | String | Nombre oficial del usuario.                                       |
| Correo electrónico| String | Medio principal de acceso y comunicación.                        |
| rolUsuario | String | Define las responsabilidades y permisos.                          |
| Estado | String | Condición operativa (Activo, Bloqueado, etc.). |

### Relaciones

-   Se especializa en `Comprador` y `Vendedor`.
-   El atributo `rolUsuario` permite determinar las responsabilidades
    del usuario.
-   Cada usuario tiene un único rol dentro del sistema.

------------------------------------------------------------------------

## Comprador

### Descripción

El `Comprador` representa a la persona que utiliza Ecommerce para
consultar productos, agregarlos al carrito y realizar compras.

También cuenta con información relacionada con sus direcciones y su
estado comercial.

### hereda de

`Usuario`

### Atributos

| Atributo            | Tipo           |Descripción                                  |
| ------------------- | -------------- | -------------------------------------------------- |
| direccionPrincipal  | String         | Ubicación habitual para entregas.      |
| direccionAdicional  | List\<String\> | Ubicaciones secundarias de entrega.     |
| estadoComprador     | String         | Condición del comprador para realizar compras.            |

### Relaciones

-   Utiliza cero o un `Carrito` activo.
-   Realiza cero o más `Pedido`.
-   Cada comprador puede manejar una dirección principal y direcciones
    adicionales.

------------------------------------------------------------------------

## Vendedor

### Descripción

El `Vendedor` representa a la persona responsable de registrar y
administrar los productos que comercializa dentro de Ecommerce.

Los vendedores son incorporados administrativamente y trabajan con las
bodegas asociadas a sus productos.

### hereda de

`Usuario`

### Atributos

| Atributo       | Tipo   |Descripción                          |
| -------------- | ------ | ------------------------------------------ |
| estadoVendedor | String | El estado de su cuenta como vendedor.      |

### Relaciones

-   Publica cero o más `Producto`.
-   Tiene cero o más `Bodega`.

------------------------------------------------------------------------

# Roles del sistema

Los participantes de Ecommerce trabajan de acuerdo con un rol
definido.

  -----------------------------------------------------------------------
  Rol                                 Responsabilidad principal
  ----------------------------------- -----------------------------------
  * **Comprador**                           Adquiere productos publicados en la
                                      plataforma.

  * **Vendedor**                            Registra y administra sus
                                      productos.

  * **Administrador**                       Administra vendedores y bodegas.

  * **Operador Logístico**                  Se encarga de la operación física
                                      de bodegas y despachos.

  * **Supervisor**                          Realiza consultas y seguimiento
                                      operativo.
  -----------------------------------------------------------------------

El rol se encuentra asociado al usuario mediante `rolUsuario`. Cada
usuario tiene un único rol y solamente puede interactuar con la
información relacionada con sus responsabilidades.

------------------------------------------------------------------------

## Producto

### Descripción

`Producto` representa los bienes físicos o digitales ofrecidos dentro de
Ecommerce. Contiene la información necesaria para identificarlo,
clasificarlo, establecer su precio y conocer su disponibilidad
comercial.

### Atributos

| Atributo       | Tipo        |Descripción                             |
| -------------- | ----------- | --------------------------------------------- |
| idProducto     | long        | Identifica de forma única al producto.        |
| nombreProducto | String      | El nombre con el que se muestra el producto.  |
| tipoProducto   | String      | La categoría a la que pertenece.              |
| variantes      | String/List | Las variantes disponibles (talla, color, etc). |
| estado         | String      | Si está activo, agotado, descontinuado, etc.  |
| precioActual   | double      | El precio al que se está vendiendo hoy.       |


### Relaciones

-   Es publicado por un `Vendedor`.
-   Puede estar relacionado con uno o más registros de `Inventario`.
-   Es referenciado por `ItemCarrito`.
-   Es referenciado por `LineaPedido`.

------------------------------------------------------------------------

## Bodega

### Descripción

`Bodega` representa el lugar donde se administra y almacena físicamente
el inventario de los productos.

Ecommerce contempla bodegas del marketplace y bodegas asociadas a
vendedores.

### Atributos

| Atributo        | Tipo   |Descripción                        |
| --------------- | ------ | ---------------------------------------- |
| idBodega        | long   | Identifica de forma única la bodega.    |
| ubicacionBodega | String | Dónde está ubicada.                     |
| tipoBodega      | String | Qué tipo de bodega es.                  |

### Relaciones

-   Puede estar asociada a un `Vendedor`.
-   Almacena cero o más `Inventario`.
-   Puede despachar cero o más `Envio`.

------------------------------------------------------------------------

## Inventario

### Descripción

`Inventario` representa las existencias disponibles de un producto en
una bodega determinada.

El inventario es distribuido, por lo que debe estar relacionado con un
producto y con una bodega específica.

### Atributos

| Atributo           | Tipo |Descripción                        |
| ------------------ | ---- | ---------------------------------------- |
| idInventario       | long | Identifica de forma única el registro.  |
| cantidadDisponible | int  | Cuántas unidades hay disponibles.       |

### Relaciones

-   Corresponde a un `Producto`.
-   Se almacena en una `Bodega`.

### Regla de negocio

No se permiten existencias negativas.

------------------------------------------------------------------------

## Carrito

### Descripción

`Carrito` representa el espacio donde el comprador reúne de manera
provisional los productos que desea adquirir antes de confirmar el
pedido.

Un comprador puede contar con un carrito activo en un momento
determinado.

### Atributos

| Atributo  | Tipo   |Descripción                       |
| --------- | ------ | ----------------------------------------- |
| idCarrito | long   | Identifica de forma única el carrito.    |
| fecha     | date   | Cuándo se creó.                          |
| total     | double | El valor total acumulado hasta el momento. |
| estado    | String | En qué estado está (activo, abandonado, etc). |

### Relaciones

-   Es utilizado por un `Comprador`.
-   Contiene cero o más `ItemCarrito`.
-   Puede confirmarse para generar un `Pedido`.

------------------------------------------------------------------------

## ItemCarrito

### Descripción

`ItemCarrito` representa cada producto que el comprador agrega al
carrito.

Permite almacenar la cantidad seleccionada y el precio unitario
correspondiente al momento de la selección.

### Atributos

| Atributo       | Tipo   |Descripción                             |
| -------------- | ------ | ---------------------------------------------- |
| idDetalle      | long   | Identifica de forma única el ítem.            |
| cantidad       | int    | Cuántas unidades se agregaron.                |
| precioUnitario | double | El precio del producto en el momento en que se agregó. |

### Relaciones

-   Pertenece a un `Carrito`.
-   Corresponde a un `Producto`.

------------------------------------------------------------------------

## Pedido

### Descripción

`Pedido` representa la compra que se genera cuando el comprador confirma
los productos seleccionados.

Es uno de los elementos principales del proceso comercial, ya que
conecta la compra con la facturación, el envío y los procesos de
devolución.

### Atributos

| Atributo      | Tipo   |Descripción                     |
| ------------- | ------ | -------------------------------------- |
| idPedido      | long   | Identifica de forma única el pedido.  |
| fechaCreacion | date   | Cuándo se creó el pedido.             |
| estado        | String | En qué estado está (pendiente, enviado, etc). |
| total         | double | El valor total del pedido.            |

### Relaciones

-   Se genera a partir de la confirmación de un `Carrito`.
-   Es realizado por un `Comprador`.
-   Contiene una o más `LineaPedido`.
-   Genera una `Factura`.
-   Puede requerir cero o un `Envio`.
-   Puede originar cero o más `Devolucion`.

### Ciclo de estados

``` text
Carrito
   ↓
Pendiente de Pago
   ↓
Pagado
   ↓
Despachado
   ↓
Entregado / Finalizado
```

------------------------------------------------------------------------

## LineaPedido

### Descripción

`LineaPedido` representa cada producto incluido dentro de un pedido
confirmado.

Permite conservar la cantidad adquirida y el precio unitario aplicado a
ese producto dentro del pedido.

### Atributos

| Atributo       | Tipo   |Descripción                          |
| -------------- | ------ | ---------------------------------------- |
| idLinea        | long   | Identifica de forma única la línea.     |
| cantidad       | int    | Cuántas unidades se pidieron.            |
| precioUnitario | double | El precio del producto al momento del pedido. |

### Relaciones

-   Pertenece a un `Pedido`.
-   Corresponde a un `Producto`.

------------------------------------------------------------------------

## Factura

### Descripción

`Factura` representa la información comercial generada a partir de un
pedido.

### Atributos

| Atributo  | Tipo   |Descripción                    |
| --------- | ------ | ------------------------------------ |
| idFactura | long   | Identifica de forma única la factura. |
| numero    | String | El número de la factura.             |
| fecha     | date   | Cuándo se emitió.                    |
| subTotal  | double | El valor antes de ajustes.           |
| total     | double | El valor final de la factura.        |

### Relaciones

-   Un `Pedido` genera una `Factura`.

------------------------------------------------------------------------

## Envio

### Descripción

`Envio` representa el proceso mediante el cual los productos de un
pedido son despachados desde una bodega hasta la dirección de entrega
indicada.

### Atributos

| Atributo           | Tipo   |Descripción                     |
| ------------------- | ------ | ------------------------------------- |
| idEnvio             | long   | Identifica de forma única el envío.  |
| estado              | String | En qué estado está (en camino, entregado, etc). |
| direccionDeEntrega  | String | A dónde se debe entregar.            |

### Relaciones

-   Un `Pedido` puede requerir cero o un `Envio`.
-   Una `Bodega` puede despachar cero o más `Envio`.

------------------------------------------------------------------------

## Devolucion

### Descripción

`Devolucion` representa el proceso mediante el cual un pedido puede ser
devuelto de acuerdo con las condiciones establecidas por el negocio.

### Atributos

| Atributo     | Tipo   |Descripción                       |
| ------------ | ------ | ----------------------------------------- |
| idDevolucion | long   | Identifica de forma única la devolución. |
| motivo       | String | Por qué se está devolviendo.             |
| fecha        | date   | Cuándo se solicitó.                      |
| estado       | String | En qué estado está la devolución.        |

### Relaciones

-   Un `Pedido` puede originar cero o más `Devolucion`.
-   Una `Devolucion` puede generar cero o un `Reembolso`.

------------------------------------------------------------------------

## Reembolso

### Descripción

`Reembolso` representa la devolución del dinero asociada con una
devolución aprobada o procesada.

### Atributos

| Atributo    | Tipo   |Descripción                     |
| ----------- | ------ | ------------------------------------- |
| idReembolso | long   | Identifica de forma única el reembolso. |
| monto       | double | Cuánto dinero se devolvió.            |
| fecha       | date   | Cuándo se hizo el reembolso.          |
| estado      | String | En qué estado está el reembolso.      |

### Relaciones

-   Una `Devolucion` puede generar cero o un `Reembolso`.

------------------------------------------------------------------------

# Reglas del dominio

## Usuarios

-   Cada usuario debe tener un identificador único.
-   El correo electrónico debe ser único.
-   El documento de identidad debe ser único.
-   Cada usuario tiene un único rol.
-   El usuario solamente puede interactuar con información relacionada
    con las funciones de su rol.

## Compradores

-   El comprador puede registrar una dirección principal.
-   Puede registrar direcciones adicionales.
-   El comprador puede utilizar un carrito para seleccionar productos.
-   El comprador puede realizar pedidos.

## Vendedores

-   Los vendedores son incorporados por el Administrador.
-   El vendedor registra y administra sus productos.
-   El vendedor puede trabajar con las bodegas asociadas.

## Productos

-   Los productos pueden ser físicos o digitales.
-   Los productos pueden tener variantes como color, talla o modelo.
-   Los productos tienen un estado dentro del catálogo.
-   Los productos físicos requieren inventario y despacho.
-   Los productos digitales tienen entrega inmediata después del pago.

## Inventario

-   El inventario está asociado a un producto y a una bodega.
-   No se permiten existencias negativas.
-   El inventario puede participar en movimientos de ingreso, reserva,
    salida por venta, ajuste y devolución.

## Carrito y pedido

-   Un comprador puede tener cero o un carrito activo.
-   Un carrito puede contener varios `ItemCarrito`.
-   Cada `ItemCarrito` corresponde a un producto.
-   Un carrito puede confirmarse para generar un pedido.
-   Un pedido contiene una o más líneas de pedido.
-   Un pedido finalizado no puede modificarse.

## Logística

-   Un pedido puede requerir un envío cuando corresponde.
-   La bodega realiza el despacho del pedido.
-   El envío contiene la dirección de entrega.
-   El estado del envío permite hacer seguimiento al proceso logístico.

## Devoluciones y reembolsos

-   Un pedido puede originar una o varias devoluciones.
-   Una devolución puede generar como máximo un reembolso.
-   El reembolso registra el monto, la fecha y el estado
    correspondiente.

------------------------------------------------------------------------

# Ciclo de vida del dominio

El proceso general de Ecommerce puede representarse de la siguiente
manera:

``` text
Vendedor
   │
   └── publica
          ↓
       Producto
          │
          └── disponible en
                 ↓
              Inventario
                 │
                 └── almacenado en
                        ↓
                      Bodega

Comprador
   │
   └── selecciona
          ↓
       Carrito
          │
          └── contiene
                 ↓
             ItemCarrito
                 │
                 └── corresponde
                        ↓
                     Producto
                        │
                        └── confirmación
                               ↓
                             Pedido
                               │
              ┌────────────────┼────────────────┐
              ↓                ↓                ↓
           Factura           Envio          Devolucion
                                                  │
                                                  ↓
                                             Reembolso
```

------------------------------------------------------------------------

# Resumen

Las principales clases del dominio de Ecommerce son: 

``` text
Usuario (Abstracto)
Comprador
Vendedor
Producto
Bodega
Inventario
Carrito
ItemCarrito
Pedido
LineaPedido
Factura
Envio
Devolucion
Reembolso
```

El modelo representa el flujo principal del marketplace: los vendedores
publican productos, estos se relacionan con el inventario disponible en
las bodegas, los compradores seleccionan productos mediante el carrito,
confirman sus pedidos y posteriormente se gestionan la facturación, el
envío y los procesos de devolución y reembolso cuando corresponda.
