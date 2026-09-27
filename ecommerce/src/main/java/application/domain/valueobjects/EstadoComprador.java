package application.domain.valueobjects;

/**
 * EstadoComprador representa la situación comercial del comprador.
 *
 * La especificación funcional establece que este atributo es obligatorio,
 * pero no entrega un catálogo detallado de valores permitidos. Por ello no
 * se inventan valores adicionales; se modela como un código controlado.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 5.
 */
public record EstadoComprador(String codigo) {

    public EstadoComprador {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El estado del comprador no puede ser vacío.");
        }
        codigo = codigo.trim().toUpperCase();
    }
}
