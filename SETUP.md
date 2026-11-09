# Guía paso a paso: poner en marcha NexusMarket

Esta guía asume **Windows + PowerShell**. Los comandos de Docker son iguales en macOS y Linux; para
llamar a la API allí puedes usar `curl` en lugar de `Invoke-RestMethod`.

## 0. Prerrequisitos

Solo necesitas **Docker Desktop** en ejecución. No hace falta instalar Java, Maven, MySQL ni MongoDB:
corren dentro de los contenedores.

```powershell
docker --version
docker compose version
```

Puertos libres en tu equipo: **8080** (API), **3306** (MySQL) y **27017** (MongoDB). Si tienes MySQL o
MongoDB instalados y encendidos, deténlos o cambia el puerto de la API con `HOST_SERVER_PORT` (ver
`.env.example`).

## 1. Levantar la aplicación y las bases de datos

Desde la raíz del repositorio (donde está `docker-compose.yml`):

```powershell
docker compose up -d --build
docker compose logs -f nexus-app
```

La **primera vez** tarda varios minutos: Maven descarga las dependencias y compila el proyecto.
Las siguientes veces arranca en segundos porque la caché de Maven queda en un volumen.

La aplicación está lista cuando el log muestra `Started EcommerceApplication` y, justo antes,
`Demo data loaded`. Sal del log con `Ctrl + C` (la aplicación sigue corriendo).

| Contenedor | Qué es | Puerto |
|---|---|---|
| `nexus-app` | La API (Spring Boot) | 8080 |
| `nexus-mysql` | MySQL 8, base `nexusmarket_db` | 3306 |
| `nexus-mongo` | MongoDB 6, base `audit_db` (auditoría) | 27017 |

## 2. Verificar que está viva

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
# status : UP
```

## 3. Compilar y correr las pruebas

```powershell
docker compose exec nexus-app mvn -B test
```

> La prueba `contextLoads` arranca toda la aplicación, así que necesita MySQL y MongoDB encendidos
> (los levanta el paso 1). Es la primera comprobación de que Spring, Hibernate, MongoDB y la seguridad
> se conectan bien entre sí.

## 4. Datos de demostración

Al arrancar por primera vez se cargan datos de prueba (idempotente: no se duplican al reiniciar).
**Todas las cuentas usan la contraseña `Password123*`.**

| Rol | Correo | Notas |
|---|---|---|
| Administrador | `admin@nexusmarket.com` | |
| Operador logístico | `logistica@nexusmarket.com` | |
| Supervisor | `supervisor@nexusmarket.com` | |
| Vendedor | `tienda.tech@nexusmarket.com` | TechStore: laptops, auriculares y un curso digital |
| Vendedor | `tienda.moda@nexusmarket.com` | Moda Urbana: camisetas, chaqueta, gorra, zapatillas |
| Comprador | `ana.gomez@mail.com` | 2 pedidos (uno entregado con una devolución solicitada, otro sin pagar) |
| Comprador | `luis.perez@mail.com` | 3 pedidos (pagado, digital con reembolso pendiente, despachado) |
| Comprador | `carla.restringida@mail.com` | Comprador **restringido**: no puede comprar |

Hay productos en todos los estados (publicado, suspendido, descontinuado), una laptop con existencias
repartidas en dos bodegas, una bodega inactiva y pedidos en cada etapa del flujo, de modo que todos los
endpoints tienen algo con qué trabajar. La lista completa de rutas está en `SDD/adapters/REST API.md`.

## 5. Prueba de humo: registro → compra → pago → factura → logout

En PowerShell, `Invoke-RestMethod` maneja el JSON sin problemas de comillas.

1. Iniciar sesión como comprador → devuelve `token`, `user` y `expiresIn: 3600`:
   ```powershell
   $base = "http://localhost:8080/api/v1"
   $login = Invoke-RestMethod -Method Post -Uri "$base/auth/login" -ContentType "application/json" `
     -Body '{"email":"ana.gomez@mail.com","password":"Password123*"}'
   $headers = @{ Authorization = "Bearer $($login.token)" }
   $login.user
   ```
2. Ver el catálogo (solo los 4 productos publicados):
   ```powershell
   $products = Invoke-RestMethod -Uri "$base/buyer/products" -Headers $headers
   $products | Select-Object productId, name, type, status
   ```
3. Agregar la camiseta al carrito → **201**:
   ```powershell
   $shirt = $products | Where-Object name -eq "Camiseta basica"
   $item = @{ productId = $shirt.productId; sku = "CAM-L-AZ"; quantity = 2 } | ConvertTo-Json
   Invoke-RestMethod -Method Post -Uri "$base/buyer/cart/items" -Headers $headers -ContentType "application/json" -Body $item
   ```
