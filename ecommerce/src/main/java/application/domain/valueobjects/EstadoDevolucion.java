package application.domain.valueobjects;

/**
 * EstadoDevolucion representa la situación de una devolución asociada a un
 * pedido, desde la solicitud hasta su finalización.
 *
 * La especificación contempla devoluciones pero no entrega un catálogo
 * detallado de estados.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 15.
 */
public record EstadoDevolucion(String codigo) {

    public EstadoDevolucion {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El estado de la devolución no puede ser vacío.");
        }
        codigo = codigo.trim().toUpperCase();
    }
}
