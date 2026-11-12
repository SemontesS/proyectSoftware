# NexusMarket — Construcción de Software 2 (2026-2)

Marketplace con **arquitectura hexagonal** (Ports & Adapters) y diseño guiado por el dominio.
Compradores y vendedores operan sobre un catálogo con inventario distribuido en bodegas, y el personal
(administrador, operador logístico y supervisor) gestiona pagos, despachos, devoluciones y reembolsos.

> Puesta en marcha paso a paso: **[SETUP.md](SETUP.md)**.

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje y framework | Java 17, Spring Boot 4.1 |
| Datos de negocio | MySQL 8 (Spring Data JPA / Hibernate) |
| Auditoría | MongoDB 6 |
| Seguridad | Spring Security, JWT sin estado (JJWT), contraseñas con BCrypt |
| Entorno | Docker Compose (aplicación + MySQL + MongoDB) |

## Estructura del repositorio

```
.
├── docker-compose.yml          # aplicación + MySQL + MongoDB
├── SETUP.md                    # guía de puesta en marcha y prueba de humo
├── SDD/                        # documentación de diseño
│   ├── domain/                 #   modelo, value objects, servicios, puertos
│   └── adapters/               #   API REST, persistencia, seguridad
└── ecommerce/
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/application/
        ├── domain/
        │   ├── models/         # entidades y agregados (sin dependencias de JPA)
        │   ├── valueobjects/   # Money, Email, Sku, RawPassword, enums...
        │   ├── exceptions/     # errores de negocio
        │   ├── ports/in/       # un puerto de entrada por rol
        │   ├── ports/out/      # repositorios, JWT, contraseñas, auditoría
        │   └── services/       # 57 servicios, uno por caso de uso
        ├── adapters/
        │   ├── useCases/       # implementan los puertos de entrada (transaccionales + auditoría)
        │   ├── rest/           # controladores, DTO, mappers, manejo de errores
        │   ├── persistence/    # jpa/ (MySQL) y mongodb/ (auditoría)
        │   └── security/       # filtro JWT, configuración por rol
        └── infrastructure/     # seed de datos, reloj
```

## Arquitectura

```
Controlador REST ──► Puerto de entrada ──► Caso de uso ──► Servicio de dominio ──► Puerto de salida ──► Adaptador (JPA / Mongo)
   (adapters/rest)    (domain/ports/in)   (adapters/useCases)   (domain/services)     (domain/ports/out)   (adapters/persistence)
```

El dominio **no importa** JPA ni controladores: los agregados se reconstruyen desde la base con
fábricas `restore(...)` y no exponen `setters`, de modo que cada regla de negocio se conserva.

## Seguridad y roles

Cada rol tiene su prefijo de rutas, su puerto de entrada y su controlador.

| Rol | Prefijo | Qué hace |
|---|---|---|
| Público | `/api/v1/auth` | Iniciar sesión, registrarse como comprador, cerrar sesión |
| `BUYER` | `/api/v1/buyer` | Catálogo, carrito, pedidos, pago, factura, seguimiento y devoluciones |
| `SELLER` | `/api/v1/seller` | Sus productos (registro, publicación, suspensión) y su inventario |
| `LOGISTICS_OPERATOR` | `/api/v1/logistics` | Inventario, preparación, despacho y entrega de envíos, recepción de devoluciones |
| `SUPERVISOR` | `/api/v1/supervisor` | Consultas, reportes y auditoría (solo lectura) |
| `ADMINISTRATOR` | `/api/v1/admin` | Usuarios, vendedores, bodegas, estados, reembolsos, reportes y auditoría |

124 endpoints en total: ver `SDD/adapters/REST API.md`. La autenticación usa un token JWT que se
invalida al cerrar sesión o cambiar la contraseña (`SDD/adapters/Security.md`).

## Reglas de negocio destacadas

- El inventario es **distribuido**: una compra puede repartirse entre varias bodegas activas, y cada
  bodega genera su propio envío.
- Crear un pedido valida comprador, productos y vendedores **antes** de reservar nada.
- Un producto digital se entrega al pagarse; uno físico sigue `PAGADO → DESPACHADO → ENTREGADO → FINALIZADO`.
- Los reembolsos no pueden superar el total del pedido ni usar otra moneda, y solo se generan sobre
  una devolución recibida.
- El control de versión optimista (`@Version`) impide que dos reservas simultáneas confirmen el mismo stock.

El catálogo completo de servicios y los supuestos pendientes de confirmar están en
`SDD/domain/services/Domain Services.md`.

## Variables de entorno

Todas son opcionales: sin un `.env` el proyecto funciona con los valores por defecto
(ver `.env.example`).

| Variable | Por defecto | Uso |
|---|---|---|
| `HOST_SERVER_PORT` | `8080` | Puerto en que se publica la API |
| `MYSQL_ROOT_PASSWORD` / `MYSQL_PASSWORD` | `root_password` / `nexus_app_password` | Contraseñas de MySQL |
| `JWT_SECRET` | valor de desarrollo | Clave de firma (mínimo 32 bytes) |
| `JWT_EXPIRATION_MS` | `3600000` | Duración del token (1 hora) |
| `FRONTEND_ORIGIN` | `http://localhost:5173` | Origen permitido por CORS |
| `SEED_ENABLED` | `true` | Cargar los datos de demostración al arrancar |

> El `JWT_SECRET` por defecto es **público** (está en el repositorio): sirve para ejecutar el trabajo
> académico, pero no para un entorno compartido.

## Estado de validación

Lo que se ha comprobado y lo que no, con honestidad:

- **Comprobado:** el código compila contra stubs de las librerías; el dominio, los mappers REST, los
  adaptadores de persistencia y el seed se probaron con repositorios simulados en memoria (más de 180
  comprobaciones, incluido el flujo completo de compra, devolución y reembolso, y el autowiring por tipo
  de los 57 servicios, 6 casos de uso y 11 adaptadores).
- **Pendiente de la primera ejecución real:** Spring, Hibernate con MySQL, MongoDB, JJWT y Spring Security
  no se han ejecutado todavía. Los pasos de `SETUP.md` (sección 3 y 5) son esa validación.

## Pruebas

`ecommerce/src/test/java/application/domain/` contiene pruebas unitarias del dominio (JUnit 5, sin Spring ni
bases de datos): `Quantity`, `Money`, `Email`, `Sku`, `RawPassword`, `Inventory`, `Cart`, `Order`, `Return`,
`Product` y `AuditLog`. Se ejecutan con `./mvnw test -Dtest='application.domain.**'`. `EcommerceApplicationTests`
arranca todo el contexto y necesita MySQL y MongoDB levantados (`docker compose up -d mysql mongo`).

## Notas

- Los supuestos de negocio que la especificación no define (estado inicial de un producto, reglas de
  devolución, permisos por rol, impuestos) están listados en `SDD/domain/services/Domain Services.md` §14.2.
- Al reiniciar con `docker compose down -v` se borran las bases y se vuelven a cargar los datos de demostración.
