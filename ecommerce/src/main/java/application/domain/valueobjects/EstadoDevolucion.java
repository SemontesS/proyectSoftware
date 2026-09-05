package com.ecommerce.domain.valueobjects;

/**
 * EstadoDevolucion
 * ----------------
 * Representa la situación de una devolución asociada a un pedido, desde
 * que se solicita hasta que finaliza. (Devolucion.estado)
 *
 * NOTA: la especificación funcional consultada no define un catálogo
 * detallado de valores para este estado. Los valores abajo son una
 * propuesta razonable y deben ajustarse a la especificación funcional
 * real cuando esté disponible.
 */
public enum EstadoDevolucion implements DomainCatalog {

    SOLICITADA("SOLICITADA", "Solicitada",
            "El comprador solicitó la devolución del producto."),
    EN_REVISION("EN_REVISION", "En revisión",
            "La solicitud de devolución está siendo evaluada."),
    APROBADA("APROBADA", "Aprobada",
            "La devolución fue aprobada y continúa su proceso."),
    RECHAZADA("RECHAZADA", "Rechazada",
            "La devolución fue rechazada."),
    FINALIZADA("FINALIZADA", "Finalizada",
            "El proceso de devolución se completó.");

    private final String code;
    private final String name;
    private final String description;

    EstadoDevolucion(String code, String name, String description) {
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
