# Domain Value Objects

## Introducción

Los Value Objects son elementos que nos ayudan a organizar y representar algunos valores importantes dentro de Ecommerce. Estos se pueden utilizar para manejar información como los roles de los usuarios, los diferentes estados de los procesos y los tipos de productos que se encuentran dentro de la plataforma.

A diferencia de una entidad, un Value Object no necesita tener un identificador propio, ya que lo más importante es el valor que representa y la información que contiene. En este caso, los Value Objects permiten que los datos utilizados en el sistema tengan un significado más claro y se manejen de una manera organizada.

Dentro de Ecommerce, estos valores hacen parte de diferentes procesos del marketplace y ayudan a representar de forma sencilla la información relacionada con los usuarios, productos, pedidos, inventario y demás elementos que hacen parte del funcionamiento de la plataforma.
------------------------------------------------------------------------

# Value Object Structure

``` text
DomainCatalog
├── RolUsuario
├── EstadoUsuario
├── EstadoComprador
├── EstadoVendedor
├── TipoProducto
├── EstadoProducto
├── TipoBodega
├── EstadoInventario
├── EstadoCarrito
├── EstadoPedido
├── EstadoEnvio
├── EstadoDevolucion
└── EstadoReembolso
```

Estos conceptos permiten mantener valores definidos para los diferentes
elementos del sistema y facilitan la interpretación de la información
del negocio.

------------------------------------------------------------------------

# DomainCatalog

## Descripción

`DomainCatalog` representa una estructura general para los valores que
se manejan de forma controlada dentro del dominio.

Su propósito es agrupar conceptos que tienen valores previamente
definidos, como los roles, estados y tipos.

### Atributos

  | Atributo      | Tipo     | Descripción |
  |------------- | -------- | ---------------------------------------------
  | code          | String   | Código utilizado para identificar el valor.|
  | name          | String   | Nombre del valor.|
  | description   | String   | del significado del valor.|

------------------------------------------------------------------------

# RolUsuario

## Descripción

`RolUsuario` representa el papel que cumple una persona dentro de
Ecommerce.

El rol permite establecer las responsabilidades que puede desarrollar
cada usuario dentro de la plataforma.

### Valores definidos

  | Código                  | Rol                     | Descripción |
  |----------------------- | ----------------------- | ---------------------|
  | COMPRADOR               | Comprador               | Usuario que adquiere productos publicados.| 
  | VENDEDOR               | Vendedor               | Responsable de registrar y administrar productos.|
  | OPERADOR_LOGISTICO      | Operador Logístico      | Responsable de la operación física de bodegas y despachos.| 
  | ADMINISTRADOR           | Administrador           | Responsable de administrar vendedores y bodegas.| 
  | SUPERVISOR              | Supervisor              | Perfil encargado de consulta y seguimiento operativo.| 
  -----------------------------------------------------------------------

### Relación con Usuario

``` text
Usuario
   │
   └── rolUsuario
```

Cada usuario tiene un único rol dentro del sistema.

------------------------------------------------------------------------

# EstadoUsuario

## Descripción

`EstadoUsuario` representa la condición operativa en la que se encuentra
un usuario.

Permite identificar si el usuario se encuentra habilitado o presenta
alguna restricción para operar dentro del sistema.

La especificación funcional contempla estados como **Activo** y
**Bloqueado**

### Uso

``` text
Usuario
   └── estado
```

------------------------------------------------------------------------

# EstadoComprador

## Descripción

`EstadoComprador` representa la condición comercial del comprador.

Este valor permite identificar la situación del comprador frente a las
operaciones comerciales que puede realizar en la plataforma.

### Uso

``` text
Comprador
   └── estadoComprador
```

La especificación funcional establece que este atributo es obligatorio.

------------------------------------------------------------------------

# EstadoVendedor

## Descripción

`EstadoVendedor` representa la situación actual del vendedor dentro de
Ecommerce.

Permite controlar si el vendedor se encuentra habilitado para
desarrollar sus actividades dentro de la plataforma.

### Uso

