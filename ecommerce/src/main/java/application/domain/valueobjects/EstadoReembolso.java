package com.ecommerce.domain.valueobjects;

/**
 * EstadoReembolso
 * ---------------
 * Representa la situación en la que se encuentra un reembolso asociado
 * a una devolución. (Reembolso.estado)
 *
 */
public enum EstadoReembolso implements DomainCatalog {

    PENDIENTE("PENDIENTE", "Pendiente",
            "El reembolso todavía no ha sido procesado."),
    PROCESADO("PROCESADO", "Procesado",
            "El reembolso fue realizado al comprador."),
    RECHAZADO("RECHAZADO", "Rechazado",
            "El reembolso no procede.");

    private final String code;
    private final String name;
    private final String description;

    EstadoReembolso(String code, String name, String description) {
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
