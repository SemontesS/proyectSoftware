package com.ecommerce.domain.valueobjects;

/**
 * EstadoUsuario
 * -------------
 * Representa la condición operativa en la que se encuentra un usuario:
 * si está habilitado o presenta alguna restricción para operar.
 * (Usuario.estado)
 */
public enum EstadoUsuario implements DomainCatalog {

    ACTIVO("ACTIVO", "Activo",
            "El usuario se encuentra habilitado para operar dentro del sistema."),
    BLOQUEADO("BLOQUEADO", "Bloqueado",
            "El usuario presenta una restricción y no puede operar dentro del sistema.");

    private final String code;
    private final String name;
    private final String description;

    EstadoUsuario(String code, String name, String description) {
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
