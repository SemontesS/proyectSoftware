package application.domain.valueobjects;

/**
 * CartStatus represents la situación current del cart.
 *
 * Valores conceptuales — el catalog definitivo debe mantenerse alineado
 * with las reglas funcionales que se establezcan para el cart.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 12.
 */
public enum CartStatus {
    ACTIVE,
    CONFIRMED,
    ABANDONED
}
