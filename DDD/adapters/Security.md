# Security – NexusMarket

Seguridad **stateless con JWT** (`application.adapters.security`), con la misma estructura del
proyecto del banco: filtro, proveedor de tokens, adaptador de contraseñas y configuración por rol.

## Flujo

1. `POST /api/v1/auth/login` → `LoginService` valida correo y contraseña y emite un token.
2. En cada petición, `JwtAuthenticationFilter` lee `Authorization: Bearer <token>` y llama a
   `LoadAuthenticatedUserService`, que comprueba la firma, la **versión del token** y que la
   cuenta siga `ACTIVA`, y carga al usuario **desde la base de datos**.
3. El usuario queda en el contexto como `AuthenticatedUserPrincipal` (rol `ROLE_<ROL>`); los
   controladores lo reciben con `@AuthenticationPrincipal(expression = "user")`.
4. `SecurityConfig` restringe cada prefijo a su rol; dentro del dominio se valida además el
   permiso (`Permission`) y la propiedad del recurso.

Diferencia con el banco: allí el usuario se reconstruye solo con los datos del token; aquí se
consulta en cada petición, por lo que un cierre de sesión, un cambio de contraseña o un bloqueo
surten efecto de inmediato (a costa de una consulta por petición).

## Token

| Claim | Contenido |
|---|---|
| `sub` | ID del usuario |
| `jti` | Identificador único del token |
| `role` | Rol (informativo; la autorización usa el usuario cargado) |
| `ver` | Versión del token; `UserCredentials.tokenVersion` |
| `iat` / `exp` | Emisión y expiración (`jwt.expiration-ms`, 1 h por defecto) |

Firma HMAC-SHA con `jwt.secret` (mínimo 32 bytes; la aplicación no arranca con uno más corto).
Cerrar sesión o cambiar la contraseña incrementa la versión: todos los tokens anteriores dejan de valer.

## Rutas por rol

| Prefijo | Acceso |
|---|---|
| `/api/v1/auth/logout` | cualquier usuario autenticado |
| `/api/v1/auth/**` | público (login, registro de compradores) |
| `/actuator/health`, `/actuator/info` | público |
| `/api/v1/buyer/**` | `BUYER` |
| `/api/v1/seller/**` | `SELLER` |
| `/api/v1/logistics/**` | `LOGISTICS_OPERATOR` |
| `/api/v1/supervisor/**` | `SUPERVISOR` |
| `/api/v1/admin/**` | `ADMINISTRATOR` |
| cualquier otra | autenticado |

## Respuestas de error

Sin token o con token inválido: **401**. Con token válido pero de otro rol: **403**. Ambos con el
mismo cuerpo JSON que `GlobalExceptionHandler` (`RestAuthenticationEntryPoint` y
`RestAccessDeniedHandler`).

## Contraseñas

`RawPassword` valida la política (mínimo 8 caracteres, una letra y un dígito) y nunca se imprime.
Solo se guarda el hash BCrypt (`BCryptPasswordServiceAdapter`) en `user_credentials`.

## Configuración (variables de entorno)

| Propiedad | Uso |
|---|---|
| `jwt.secret` | Clave de firma (obligatoria, ≥ 32 bytes) |
| `jwt.expiration-ms` | Duración del token |
| `FRONTEND_ORIGIN` | Origen permitido por CORS (por defecto `http://localhost:5173`) |
