# Domain Services — Ecommerce

## 1. Introducción

Los **Domain Services (Servicios de Dominio)** de Ecommerce representan operaciones que contienen reglas de negocio y que no pertenecen naturalmente a una sola entidad o Value Object.

En este modelo se utilizan únicamente cuando una regla necesita coordinar diferentes objetos del dominio o cuando asignar la responsabilidad a una entidad concreta generaría un diseño poco claro.

Los servicios de dominio no representan personas, procesos técnicos ni componentes de infraestructura. Su responsabilidad es ejecutar reglas propias del negocio.

---

## 2. ¿Por qué Ecommerce necesita Domain Services?

El Domain Model identifica como agregados iniciales:

```text
Carrito
└── ItemCarrito

Pedido
└── LineaPedido

Inventario
└── MovimientoInventario
```

Y establece como Aggregate Roots iniciales:

```text
Carrito
Pedido
Inventario
```

Cada agregado debe proteger sus propias reglas.

Por ejemplo:

- `Carrito` controla sus `ItemCarrito`.
- `Pedido` controla sus `LineaPedido` y su ciclo de vida.
- `Inventario` controla sus existencias y movimientos.

Sin embargo, existen operaciones del negocio que requieren coordinar varios agregados.

Ejemplo:

```text
Carrito
   +
Inventario
   +
Pedido
   ↓
Confirmación de compra
```

Esta operación no debería colocar toda la responsabilidad dentro de `Carrito`, porque también necesita validar y afectar el inventario y generar un nuevo pedido.

Para este tipo de situaciones se utilizan los Domain Services.

---

## 3. Principios utilizados

Los servicios de dominio de Ecommerce siguen estas reglas:

### 3.1 No tienen identidad propia

Un servicio de dominio no representa una entidad del negocio.

No tiene:

```text
id
estado
fecha de creación
identidad persistente
```

Su responsabilidad está en el comportamiento que ejecuta.

### 3.2 No almacenan estado del negocio

Los servicios deben ser principalmente **stateless**.

Reciben los objetos necesarios, ejecutan una regla y devuelven el resultado.


### 3.4 No reemplazan a las entidades

Si una regla pertenece claramente a una entidad, la regla permanece dentro de esa entidad.

Ejemplo:

```text
Pedido.finalizar()
```

es responsabilidad de `Pedido`, porque el propio pedido controla su estado.

No sería necesario crear:

```text
PedidoService.finalizarPedido()
```

solo para cambiar el estado del pedido.

---

# 4. Domain Services identificados

Para la versión actual del dominio se identifican los siguientes servicios:

| Servicio | Responsabilidad principal |
|---|---|
| `ConfirmacionCompraService` | Coordinar la confirmación de un carrito, la validación del inventario y la creación del pedido. |
| `ReservaInventarioService` | Coordinar reservas de inventario asociadas a productos antes de continuar con una operación comercial. |
| `GestionDevolucionService` | Coordinar una devolución y validar las condiciones necesarias para generar un reembolso. |
| `GeneracionReembolsoService` | Coordinar la creación de un reembolso a partir de una devolución aprobada. |

Estos servicios se mantienen separados porque representan reglas de negocio diferentes.

---

# 5. ConfirmacionCompraService

## 5.1 Propósito

`ConfirmacionCompraService` representa la operación de confirmar una compra.

Esta operación relaciona diferentes conceptos del dominio:

```text
Carrito
   │
   ├── ItemCarrito
   │
   └── Producto
          │
          ↓
      Inventario
          │
          ↓
        Pedido
```

El carrito contiene la selección provisional del comprador, mientras que el pedido representa el compromiso comercial formal.

Por esta razón, confirmar una compra no debe consistir simplemente en cambiar el estado del carrito.

## 5.2 Responsabilidades

El servicio debe:

1. Validar que el carrito pueda ser confirmado.
2. Verificar que el carrito tenga productos.
3. Validar la disponibilidad del inventario cuando corresponda.
4. Reservar las existencias necesarias.
5. Crear las líneas del pedido.
6. Crear el pedido.
7. Mantener el pedido inicialmente en estado `PENDIENTE_PAGO`.

## 5.3 Regla de negocio

La especificación funcional establece que el carrito representa una selección provisional y que el pedido representa el compromiso comercial formal.

Por lo tanto:

