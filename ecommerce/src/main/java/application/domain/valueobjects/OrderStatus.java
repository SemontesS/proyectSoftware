package application.domain.valueobjects;

/**
 * OrderStatus represents la etapa del order dentro de su ciclo commercial.
 *
 * `CART` no pertenece a este catalog porque el cart es un concepto
 * independiente que precede a la creación del order.
 *
 * Rule: un order en status COMPLETED no puede modificarse
 * (ver Order#canBeModified).
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 13.
 */
public enum OrderStatus {
    PENDIENTE_PAGO,
    PAID,
    SHIPPED,
    DELIVERED,
    COMPLETED
}
