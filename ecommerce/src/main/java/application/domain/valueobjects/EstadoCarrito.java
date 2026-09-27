package application.domain.valueobjects;

/**
 * EstadoCarrito representa la situación actual del carrito.
 *
 * Valores conceptuales — el catálogo definitivo debe mantenerse alineado
 * con las reglas funcionales que se establezcan para el carrito.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 12.
 */
public enum EstadoCarrito {
    ACTIVO,
    CONFIRMADO,
    ABANDONADO
}