```text
Carrito confirmado
        ↓
   Generación
        ↓
     Pedido
        ↓
PENDIENTE_PAGO
```

`CARRITO` no debe tratarse como un estado del `Pedido`.

## 5.4 Entradas

```text
Carrito
Inventario disponible
```

## 5.5 Salida

```text
Pedido
```

## 5.6 Reglas que debe respetar

- El carrito debe estar en una condición que permita confirmarlo.
- El carrito no debe estar vacío.
- Los productos físicos deben contar con inventario.
- No se puede reservar inventario inexistente.
- No se pueden producir existencias negativas.
- El pedido se crea como compromiso comercial formal.
- El pedido inicia en `PENDIENTE_PAGO`.

---

# 6. ReservaInventarioService

## 6.1 Propósito

`ReservaInventarioService` coordina la reserva de inventario cuando una operación comercial necesita separar existencias disponibles.

El servicio es necesario especialmente porque el inventario es distribuido y cada inventario está vinculado a un producto y una bodega específica.

```text
Producto A ──→ Inventario ──→ Bodega 1
Producto B ──→ Inventario ──→ Bodega 2
Producto C ──→ Inventario ──→ Bodega 1
```

## 6.2 Responsabilidades

El servicio debe:

1. Recibir el producto y la cantidad requerida.
2. Identificar el inventario correspondiente.
3. Validar disponibilidad.
4. Ejecutar la reserva.
5. Registrar el movimiento correspondiente.

## 6.3 Regla principal

La especificación establece que no se permitirán existencias negativas bajo ninguna circunstancia y que no se puede reservar inventario inexistente o marcado como `Dañado`.

Por lo tanto:

```text
Cantidad solicitada
        ↓
¿Existe inventario?
        ↓
¿Hay cantidad suficiente?
        ↓
      Sí
        ↓
     Reservar
        ↓
Registrar movimiento
```

Si la cantidad disponible no es suficiente, la operación debe rechazarse.

## 6.4 Responsabilidad de `Inventario`

Aunque existe el servicio, la regla de que una existencia no puede ser negativa continúa protegida por `Inventario`.

El servicio coordina y el agregado `Inventario` protege sus propias invariantes.

---

# 7. GestionDevolucionService

## 7.1 Propósito

`GestionDevolucionService` coordina el proceso de devolución relacionado con un pedido.

La especificación funcional incluye la gestión de devoluciones y establece que un pedido puede originar devoluciones.

```text
Pedido
   │
   ↓
Devolucion
   │
   ↓
Reembolso
```

## 7.2 Responsabilidades

El servicio debe:

1. Recibir el pedido sobre el cual se solicita la devolución.
2. Validar que la solicitud pueda ser procesada.
3. Crear la devolución.
4. Asociar la devolución con el pedido.
5. Permitir que el proceso posterior determine si corresponde un reembolso.

## 7.3 Regla importante

Un pedido finalizado no puede modificarse.

Por lo tanto, cualquier operación de devolución debe respetar el estado del pedido y las reglas específicas definidas para la posventa.

En esta versión del dominio no se inventan condiciones adicionales de devolución porque la especificación funcional no define criterios detallados como plazo máximo, categorías excluidas o porcentajes de reembolso.

---

# 8. GeneracionReembolsoService

## 8.1 Propósito

`GeneracionReembolsoService` coordina la generación de un reembolso a partir de una devolución.

La relación establecida en el Domain Model es:

```text
Devolucion
     │
     └── genera
            ↓
        Reembolso
```

## 8.2 Responsabilidades

El servicio debe:

1. Recibir una devolución.
2. Validar que la devolución pueda generar un reembolso.
3. Determinar el monto según la información disponible.
4. Crear el reembolso.
5. Mantener la relación entre devolución y reembolso.

## 8.3 Regla del dominio

El Domain Model establece:

```text
Una devolución puede generar como máximo un reembolso.
```

Por lo tanto:

```text
Devolución
     │
     └── 0..1 Reembolso
```

El servicio debe impedir que una misma devolución genere más de un reembolso.

---

# 9. Servicios que NO se deben crear

No todo proceso del sistema necesita un Domain Service.

### 9.1 `CarritoService` para agregar productos

No es necesario si la operación pertenece al agregado:

```java
carrito.agregarProducto(producto, cantidad);
```

La responsabilidad está directamente en `Carrito`.

