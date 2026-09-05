package com.ecommerce.domain.valueobjects;

/**
 * RolUsuario
 * ----------
 * Representa el papel que cumple una persona dentro de Ecommerce.
 * Cada usuario tiene un único rol dentro del sistema (Usuario.rolUsuario).
 */
public enum RolUsuario implements DomainCatalog {

    COMPRADOR("COMPRADOR", "Comprador",
            "Usuario que adquiere productos publicados."),
    VENDEDOR("VENDEDOR", "Vendedor",
            "Responsable de registrar y administrar productos."),
    OPERADOR_LOGISTICO("OPERADOR_LOGISTICO", "Operador Logístico",
            "Responsable de la operación física de bodegas y despachos."),
    ADMINISTRADOR("ADMINISTRADOR", "Administrador",
            "Responsable de administrar vendedores y bodegas."),
    SUPERVISOR("SUPERVISOR", "Supervisor",
            "Perfil encargado de consulta y seguimiento operativo.");

    private final String code;
    private final String name;
    private final String description;

    RolUsuario(String code, String name, String description) {
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
