package application.domain.valueobjects;

/**
 * EstadoPedido representa la etapa del pedido dentro de su ciclo comercial.
 *
 * `CARRITO` no pertenece a este catálogo porque el carrito es un concepto
 * independiente que precede a la creación del pedido.
 *
 * Regla: un pedido en estado FINALIZADO no puede modificarse
 * (ver Pedido#puedeModificarse).
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 13.
 */
public enum EstadoPedido {
    PENDIENTE_PAGO,
    PAGADO,
    DESPACHADO,
    ENTREGADO,
    FINALIZADO
}
