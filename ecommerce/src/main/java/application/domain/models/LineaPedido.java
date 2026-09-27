package application.domain.models;

import application.domain.valueobjects.Dinero;

import java.util.Objects;

/**
 * LineaPedido representa cada producto incluido en un pedido y es un
 * elemento interno del agregado Pedido (Domain Model — NexusMarket,
 * sección 15).
 */
public class LineaPedido {

    private final Long idProducto;
    private final int cantidad;
    private final Dinero precioUnitario;

    public LineaPedido(Long idProducto, int cantidad, Dinero precioUnitario) {
        this.idProducto = Objects.requireNonNull(idProducto, "idProducto es obligatorio");
        this.precioUnitario = Objects.requireNonNull(precioUnitario, "precioUnitario es obligatorio");
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
