package com.ecommerce.domain.valueobjects;

/**
 * EstadoComprador
 * ---------------
 * Representa la condición comercial del comprador frente a las
 * operaciones que puede realizar en la plataforma. (Comprador.estadoComprador)
 *
 * NOTA: el documento fuente indica que este atributo es obligatorio,
 * pero no define un catálogo detallado de valores. Los valores abajo
 * son una propuesta razonable y pueden ajustarse a la especificación
 * funcional real.
 */
public enum EstadoComprador implements DomainCatalog {

    ACTIVO("ACTIVO", "Activo",
            "El comprador puede realizar compras con normalidad."),
    SUSPENDIDO("SUSPENDIDO", "Suspendido",
            "El comprador tiene restringida temporalmente la posibilidad de comprar."),
    INACTIVO("INACTIVO", "Inactivo",
            "El comprador no se encuentra habilitado dentro de la plataforma.");

    private final String code;
    private final String name;
    private final String description;

    EstadoComprador(String code, String name, String description) {
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
