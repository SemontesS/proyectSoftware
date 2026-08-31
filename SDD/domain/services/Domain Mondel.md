# Modelo de Dominio

## Introducción

NexusMarket es una plataforma digital centralizada diseñada para actuar como intermediario comercial entre compradores y vendedores. El propósito principal de la plataforma es administrar de manera integral todo el ciclo de la operación e-commerce: desde la incorporación de usuarios y el catálogo de productos hasta el control de existencias, la logística de envíos, la facturación y los servicios posventa (devoluciones y reembolsos), garantizando la trazabilidad y la correcta coordinación entre todos los participantes del ecosistema

Básicamente el modelo se divide en estos grupos:

* **Usuarios**, que son las personas que usan la plataforma, ya sea comprando o vendiendo.
* **Compradores y Vendedores**, que son las dos "caras" que puede tener un usuario según lo que hace en el sistema.
* **Productos e Inventario**, o sea, lo que se vende y cómo se controla cuánto queda disponible.
* **Carritos y Pedidos**, que son el camino que recorre una compra desde que el comprador empieza a agregar cosas hasta que confirma todo.
  
* **Postventa**, que agrupa todo lo que pasa después de comprar: la factura, el envío, y si algo sale mal, la devolución y el reembolso.

En resumen: un vendedor publica productos y los guarda en una o varias bodegas. Un comprador va llenando un carrito, y cuando confirma, ese carrito se convierte en un pedido. Ese pedido genera una factura, puede necesitar un envío, y si el comprador no queda conforme, puede terminar en una devolución (y de ahí, en un reembolso).

---

# Jerarquía de Clases

```text
usuario (Abstracta)
├── Comprador
└── Vendedor

Producto
Inventario
Bodega

Carrito
├── ItemCarrito

Pedido
├── LineaPedido

Factura
Envio
Devolucion
Reembolso
```

---

# Relaciones del Dominio

```text
usuario
   │
   ├── Comprador
   │
   └── Vendedor

Comprador
   │
   └── utiliza (1 → 0..1) ──────────────> Carrito

Carrito
   ├── contiene (1 → 0..*) ─────────────> ItemCarrito
   │                                          └── corresponde (0..* → 1) ──> Producto
   │
   └── confirma (1 → 0..1) ─────────────> Pedido

Pedido
   ├── Contiene (1 → 1..*) ──────────────> LineaPedido
   │                                          └── corresponde ─────────────> Producto
   │
   ├── corresponde (1 → 1) ──────────────> Producto
   ├── genera (1 → 1) ───────────────────> Factura
   ├── requiere si aplica (1 → 0..1) ────> Envio
   └── posible devolucion (1 → 0..*) ────> Devolucion
                                              └── genera (1 → 0..1) ────────> Reembolso

Vendedor
   ├── publica (1 → 0..*) ───────────────> Producto
   │                                          └── tiene (1 → 0..*) ────────> Inventario
   │
   └── tiene (1 → 0..*) ─────────────────> Bodega
                                              ├── almacenar ────────────────> Inventario
                                              └── despacha (1 → 0..*) ──────> Envio
```

---

# Entidades

---

# usuario (Abstracta)



Es la clase base que representa a cualquier persona que use la plataforma. Aquí se guarda lo básico que todos los usuarios tienen en común, sin importar si son comprador o vendedor: nombre, correo, rol y estado de la cuenta.

Como es abstracta, nunca se crea un "usuario" directamente; siempre va a ser un Comprador o un Vendedor.

## Atributos

| Atributo   | Tipo   |Descripción                                              |
| ---------- | ------ | ------------------------------------------------------------ |
| idUsuario    | long   | Identifica de forma única al usuario.                        |
| name       | String | Nombre oficial del usuario.                                       |
| Correo electrónico| String | Medio principal de acceso y comunicación.                        |
| rolUsuario | String | Define las responsabilidades y permisos.                          |
| Estado | String | Condición operativa (Activo, Bloqueado, etc.). |

## Relaciones

* Un `usuario` se puede especializar como `Comprador`.
* Un `usuario` se puede especializar como `Vendedor`.

---

# Comprador

Es el usuario que compra en la plataforma. Va agregando productos a su carrito y, cuando confirma, ese carrito pasa a ser un pedido. Puede tener varias direcciones guardadas para recibir sus compras.

## Hereda de

`usuario`

## Atributos

