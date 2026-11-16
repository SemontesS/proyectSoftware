<#
  Demo de sustentacion NexusMarket.
  Uso (con la app corriendo en http://localhost:8080):
    .\scripts\demo-sustentacion.ps1 -Fase Flujo           # pasos 1-6 + guarda reportes antes del reinicio
    (reiniciar la aplicacion completa: Ctrl+C y volver a arrancar)
    .\scripts\demo-sustentacion.ps1 -Fase Verificacion    # login de nuevo y compara reportes con los guardados
  Agrega -Pausas para que se detenga antes de cada bloque y puedas explicarlo en el video.
  Cada operacion se valida enseguida con su GET. Los intentos no autorizados deben mostrar 401/403/409.
#>
param([ValidateSet("Flujo","Verificacion")][string]$Fase = "Flujo", [switch]$Pausas)

$Base  = "http://localhost:8080/api/v1"
$Pass  = "Password123*"
$ArchivoEstado = Join-Path $PSScriptRoot "estado-demo.json"

function Titulo($t) {
    if ($Pausas) { Read-Host "`n[Enter para continuar]" | Out-Null }
    Write-Host "`n=== $t ===" -ForegroundColor Cyan
}
function Mostrar($o) { $o | ConvertTo-Json -Depth 8 | Write-Host }

function Api($metodo, $ruta, $token, $cuerpo = $null) {
    $h = @{}
    if ($token) { $h["Authorization"] = "Bearer $token" }
    $p = @{ Method = $metodo; Uri = "$Base$ruta"; Headers = $h; ContentType = "application/json" }
    if ($null -ne $cuerpo) { $p["Body"] = ($cuerpo | ConvertTo-Json -Depth 8) }
    Invoke-RestMethod @p
}

# Operacion que DEBE ser rechazada: muestra el codigo HTTP y falla la demo si fue aceptada.
function Bloqueada($descripcion, $metodo, $ruta, $token, $cuerpo = $null) {
    try {
        Api $metodo $ruta $token $cuerpo | Out-Null
        Write-Host "  [FALLO] $descripcion -> fue ACEPTADA" -ForegroundColor Red
    } catch {
        $code = [int]$_.Exception.Response.StatusCode
        Write-Host "  [BLOQUEADA $code] $descripcion" -ForegroundColor Yellow
    }
}

function Login($email) { (Api POST "/auth/login" $null @{ email = $email; password = $Pass }).token }
function Direccion($detalle, $ciudad) { @{ type = "HOME"; detail = $detalle; city = $ciudad } }

if ($Fase -eq "Verificacion") {
    if (-not (Test-Path $ArchivoEstado)) {
        Write-Host "No existe $ArchivoEstado. Ejecuta primero -Fase Flujo completo (hasta el mensaje 'Listo')." -ForegroundColor Red
        return
    }
    $e = Get-Content $ArchivoEstado -Raw | ConvertFrom-Json
    if (-not $e.sellers -or @($e.sellers).Count -eq 0) {
        Write-Host "El archivo de estado no contiene vendedores; repite -Fase Flujo." -ForegroundColor Red
        return
    }
    Titulo "Verificacion final tras reinicio"
    $iguales = $true
    foreach ($s in $e.sellers) {
        $tok = Login $s.email
        $despues = (Api GET "/seller/reports/sales" $tok) | ConvertTo-Json -Depth 8
        $antes = $s.reporteAntes
        Titulo "Reporte de $($s.email) tras el reinicio"
        Write-Host $despues
        if ($despues.Trim() -eq $antes.Trim()) { Write-Host "  IGUAL al reporte previo al reinicio" -ForegroundColor Green }
        else { Write-Host "  DIFERENTE al reporte previo" -ForegroundColor Red; $iguales = $false }
    }
    $adm = Login "admin@nexusmarket.com"
    foreach ($s in $e.sellers) {
        $r = (Api GET "/admin/reports/sellers/$($s.sellerId)" $adm) | ConvertTo-Json -Depth 8
        if ($r.Trim() -ne $s.reporteAntes.Trim()) { $iguales = $false; Write-Host "  El admin ve datos distintos para $($s.email)" -ForegroundColor Red }
    }
    if ($iguales) { Write-Host "`nPERSISTENCIA OK: todos los reportes son identicos" -ForegroundColor Green }
    else { Write-Host "`nHAY DIFERENCIAS" -ForegroundColor Red }
    return
}

$n = Get-Random -Minimum 1000 -Maximum 9999
$admin = Login "admin@nexusmarket.com"
$logi  = Login "logistica@nexusmarket.com"
$sup   = Login "supervisor@nexusmarket.com"

# 1. Tres compradores y tres vendedores (cada vendedor con un producto fisico y uno digital) ---------
Titulo "1. Crear tres compradores"
$compradores = @()
1..3 | ForEach-Object {
    $mail = "comprador$_.$n@demo.com"
    Api POST "/auth/register/buyer" $null @{ name = "Comprador $_"; email = $mail; identityDocument = "BUY-$n$_";
        primaryAddress = Direccion "Calle $_ # 10-20" "Medellin"; password = $Pass } | Out-Null
    $tok = Login $mail
    $compradores += @{ email = $mail; token = $tok }
    Write-Host "GET perfil:"; Mostrar (Api GET "/buyer/profile" $tok)
}

Titulo "1b. Crear tres vendedores, cada uno con un producto FISICO y uno DIGITAL"
$vendedores = @()
1..3 | ForEach-Object {
    $mail = "vendedor$_.$n@demo.com"
    $reg = Api POST "/admin/sellers" $admin @{ name = "Vendedor $_"; email = $mail; identityDocument = "SEL-$n$_";
        warehouseLocation = Direccion "Bodega $_" "Bello"; password = $Pass }
    $tok = Login $mail
    $sid = $reg.seller.userId; $wid = $reg.firstWarehouse.warehouseId
    $fis = Api POST "/seller/products" $tok @{ name = "Producto fisico V$_"; type = "PHYSICAL";
        price = @{ amount = 100000 * $_; currency = "COP" };
        variants = @(@{ sku = "FIS-$n-$_"; attribute = "color"; value = "negro" }) }
    $dig = Api POST "/seller/products" $tok @{ name = "Producto digital V$_"; type = "DIGITAL";
        price = @{ amount = 50000 * $_; currency = "COP" };
        variants = @(@{ sku = "DIG-$n-$_"; attribute = "licencia"; value = "anual" }) }
    Api POST "/seller/products/$($fis.productId)/publish" $tok | Out-Null
    Api POST "/seller/products/$($dig.productId)/publish" $tok | Out-Null
    Api POST "/seller/inventory" $tok @{ productId = $fis.productId; sku = "FIS-$n-$_"; warehouseId = $wid; initialQuantity = 5 } | Out-Null
    $vendedores += @{ email = $mail; token = $tok; sellerId = $sid; fisicoId = $fis.productId; fisicoSku = "FIS-$n-$_";
                      digitalId = $dig.productId; digitalSku = "DIG-$n-$_" }
    Write-Host "GET productos del vendedor $_ :"; Mostrar (Api GET "/seller/products" $tok)
    Write-Host "GET inventario del producto fisico:"; Mostrar (Api GET "/seller/products/$($fis.productId)/inventory" $tok)
}

# 2. Carrito del comprador 1: 2 fisicos + 2 digitales ---------------------------------------------
Titulo "2. Carrito (2 fisicos + 2 digitales) del comprador 1"
$c1 = $compradores[0].token
$v1 = $vendedores[0]; $v2 = $vendedores[1]
Api POST "/buyer/cart/items" $c1 @{ productId = $v1.fisicoId;  sku = $v1.fisicoSku;  quantity = 1 } | Out-Null
Api POST "/buyer/cart/items" $c1 @{ productId = $v2.fisicoId;  sku = $v2.fisicoSku;  quantity = 1 } | Out-Null
Api POST "/buyer/cart/items" $c1 @{ productId = $v1.digitalId; sku = $v1.digitalSku; quantity = 1 } | Out-Null
Api POST "/buyer/cart/items" $c1 @{ productId = $v2.digitalId; sku = $v2.digitalSku; quantity = 1 } | Out-Null
Write-Host "GET carrito:"; Mostrar (Api GET "/buyer/cart" $c1)

Titulo "2b. Intentar agregar mas unidades de las disponibles (stock = 5) -> debe bloquearse"
Bloqueada "Agregar 50 unidades del fisico del vendedor 1" POST "/buyer/cart/items" $c1 @{ productId = $v1.fisicoId; sku = $v1.fisicoSku; quantity = 50 }
Bloqueada "Subir a 50 unidades en el carrito" PUT "/buyer/cart/items/$($v1.fisicoId)" $c1 @{ sku = $v1.fisicoSku; quantity = 50 }
Write-Host "GET carrito (sin cambios):"; Mostrar (Api GET "/buyer/cart" $c1)

# 3. Compra y envio -------------------------------------------------------------------------------
Titulo "3. Crear orden, pagar y enviar"
$orden = Api POST "/buyer/orders" $c1
Write-Host "GET orden creada:"; Mostrar (Api GET "/buyer/orders/$($orden.orderId)" $c1)
Api POST "/buyer/orders/$($orden.orderId)/payment" $c1 | Out-Null
Write-Host "GET orden pagada:"; Mostrar (Api GET "/buyer/orders/$($orden.orderId)" $c1)
$envios = Api POST "/logistics/orders/$($orden.orderId)/shipments" $logi
foreach ($s in $envios) {
    Api POST "/logistics/shipments/$($s.shipmentId)/dispatch" $logi | Out-Null
    Api POST "/logistics/shipments/$($s.shipmentId)/delivery" $logi | Out-Null
}
Write-Host "GET envios:"; Mostrar (Api GET "/buyer/orders/$($orden.orderId)/shipments" $c1)
Write-Host "GET orden entregada:"; Mostrar (Api GET "/buyer/orders/$($orden.orderId)" $c1)
Write-Host "GET inventario del vendedor 2 tras la compra:"; Mostrar (Api GET "/seller/products/$($v2.fisicoId)/inventory" $v2.token)

Titulo "Reporte de los vendedores ANTES de la devolucion"
foreach ($v in @($v1, $v2)) { Mostrar (Api GET "/seller/reports/sales" $v.token) }

# 4. Devolucion y reembolso de UN producto fisico del vendedor 2 ----------------------------------
Titulo "4. Devolucion del producto fisico del vendedor 2"
$dev = Api POST "/buyer/orders/$($orden.orderId)/returns" $c1 @{ productId = $v2.fisicoId; sku = $v2.fisicoSku; reason = "Producto defectuoso" }
Write-Host "El vendedor identificado es: $($dev.sellerId) (esperado: $($v2.sellerId))"
Write-Host "GET devoluciones del comprador:"; Mostrar (Api GET "/buyer/returns" $c1)
Api POST "/logistics/returns/$($dev.returnId)/receive" $logi | Out-Null
Write-Host "GET devoluciones (logistica):"; Mostrar (Api GET "/logistics/returns" $logi)
Api POST "/admin/returns/$($dev.returnId)/refund" $admin | Out-Null
Api POST "/admin/returns/$($dev.returnId)/refund/approve" $admin | Out-Null
Write-Host "GET devolucion + reembolso (admin):"; Mostrar (Api GET "/admin/returns" $admin)
Write-Host "GET orden: las 4 lineas siguen igual:"; Mostrar (Api GET "/buyer/orders/$($orden.orderId)" $c1)
Write-Host "GET envios sin cambios:"; Mostrar (Api GET "/buyer/orders/$($orden.orderId)/shipments" $c1)
Write-Host "GET inventario del vendedor 2 (+1 unidad devuelta):"; Mostrar (Api GET "/seller/products/$($v2.fisicoId)/inventory" $v2.token)
Write-Host "GET inventario del vendedor 1 (sin cambios):"; Mostrar (Api GET "/seller/products/$($v1.fisicoId)/inventory" $v1.token)

# 5. Reportes de vendedores -----------------------------------------------------------------------
Titulo "5. Reportes de vendedores"
$estado = @{ sellers = @() }
foreach ($v in $vendedores) {
    $rep = (Api GET "/seller/reports/sales" $v.token) | ConvertTo-Json -Depth 8
    Write-Host "Vendedor $($v.sellerId):"; Write-Host $rep
    $estado.sellers += @{ email = $v.email; sellerId = $v.sellerId; reporteAntes = $rep }
}
Write-Host "Supervisor consulta el reporte del vendedor 2:"; Mostrar (Api GET "/supervisor/reports/sellers/$($v2.sellerId)" $sup)

# 6. Seguridad por rol ----------------------------------------------------------------------------
Titulo "6. Operaciones NO autorizadas (todas deben bloquearse)"
$c2 = $compradores[1].token
Bloqueada "Comprador crea un producto"                    POST "/seller/products" $c1 @{ name = "x"; type = "PHYSICAL"; price = @{ amount = 1; currency = "COP" }; variants = @(@{ sku = "X-1"; attribute = "a"; value = "b" }) }
Bloqueada "Comprador consulta reportes de administracion"  GET  "/admin/reports" $c1
Bloqueada "Comprador aprueba un reembolso"                 POST "/admin/returns/$($dev.returnId)/refund/approve" $c1
Bloqueada "Comprador 2 lee la orden del comprador 1"       GET  "/buyer/orders/$($orden.orderId)" $c2
Bloqueada "Vendedor usa el carrito"                        GET  "/buyer/cart" $v1.token
Bloqueada "Vendedor 1 lee el reporte del vendedor 2"       GET  "/admin/reports/sellers/$($v2.sellerId)" $v1.token
$inv2 = Api GET "/seller/products/$($v2.fisicoId)/inventory" $v2.token
$inv2Id = @($inv2)[0].inventoryId
Bloqueada "Vendedor 1 ajusta inventario del vendedor 2"    POST "/seller/inventory/$inv2Id/adjustments" $v1.token @{ delta = 100 }
Bloqueada "Logistica genera un reembolso"                  POST "/admin/returns/$($dev.returnId)/refund" $logi
Bloqueada "Supervisor intenta recibir una devolucion"      POST "/logistics/returns/$($dev.returnId)/receive" $sup
Bloqueada "Sin token"                                      GET  "/buyer/orders" $null

$estado | ConvertTo-Json -Depth 10 | Set-Content -Path $ArchivoEstado -Encoding UTF8
Titulo "Listo. Ahora reinicia la aplicacion y ejecuta:  .\scripts\demo-sustentacion.ps1 -Fase Verificacion"
