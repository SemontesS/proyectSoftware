package application.domain.services;

import application.domain.models.Cart;
import application.domain.models.CartItem;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Product;

import java.util.Map;
import java.util.Objects;

/**
 * PurchaseConfirmationService coordina la confirmación de un cart, la
 * validación/reservation de inventory y la creación del order.
 *
 * No tiene identidad propia ni stores status del negocio: recibe los
 * added necesarios, ejecuta la regla y devuelve el resultado.
 *
 * Fuente: Domain Services — NexusMarket, sección 5.
 */
public class PurchaseConfirmationService {

    /**
     * Confirms la compra representsda por el cart, reservando el
     * inventory de los products físicos y generando el order
     * correspondiente en status PENDIENTE_PAGO.
     *
     * @param orderId               identificador a asignar al nuevo order
     * @param cart                cart a confirm (debe estar ACTIVE y no vacío)
     * @param productosPorId         products referenciados por los ítems del cart, indexados por productId
     * @param inventariosPorProducto inventory correspondiente a cada product físico, indexado por productId
     * @return el Order generado a partir del cart confirmado
     */
    public Order confirmPurchase(Long orderId,
                                   Cart cart,
                                   Map<Long, Product> productosPorId,
                                   Map<Long, Inventory> inventariosPorProducto) {

        Objects.requireNonNull(cart, "cart es obligatorio");
        Objects.requireNonNull(productosPorId, "productosPorId es obligatorio");
        Objects.requireNonNull(inventariosPorProducto, "inventariosPorProducto es obligatorio");

        // 1-2. Validates que el cart pueda confirmarse y que tenga products.
        //      Cart.confirm() ya protege ambas reglas (status ACTIVE y no vacío).
        cart.confirm();

        // 3-4. For cada product físico, valida availability y reservation existencias.
        for (CartItem item : cart.getItems()) {
            Product product = productosPorId.get(item.getProductId());
            if (product == null) {
                throw new IllegalStateException(
                        "No se encontró el product " + item.getProductId() + " para confirm la compra.");
            }

            if (product.requiresInventoryAndShipping()) {
                Inventory inventory = inventariosPorProducto.get(item.getProductId());
                if (inventory == null) {
                    throw new IllegalStateException(
                            "No existe inventory disponible para el product " + item.getProductId() + ".");
                }
                if (inventory.getAvailableQuantity() < item.getCantidad()) {
                    throw new IllegalStateException(
                            "Inventory insuficiente para el product " + item.getProductId() + ".");
                }
                // 5. Registrar movement correspondiente (RESERVATION).
                //    The invariante de no-negatividad la protege el propio Inventory.
                inventory.recordReservation(item.getCantidad());
            }
        }

        // 6-7. Crear las líneas y el order, inicialmente en PENDIENTE_PAGO.
        //      Order.confirmFromCart ya construye el order en ese status.
        return Order.confirmFromCart(orderId, cart);
    }
}
