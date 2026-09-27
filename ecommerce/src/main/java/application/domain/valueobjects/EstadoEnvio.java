package application.domain.valueobjects;

/**
 * EstadoEnvio representa la situación del proceso logístico asociado a un
 * pedido (preparación, despacho, transporte y entrega).
 *
 * El catálogo exacto de estados debe mantenerse alineado con las reglas
 * funcionales de logística cuando se definan.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 14.
 */
public record EstadoEnvio(String codigo) {

    public EstadoEnvio {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El estado del envío no puede ser vacío.");
        }
        codigo = codigo.trim().toUpperCase();
    }
}
