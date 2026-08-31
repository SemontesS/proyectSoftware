# Modelo Dominio
## introdccion

NexusMarket es una plataforma digital centralizada diseñada para actuar como intermediario comercial entre compradores y vendedores. El propósito principal de la plataforma es administrar de manera integral todo el ciclo de la operación e-commerce: desde la incorporación de usuarios y el catálogo de productos hasta el control de existencias, la logística de envíos, la facturación y los servicios posventa (devoluciones y reembolsos), garantizando la trazabilidad y la correcta coordinación entre todos los participantes del ecosistema

El modelo distingue entre:

* **Usuarios**, Personas autorizadas para interactuar con el sistema.
* **Vendedores**, Responsables de comercializar productos.
* **Compradores**, Usuarios que realizan compras.
* **Bodegas**, Lugares donde se administra el inventario físico.
* **producto** Bienes físicos o digitales ofrecidos.
* **Inventario** Existencias disponibles para comercialización.
* **Pedidos**, Solicitudes de compra realizadas por compradores.
* **Factura**, Información comercial asociada a las ventas.
* **Envios**, Procesos logísticos para productos físicos.

---

# Domain Class Hierarchy

```texto

usuario (Abstracta)
├── id_user: long
├── name: String
├── e-mail: String
├── rolUsuario: String
└── statusUser: String
        △
        │ extends
   ┌────┴────┐
Comprador   Vendedor

```
---

# Relaciones de dominio

```texto

usuario (Abstracta)
   │
   ├── Comprador
   │      └── utiliza ──────────────> Carrito
   │
   └── Vendedor
          ├── publica ─────────────> Producto
          └── tiene ───────────────> Bodega

Carrito
   ├── contiene ────────────────────> ItemCarrito
   │                                     └── corresponde ──> Producto
   │
   └── confirma ────────────────────> Pedido

Pedido
   ├── Contiene ────────────────────> LineaPedido
   │                                     └── corresponde ──> Producto
   │
   ├── corresponde ─────────────────> Producto
   ├── genera ──────────────────────> Factura
   ├── requiere (si aplica) ────────> Envio
   └── posible devolucion ──────────> Devolucion
                                          └── genera ──────> Reembolso

Producto
   └── tiene ───────────────────────> Inventario

Bodega
   ├── almacenar ───────────────────> Inventario
   └── despacha ────────────────────> Envio


```

---

# Entidades

---

# Usuario(Abstracta)

Esta clase abstracta identifica la información principal y contacto que comparten los usuarios del sistema.

El rol asignado a un participante tiene varias funciones y restricciones con el fin de asignar responsabilidades a cada uno

Esta clase no se puede instanciar directamente.

## Atributos

|Atributo |tipo |Descrispcion                                                                                                                                     |
|---------|-----|-------------------------------------------------------------------------------------------------------------------------------------------|