### 9.2 `PedidoService` para cambiar estados

No es necesario para operaciones como:

```text
marcarComoPagado()
marcarComoDespachado()
marcarComoEntregado()
finalizar()
```

El propio `Pedido` controla su ciclo de vida.

### 9.3 `ProductoService` para publicar productos

La publicación es una regla relacionada directamente con el estado del producto.

Por eso puede permanecer en:

```java
producto.publicar();
```

### 9.4 `InventarioService` para sumar existencias

Si la operación afecta a un único agregado:

```java
inventario.ingresar(cantidad);
```

no es necesario crear un servicio solamente para llamar ese método.

El Domain Service aparece cuando se necesita coordinar diferentes objetos o agregados.

---

# 10. Diferencia entre Entidad y Domain Service

| Concepto | Responsabilidad |
|---|---|
| `Pedido` | Protege el estado y reglas propias del pedido. |
| `Inventario` | Protege la cantidad disponible y sus movimientos. |
| `Carrito` | Protege sus productos y estado. |
| `ConfirmacionCompraService` | Coordina carrito, inventario y creación del pedido. |
| `ReservaInventarioService` | Coordina la reserva de inventario requerida por una operación. |
| `GestionDevolucionService` | Coordina el proceso de devolución relacionado con un pedido. |
| `GeneracionReembolsoService` | Coordina la generación del reembolso a partir de una devolución. |

---

# 11. Relación con los Aggregate Roots

Los servicios trabajan con los Aggregate Roots y no deben manipular directamente los elementos internos de un agregado desde fuera.

### Carrito

```text
Carrito
└── ItemCarrito
```

El servicio trabaja con `Carrito` y no necesita administrar directamente cada `ItemCarrito` como si fuera un agregado independiente.

### Pedido

```text
Pedido
└── LineaPedido
```

El servicio trabaja con `Pedido` y el propio pedido controla sus líneas.

### Inventario

```text
Inventario
└── MovimientoInventario
```

El servicio solicita operaciones al `Inventario` y este controla sus movimientos.

---

# 12. Relación con Arquitectura Hexagonal

Los Domain Services pertenecen al **núcleo del dominio**.

Una estructura inicial puede ser:

```text
src
└── main
    └── java
        └── domain
            ├── entity
            ├── valueobject
            └── service
                ├── ConfirmacionCompraService
                ├── ReservaInventarioService
                ├── GestionDevolucionService
                └── GeneracionReembolsoService
```

El dominio no depende de Spring, JPA, bases de datos, REST, HTTP ni APIs externas.

La comunicación con infraestructura se realizará posteriormente mediante puertos y adaptadores.

---

# 13. Dependencias permitidas

La dirección de dependencia debe mantenerse hacia el dominio:

```text
Application
     ↓
Domain
     ↑
Infrastructure
```

Los servicios de dominio pueden utilizar:

- Entidades.
- Aggregate Roots.
- Value Objects.
- Reglas del dominio.
- Excepciones de dominio.

No deben utilizar directamente:

- Controladores.
- Repositorios JPA.
- Entidades de persistencia.
- Clientes HTTP.
- Frameworks.

---

# 14. Resumen de servicios

La primera versión del Domain Model de Ecommerce queda complementada con:

```text
                    DOMAIN SERVICES
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ↓                  ↓                  ↓
ConfirmacionCompra   ReservaInventario   GestionDevolucion
        │                  │                  │
        │                  │                  ↓
        │                  │              Reembolso
        ↓                  ↓
     Pedido           Inventario
```

Y adicionalmente:

```text
GeneracionReembolsoService
          │
          ↓
     Devolucion
          │
          ↓
      Reembolso
```

---

# 15. Conclusión

Los Domain Services de Ecommerce se utilizan para representar operaciones de negocio que necesitan coordinar diferentes elementos del dominio.

La regla principal del diseño es:

> Si una operación pertenece claramente a un único agregado, debe permanecer en ese agregado. Si la operación necesita coordinar varios agregados y representa una regla propia del negocio, puede utilizarse un Domain Service.

Por esta razón, la primera versión contempla servicios para:

- Confirmación de compra.
- Reserva de inventario.
- Gestión de devoluciones.
- Generación de reembolsos.

Esta definición mantiene el dominio cohesionado y evita crear servicios innecesarios que simplemente actúen como intermediarios entre la aplicación y las entidades.
