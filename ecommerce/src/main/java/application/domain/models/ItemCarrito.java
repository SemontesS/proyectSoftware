package application.domain.models;

import application.domain.valueobjects.Dinero;

import java.util.Objects;

/**
 * ItemCarrito representa un producto seleccionado dentro de un carrito y es
 * un elemento interno del agregado Carrito (Domain Model — NexusMarket,
 * sección 13).
 */
public class ItemCarrito {

    private final Long idProducto;
    private int cantidad;
    private final Dinero precioUnitario;

    public ItemCarrito(Long idProducto, int cantidad, Dinero precioUnitario) {
        this.idProducto = Objects.requireNonNull(idProducto, "idProducto es obligatorio");
        this.precioUnitario = Objects.requireNonNull(precioUnitario, "precioUnitario es obligatorio");
        setCantidad(cantidad);
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("cantidad debe ser mayor a cero.");
        }
        this.cantidad = cantidad;
    }

    public Dinero getSubtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Dinero getPrecioUnitario() {
        return precioUnitario;
    }
}
