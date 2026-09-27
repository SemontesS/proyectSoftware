package application.domain.valueobjects;

/**
 * EstadoVendedor representa la situación actual del vendedor dentro de
 * NexusMarket.
 *
 * La especificación contempla el estado del vendedor, pero no define un
 * catálogo completo de valores permitidos.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 6.
 */
public record EstadoVendedor(String codigo) {

    public EstadoVendedor {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El estado del vendedor no puede ser vacío.");
        }
        codigo = codigo.trim().toUpperCase();
    }
}
