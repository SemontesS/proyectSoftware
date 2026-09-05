package com.ecommerce.domain.valueobjects;

/**
 * EstadoEnvio
 * -----------
 * Representa la situación del proceso logístico asociado a un pedido.
 * (Envio.estado)
 */
public enum EstadoEnvio implements DomainCatalog {

    EN_PREPARACION("EN_PREPARACION", "En preparación",
            "El pedido está siendo empacado antes del despacho."),
    DESPACHADO("DESPACHADO", "Despachado",
            "El envío salió de la bodega hacia su destino."),
    EN_TRANSITO("EN_TRANSITO", "En tránsito",
            "El envío se encuentra en proceso de transporte."),
    ENTREGADO("ENTREGADO", "Entregado",
            "La entrega del envío fue confirmada.");

    private final String code;
    private final String name;
    private final String description;

    EstadoEnvio(String code, String name, String description) {
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