``` text
Vendedor
   └── estadoVendedor
```

------------------------------------------------------------------------

# TipoProducto

## Descripción

`TipoProducto` permite diferenciar los productos ofrecidos en
Ecommerce según la forma en que son entregados.

### Valores definidos

 
| Código                 | Tipo                   | Descripción |
| ----------------------- | ---------------------- |-------------------- |
| FISICO                 | Físico                 | Producto que requiere inventario y despacho.|
| DIGITAL                | Digital                | Producto cuya entrega se realiza de forma  inmediata después del pago.|


### Uso

``` text
Producto
   └── tipoProducto
```

------------------------------------------------------------------------

# EstadoProducto

## Descripción

`EstadoProducto` representa la situación del producto dentro del
catálogo.

### Valores definidos

  -----------------------------------------------------------------------

 | Código                  | Estado                |  Descripción |
 | ----------------------- | ----------------------- | --------------------|
 | PUBLICADO               | Publicado               | Producto disponible dentro del catálogo.|
 |SUSPENDIDO              | Suspendido               | Producto temporalmente suspendido. |
 | DESCONTINUADO          | Descontinuado          | Producto que dejó de estar disponible dentro  del catálogo. |


### Uso

``` text
Producto
   └── estado
```

------------------------------------------------------------------------

# VariantesProducto

## Descripción

Las variantes representan las características que permiten diferenciar
una presentación de un producto, por ejemplo, color, talla o modelo.

En Ecommerce se manejan como un valor asociado al producto y se
almacenan como una lista.

### Ejemplos

``` text
Color: Negro
Talla: M
Modelo: 2026
```

### Uso

``` text
Producto
   └── variantes
```

La especificación funcional establece que las variantes corresponden a
diferencias como color, talla y modelo.

------------------------------------------------------------------------

# TipoBodega

## Descripción

`TipoBodega` permite clasificar las bodegas utilizadas para el
almacenamiento de los productos.

### Clasificación definida

-   Bodega del Marketplace.
-   Bodega de Vendedor.

### Uso

``` text
Bodega
   └── tipoBodega
```

------------------------------------------------------------------------

# EstadoInventario

## Descripción

`EstadoInventario` representa la condición de las existencias asociadas
a un producto y una bodega.

La gestión del inventario contempla el control de las existencias y
evita que se registren cantidades negativas.

### Movimientos relacionados

La especificación funcional contempla los siguientes movimientos:

  |Movimiento        | Descripción |
| ------------------- | ---------------------------------------|
|  Ingreso           | Entrada de existencias al inventario. |
|  Reserva           | Separación de existencias para una operación. |
|  Salida por venta |   Descuento de existencias debido a una venta. |
|  Ajuste            | Modificación de la cantidad registrada por una corrección. |
|  Devolución |        Reingreso relacionado con una devolución.|

------------------------------------------------------------------------

# EstadoCarrito

## Descripción

`EstadoCarrito` representa la situación del carrito durante el proceso
de compra.

Permite diferenciar un carrito que se encuentra disponible para seguir
agregando productos de uno que ya fue utilizado para confirmar un
pedido.

### Uso

``` text
Carrito
   └── estado
```

------------------------------------------------------------------------

# EstadoPedido

## Descripción

`EstadoPedido` representa la etapa en la que se encuentra una compra
dentro de Ecommerce.

### Ciclo definido

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

### Descripción de los estados

  -----------------------------------------------------------------------
 |Estado                              | Descripción
  | ----------------------------------- | -----------------------------------
  | Carrito                             | Los productos todavía se encuentran   en selección provisional. |
|Pendiente de Pago                   | El pedido espera la confirmación  financiera. |
|Pagado                              |El pago fue confirmado y comienza el alistamiento. |
|Despachado                          El pedido salió físicamente de la bodega.|
|Entregado / Finalizado             | La entrega fue completadasatisfactoriamente. |
  

### Uso

``` text
Pedido
   └── estado
```

Un pedido que ya se encuentra finalizado no puede ser modificado.

------------------------------------------------------------------------

