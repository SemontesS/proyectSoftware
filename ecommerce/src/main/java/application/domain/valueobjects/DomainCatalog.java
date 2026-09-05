package com.ecommerce.domain.valueobjects;

/**
 * DomainCatalog
 * -------------
 * Estructura general para los Value Objects que manejan valores
 * controlados dentro del dominio (roles, estados, tipos).
 *
 */
public interface DomainCatalog {

    /** Código utilizado para identificar el valor (ej: "PUBLICADO"). */
    String getCode();

    /** Nombre del valor (ej: "Publicado"). */
    String getName();

    /** Descripción del significado del valor. */
    String getDescription();
}
