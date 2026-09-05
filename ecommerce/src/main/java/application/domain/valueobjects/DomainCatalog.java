package com.ecommerce.domain.valueobjects;

/**
 * DomainCatalog
 * -------------
 * Estructura general para los Value Objects que manejan valores
 * controlados dentro del dominio (roles, estados, tipos).
 *
 * Todo enum que represente un catálogo del dominio (RolUsuario,
 * EstadoProducto, TipoProducto, etc.) implementa esta interfaz para
 * garantizar que siempre exponga code, name y description.
 */
public interface DomainCatalog {

    /** Código utilizado para identificar el valor (ej: "PUBLICADO"). */
    String getCode();

    /** Nombre del valor (ej: "Publicado"). */
    String getName();

    /** Descripción del significado del valor. */
    String getDescription();
}