4. Crear el pedido (reserva el inventario) → **201**, estado `PENDING_PAYMENT`:
   ```powershell
   $order = Invoke-RestMethod -Method Post -Uri "$base/buyer/orders" -Headers $headers
   $order.status; $order.total
   ```
5. Pagar y facturar:
   ```powershell
   Invoke-RestMethod -Method Post -Uri "$base/buyer/orders/$($order.orderId)/payment" -Headers $headers   # status: PAID
   Invoke-RestMethod -Method Post -Uri "$base/buyer/orders/$($order.orderId)/invoice" -Headers $headers   # 201, número INV-...
   ```
6. Como operador logístico, preparar, despachar y confirmar la entrega:
   ```powershell
   $log = Invoke-RestMethod -Method Post -Uri "$base/auth/login" -ContentType "application/json" `
     -Body '{"email":"logistica@nexusmarket.com","password":"Password123*"}'
   $lh = @{ Authorization = "Bearer $($log.token)" }
   $ship = Invoke-RestMethod -Method Post -Uri "$base/logistics/orders/$($order.orderId)/shipments" -Headers $lh
   Invoke-RestMethod -Method Post -Uri "$base/logistics/shipments/$($ship[0].shipmentId)/dispatch" -Headers $lh
   Invoke-RestMethod -Method Post -Uri "$base/logistics/shipments/$($ship[0].shipmentId)/delivery" -Headers $lh
   Invoke-RestMethod -Uri "$base/buyer/orders/$($order.orderId)" -Headers $headers | Select-Object status   # COMPLETED
   ```
7. Seguridad: sin token → **401**; con el token de otro rol → **403**:
   ```powershell
   try { Invoke-RestMethod -Uri "$base/buyer/products" } catch { $_.Exception.Response.StatusCode.value__ }            # 401
   try { Invoke-RestMethod -Uri "$base/admin/reports" -Headers $headers } catch { $_.Exception.Response.StatusCode.value__ }  # 403
   ```
8. Como administrador, ver el reporte y la auditoría:
   ```powershell
   $adm = Invoke-RestMethod -Method Post -Uri "$base/auth/login" -ContentType "application/json" `
     -Body '{"email":"admin@nexusmarket.com","password":"Password123*"}'
   $ah = @{ Authorization = "Bearer $($adm.token)" }
   Invoke-RestMethod -Uri "$base/admin/reports" -Headers $ah
   Invoke-RestMethod -Uri "$base/admin/audit/users/$($login.user.userId)" -Headers $ah
   ```
9. Cerrar sesión → **204**; el token viejo queda invalidado → **401**:
   ```powershell
   Invoke-RestMethod -Method Post -Uri "$base/auth/logout" -Headers $headers
   try { Invoke-RestMethod -Uri "$base/buyer/products" -Headers $headers } catch { $_.Exception.Response.StatusCode.value__ }  # 401
   ```
10. Verificar la persistencia directamente en las bases:
    ```powershell
    docker exec nexus-mysql mysql -uroot -proot_password --batch -e "SHOW TABLES FROM nexusmarket_db; SELECT id, email, role, status FROM nexusmarket_db.users;"
    docker exec nexus-mongo mongosh --quiet audit_db --eval "db.audit_logs.countDocuments()"
    ```

## 6. Apagar todo

```powershell
docker compose down        # detiene los contenedores y conserva los datos
docker compose down -v     # además borra las bases: el próximo arranque vuelve a cargar los datos de demostración
```

## 7. Solución de problemas

| Síntoma | Qué hacer |
|---|---|
| `port is already allocated` | Otro programa usa 8080, 3306 o 27017. Detenlo, o cambia `HOST_SERVER_PORT` en un archivo `.env` (copia de `.env.example`). |
| La aplicación termina al arrancar con `Access denied for user` | El volumen de MySQL conserva una contraseña anterior. Ejecuta `docker compose down -v` y vuelve a levantar. |
| Errores de compilación en `nexus-app` | El proyecto se compila dentro del contenedor la primera vez. Copia **el mensaje completo** (`docker compose logs nexus-app`) y compártelo para corregirlo. |
| `jwt.secret must have at least 32 bytes` | Tu `JWT_SECRET` es demasiado corto. Usa uno de 32 caracteres o más. |
| `Connection refused` a MongoDB o MySQL | Espera a que ambos estén `healthy` (`docker compose ps`); `nexus-app` ya espera por ellos, pero un equipo lento puede tardar. |
| Cambié el código y no se refleja | El código se monta desde `./ecommerce`; reinicia con `docker compose restart nexus-app`. |
| Quiero ver el SQL que ejecuta Hibernate | Agrega `SHOW_SQL=true` al entorno de `nexus-app`. |