| Atributo            | Tipo           |Descripción                                  |
| ------------------- | -------------- | -------------------------------------------------- |
| direccionPrincipal  | String         | Ubicación habitual para entregas.      |
| direccionAdicional  | List\<String\> | Ubicaciones secundarias de entrega.     |
| estadoComprador     | String         | Condición del comprador para realizar compras.            |

## Relaciones

* Un comprador usa un `Carrito` a la vez (`utiliza`, 1 → 0..1).

---

# Vendedor

Es el usuario que publica productos para vender y que administra el inventario a través de sus bodegas.

## Hereda de

`usuario`

## Atributos

| Atributo       | Tipo   |Descripción                          |
| -------------- | ------ | ------------------------------------------ |
| estadoVendedor | String | El estado de su cuenta como vendedor.      |

## Relaciones

* Un vendedor publica cero o varios `Producto` (`publica`, 1 → 0..*).
* Un vendedor administra cero o varias `Bodega` (`tiene`, 1 → 0..*).

---

# Producto


Es cualquier artículo que un vendedor pone a la venta en la plataforma.

## Atributos

| Atributo       | Tipo        |Descripción                             |
| -------------- | ----------- | --------------------------------------------- |
| idProducto     | long        | Identifica de forma única al producto.        |
| nombreProducto | String      | El nombre con el que se muestra el producto.  |
| tipoProducto   | String      | La categoría a la que pertenece.              |
| variantes      | String/List | Las variantes disponibles (talla, color, etc). |
| estado         | String      | Si está activo, agotado, descontinuado, etc.  |
| precioActual   | double      | El precio al que se está vendiendo hoy.       |

## Relaciones

* Cada producto lo publica un solo `Vendedor` (`publica`, lado inverso).
* Un producto puede tener varios registros de `Inventario` (`tiene`, 1 → 0..*).
* Un producto puede aparecer en varios `ItemCarrito` y `LineaPedido` (`corresponde`).

---

# Inventario

Es el registro que controla cuánto stock hay disponible de un producto en una bodega específica.

## Atributos

| Atributo           | Tipo |Descripción                        |
| ------------------ | ---- | ---------------------------------------- |
| idInventario       | long | Identifica de forma única el registro.  |
| cantidadDisponible | int  | Cuántas unidades hay disponibles.       |

## Relaciones

* Cada registro de inventario pertenece a un solo `Producto` (`tiene`, lado inverso).
* Cada registro de inventario está guardado en una sola `Bodega` (`almacenar`, lado inverso).

---

# Bodega



Es el lugar (físico o lógico) donde un vendedor almacena el inventario de sus productos.

## Atributos

| Atributo        | Tipo   |Descripción                        |
| --------------- | ------ | ---------------------------------------- |
| idBodega        | long   | Identifica de forma única la bodega.    |
| ubicacionBodega | String | Dónde está ubicada.                     |
| tipoBodega      | String | Qué tipo de bodega es.                  |

## Relaciones

* Cada bodega pertenece a un solo `Vendedor` (`tiene`, lado inverso).
* Una bodega guarda cero o varios registros de `Inventario` (`almacenar`).
* Una bodega despacha cero o varios `Envio` (`despacha`, 1 → 0..*).

---

# Carrito



Es donde el comprador va acumulando los productos que quiere comprar, antes de confirmar el pedido.

## Atributos

| Atributo  | Tipo   |Descripción                       |
| --------- | ------ | ----------------------------------------- |
| idCarrito | long   | Identifica de forma única el carrito.    |
| fecha     | date   | Cuándo se creó.                          |
| total     | double | El valor total acumulado hasta el momento. |
| estado    | String | En qué estado está (activo, abandonado, etc). |

## Relaciones

* Cada carrito lo usa un solo `Comprador` (`utiliza`, lado inverso).
* Un carrito contiene cero o varios `ItemCarrito` (`contiene`, 1 → 0..*).
* Un carrito se puede confirmar en cero o un `Pedido` (`confirma`, 1 → 0..1).

---

# ItemCarrito



Es cada producto individual que el comprador agregó al carrito, con su cantidad y precio.

## Atributos

| Atributo       | Tipo   |Descripción                             |
| -------------- | ------ | ---------------------------------------------- |
| idDetalle      | long   | Identifica de forma única el ítem.            |
| cantidad       | int    | Cuántas unidades se agregaron.                |
| precioUnitario | double | El precio del producto en el momento en que se agregó. |

