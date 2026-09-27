package application.domain.valueobjects;

/**
 * VarianteProducto representa una característica que diferencia una
 * presentación de un producto (por ejemplo Color = Negro, Talla = M).
 *
 * La estructura definitiva podrá ajustarse si los requisitos detallan cómo
 * las variantes afectan el inventario.
 *
 * Fuente: Domain Value Objects — NexusMarket, sección 9.
 */
public record VarianteProducto(String atributo, String valor) {

    public VarianteProducto {
        if (atributo == null || atributo.isBlank()) {
            throw new IllegalArgumentException("El atributo de la variante no puede ser vacío.");
        }
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El valor de la variante no puede ser vacío.");
        }
    }
}
