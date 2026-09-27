package application.domain.valueobjects;

/**
 * RolUsuario representa la responsabilidad que tiene un usuario dentro de
 * NexusMarket. Cada usuario posee un único rol.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 3.
 */
public enum RolUsuario {
    COMPRADOR("Comprador", "Adquiere productos publicados."),
    VENDEDOR("Vendedor", "Registra y administra productos."),
    ADMINISTRADOR("Administrador", "Administra vendedores y bodegas."),
    OPERADOR_LOGISTICO("Operador Logístico", "Gestiona la operación física y los despachos."),
    SUPERVISOR("Supervisor", "Realiza consultas y seguimiento operativo.");

    private final String nombre;
    private final String responsabilidad;

    RolUsuario(String nombre, String responsabilidad) {
        this.nombre = nombre;
        this.responsabilidad = responsabilidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getResponsabilidad() {
        return responsabilidad;
    }
}
