package com.ecommerce.domain.valueobjects;

/**
 * TipoProducto
 * ------------
 * Diferencia los productos ofrecidos en Ecommerce según la forma en
 * que son entregados. (Producto.tipoProducto)
 */
public enum TipoProducto implements DomainCatalog {

    FISICO("FISICO", "Físico",
            "Producto que requiere inventario y despacho."),
    DIGITAL("DIGITAL", "Digital",
            "Producto cuya entrega se realiza de forma inmediata después del pago.");

    private final String code;
    private final String name;
    private final String description;

    TipoProducto(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