## Relaciones

* Cada ítem pertenece a un solo `Carrito` (`contiene`, lado inverso).
* Cada ítem corresponde a un solo `Producto` (`corresponde`, 0..* → 1).

---

# Pedido



Es la compra ya confirmada, generada a partir de un carrito. A partir de aquí empieza el proceso de facturación, envío y, si aplica, devolución.

## Atributos

| Atributo      | Tipo   |Descripción                     |
| ------------- | ------ | -------------------------------------- |
| idPedido      | long   | Identifica de forma única el pedido.  |
| fechaCreacion | date   | Cuándo se creó el pedido.             |
| estado        | String | En qué estado está (pendiente, enviado, etc). |
| total         | double | El valor total del pedido.            |

## Relaciones

* Un pedido se confirma a partir de cero o un `Carrito` (`confirma`, lado inverso).
* Un pedido contiene una o varias `LineaPedido` (`Contiene`, 1 → 1..*).
* Un pedido corresponde a un `Producto` (`corresponde`).
* Un pedido genera una `Factura` (`genera`, 1 → 1).
* Un pedido puede necesitar un `Envio` si aplica (`requiere si aplica`, 1 → 0..1).
* Un pedido puede tener cero o varias `Devolucion` (`posible devolucion`, 1 → 0..*).

---

# LineaPedido



Es cada producto individual dentro de un pedido ya confirmado, con la cantidad y el precio acordado.

## Atributos

| Atributo       | Tipo   |Descripción                          |
| -------------- | ------ | ---------------------------------------- |
| idLinea        | long   | Identifica de forma única la línea.     |
| cantidad       | int    | Cuántas unidades se pidieron.            |
| precioUnitario | double | El precio del producto al momento del pedido. |

## Relaciones

* Cada línea pertenece a un solo `Pedido` (`Contiene`, lado inverso, 1..* → 1).
* Cada línea corresponde a un `Producto` (`corresponde`).

---

# Factura



Es el documento de cobro que se genera cuando se confirma un pedido.

## Atributos

| Atributo  | Tipo   |Descripción                    |
| --------- | ------ | ------------------------------------ |
| idFactura | long   | Identifica de forma única la factura. |
| numero    | String | El número de la factura.             |
| fecha     | date   | Cuándo se emitió.                    |
| subTotal  | double | El valor antes de ajustes.           |
| total     | double | El valor final de la factura.        |

## Relaciones

* Cada factura se genera a partir de un solo `Pedido` (`genera`, lado inverso, 1 → 1).

---

# Envio



Es el envío asociado a un pedido, cuando ese pedido necesita entrega física.

## Atributos

| Atributo           | Tipo   |Descripción                     |
| ------------------- | ------ | ------------------------------------- |
| idEnvio             | long   | Identifica de forma única el envío.  |
| estado              | String | En qué estado está (en camino, entregado, etc). |
| direccionDeEntrega  | String | A dónde se debe entregar.            |

## Relaciones

* Un envío es requerido por cero o un `Pedido` (`requiere si aplica`, lado inverso).
* Un envío es despachado por una sola `Bodega` (`despacha`, lado inverso).

---

# Devolucion


Es la solicitud que hace el comprador cuando quiere devolver algo de su pedido.

## Atributos

| Atributo     | Tipo   |Descripción                       |
| ------------ | ------ | ----------------------------------------- |
| idDevolucion | long   | Identifica de forma única la devolución. |
| motivo       | String | Por qué se está devolviendo.             |
| fecha        | date   | Cuándo se solicitó.                      |
| estado       | String | En qué estado está la devolución.        |

## Relaciones

* Una devolución está asociada a cero o varios `Pedido` (`posible devolucion`, lado inverso).
* Una devolución puede generar cero o un `Reembolso` (`genera`, 1 → 0..1).

---

# Reembolso


Es el dinero que se le devuelve al comprador cuando su devolución fue aprobada.

## Atributos

| Atributo    | Tipo   |Descripción                     |
| ----------- | ------ | ------------------------------------- |
| idReembolso | long   | Identifica de forma única el reembolso. |
| monto       | double | Cuánto dinero se devolvió.            |
| fecha       | date   | Cuándo se hizo el reembolso.          |
| estado      | String | En qué estado está el reembolso.      |

## Relaciones

* Cada reembolso se genera a partir de una sola `Devolucion` (`genera`, lado inverso, 0..1 → 1).