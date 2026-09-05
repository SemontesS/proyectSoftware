package com.ecommerce.domain.valueobjects;

/**
 * TipoBodega
 * ----------
 * Clasifica las bodegas utilizadas para el almacenamiento de los
 * productos. (Bodega.tipoBodega)
 */
public enum TipoBodega implements DomainCatalog {

    BODEGA_MARKETPLACE("BODEGA_MARKETPLACE", "Bodega del Marketplace",
            "Bodega administrada directamente por el marketplace."),
    BODEGA_VENDEDOR("BODEGA_VENDEDOR", "Bodega de Vendedor",
            "Bodega administrada por un vendedor de la plataforma.");

    private final String code;
    private final String name;
    private final String description;

    TipoBodega(String code, String name, String description) {
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
