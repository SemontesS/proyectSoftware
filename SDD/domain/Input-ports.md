# Input Ports – NexusMarket

Puertos de entrada del dominio (`application.domain.ports.in`): uno por rol. Los
controladores REST dependen solo de estas interfaces; las implementan los casos de
uso de `application.adapters.useCases`, que delegan en los servicios de dominio,
registran la auditoría de las operaciones de escritura y se ejecutan en una
transacción. Todas las operaciones reciben como primer argumento al usuario autenticado
(`requestingUser`), salvo el login y el registro público.

## Acceso público (`PublicAccessPort`) – 3 operaciones

`login` `logout` `registerBuyer` 

## Comprador (`BuyerPort`) – 21 operaciones

 **Account:** `consultUser` `updateUser` `changePassword` `consultBuyer` `updateBuyerInformation` **Catalog:** `consultProduct` `consultCatalog` **Cart:** `addProductToCart` `updateCartItem` `removeProductFromCart` `consultCart` **Orders:** `createOrder` `payOrder` `cancelOrder` `consultOrder` `consultMyOrders` `generateInvoice` `consultInvoice` `consultShipment` `consultShipmentsByOrder` **Returns:** `requestReturn` 

## Vendedor (`SellerPort`) – 17 operaciones

 **Account:** `consultUser` `updateUser` `changePassword` `consultSeller` `consultWarehouse` **Catalog:** `registerProduct` `updateProduct` `publishProduct` `suspendProduct` `discontinueProduct` `consultProduct` `consultCatalog` **Inventory:** `registerInitialInventory` `registerInventoryEntry` `adjustInventory` `consultInventory` **Orders:** `consultOrder` 

## Operador logístico (`LogisticsOperatorPort`) – 20 operaciones

 **Account:** `consultUser` `updateUser` `changePassword` `consultWarehouse` `consultProduct` `consultCatalog` **Inventory:** `registerInitialInventory` `registerInventoryEntry` `reserveInventory` `registerSaleDispatch` `adjustInventory` `registerInventoryReturn` `consultInventory` **Orders and shipments:** `consultOrder` `prepareShipment` `dispatchShipment` `confirmDelivery` `consultShipment` `consultShipmentsByOrder` **Returns:** `receiveReturn` 

## Supervisor (`SupervisorPort`) – 17 operaciones

 **Account:** `consultUser` `updateUser` `changePassword` **Consultations:** `consultBuyer` `consultSeller` `consultWarehouse` `consultProduct` `consultCatalog` `consultInventory` `consultOrder` `consultOrdersByBuyer` `consultInvoice` `consultShipment` `consultShipmentsByOrder` `consultAdministrativeReport` `consultAuditLogByUser` `consultAuditLogByEntity` 

## Administrador (`AdministratorPort`) – 46 operaciones

 **Users:** `registerInternalUser` `consultUser` `updateUser` `changeUserStatus` `changePassword` **Buyers:** `consultBuyer` `updateBuyerInformation` `changeBuyerCommercialStatus` **Sellers:** `registerSeller` `consultSeller` `updateSellerInformation` `changeSellerStatus` **Warehouses:** `registerWarehouse` `consultWarehouse` `updateWarehouse` `changeWarehouseStatus` **Catalog:** `publishProduct` `suspendProduct` `discontinueProduct` `consultProduct` `consultCatalog` **Inventory:** `registerInitialInventory` `registerInventoryEntry` `reserveInventory` `registerSaleDispatch` `adjustInventory` `registerInventoryReturn` `consultInventory` **Orders, billing and shipments:** `payOrder` `updateOrderStatus` `consultOrder` `consultOrdersByBuyer` `generateInvoice` `consultInvoice` `prepareShipment` `dispatchShipment` `confirmDelivery` `consultShipment` `consultShipmentsByOrder` **Returns and refunds:** `receiveReturn` `generateRefund` `approveRefund` `rejectRefund` **Reports and audit:** `consultAdministrativeReport` `consultAuditLogByUser` `consultAuditLogByEntity` 

