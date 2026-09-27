package application.domain.valueobjects;

/**
 * TipoProducto diferencia los productos según la forma en que se entregan.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 7.
 */
public enum TipoProducto {
    FISICO("Físico", "Requiere inventario y despacho."),
    DIGITAL("Digital", "Tiene entrega inmediata después del pago.");

    private final String nombre;
    private final String descripcion;

    TipoProducto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