# EstadoEnvio

## Descripción

`EstadoEnvio` representa la situación del proceso logístico asociado con
un pedido.

Permite realizar el seguimiento del envío desde su preparación y
despacho hasta la entrega.

### Uso

``` text
Envio
   └── estado
```

La especificación funcional establece que la gestión logística incluye
el empaque, despacho, transporte y confirmación de la entrega.

------------------------------------------------------------------------

# EstadoDevolucion

## Descripción

`EstadoDevolucion` representa la situación de una devolución asociada a
un pedido.

Permite realizar seguimiento al proceso desde que se solicita hasta que
finaliza.

### Uso

``` text
Devolucion
   └── estado
```

La especificación funcional contempla la gestión de devoluciones, aunque
no establece en el documento consultado un catálogo detallado de valores
para este estado.

------------------------------------------------------------------------

# EstadoReembolso

## Descripción

`EstadoReembolso` representa la situación en la que se encuentra un
reembolso asociado a una devolución.

### Uso

``` text
Reembolso
   └── estado
```

El proceso de reembolso forma parte de los procesos incluidos dentro del
alcance de Ecommerce.

La especificación funcional consultada no define un catálogo detallado
de valores para este estado.

------------------------------------------------------------------------

# Relación de los Value Objects con las entidades

Los valores se utilizan directamente dentro de las entidades
correspondientes:

``` text
Usuario
 ├── rolUsuario
 └── estado

Comprador
 └── estadoComprador

Vendedor
 └── estadoVendedor

Producto
 ├── tipoProducto
 ├── variantes
 └── estado

Bodega
 └── tipoBodega

Inventario
 └── estadoExistencia

Carrito
 └── estado

Pedido
 └── estado

Envio
 └── estado

Devolucion
 └── estado

Reembolso
 └── estado
```

------------------------------------------------------------------------

# Rol, Estado y Función

Estos tres conceptos cumplen funciones diferentes dentro del modelo.

## Rol

Indica qué responsabilidad tiene el usuario.

``` text
rolUsuario = VENDEDOR
```

Significa que el usuario participa como vendedor.

## Estado

Indica en qué situación se encuentra actualmente una entidad.

``` text
estado = PUBLICADO
```

Significa que el producto está publicado en el catálogo.

## Función

Representa una acción que realiza el sistema o un participante.

Por ejemplo:

``` text
registrarProducto
confirmarPedido
gestionarEnvio
```

Por lo tanto, el rol y el estado representan información del dominio,
mientras que las funciones representan acciones del proceso.

------------------------------------------------------------------------

# Reglas de los Value Objects

## Valores controlados

Los valores utilizados para roles, tipos y estados deben mantenerse de
forma consistente.

Por ejemplo, para un producto se debe utilizar un valor definido como:

``` text
PUBLICADO
```

y no diferentes textos para representar la misma condición.

## Significado del valor

Cada valor debe tener un significado claro dentro del negocio.

Por ejemplo:

``` text
FISICO
```

representa un producto que requiere inventario y despacho, mientras que:

``` text
DIGITAL
```

representa un producto cuya entrega es inmediata después del pago.

## Relación con las entidades

Los Value Objects se utilizan como parte de la información de las
entidades y ayudan a representar conceptos que tienen valores
controlados.

------------------------------------------------------------------------

# Resumen

Los principales Value Objects y valores controlados identificados para
Ecommerce son:

``` text
RolUsuario
EstadoUsuario
EstadoComprador
EstadoVendedor
TipoProducto
EstadoProducto
VariantesProducto
TipoBodega
EstadoInventario
EstadoCarrito
EstadoPedido
EstadoEnvio
EstadoDevolucion
EstadoReembolso
```

Estos valores permiten representar de manera organizada los diferentes
roles, estados, tipos y características que intervienen en el
funcionamiento del marketplace.

Su utilización ayuda a mantener una estructura clara del dominio y
facilita la comprensión de los procesos relacionados con usuarios,
productos, inventario, compras, pedidos, logística y posventa.
