package com.ecommerce.domain.valueobjects;

import java.util.Objects;

/**
 * VarianteProducto
 * ----------------
 * Representa una característica que permite diferenciar una
 * presentación de un producto (por ejemplo: color, talla o modelo).
 *
 * A diferencia de RolUsuario, TipoProducto, etc., no es un catálogo de
 * valores fijos, sino un Value Object simple de tipo/valor. Producto
 * mantiene una lista de variantes (Producto.variantes -> List<VarianteProducto>).
 *
 * Ejemplos:
 *   new VarianteProducto("Color", "Negro")
 *   new VarianteProducto("Talla", "M")
 *   new VarianteProducto("Modelo", "2026")
 */
public record VarianteProducto(String tipo, String valor) {

    public VarianteProducto {
        Objects.requireNonNull(tipo, "El tipo de variante no puede ser nulo.");
        Objects.requireNonNull(valor, "El valor de la variante no puede ser nulo.");
        if (tipo.isBlank() || valor.isBlank()) {
            throw new IllegalArgumentException("El tipo y el valor de la variante no pueden estar vacíos.");
        }
    }

    @Override
    public String toString() {
        return tipo + ": " + valor;
    }
}
