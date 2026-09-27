package application.domain.models;

import application.domain.valueobjects.Dinero;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Factura representa la información comercial generada como resultado de
 * un pedido. Un Pedido genera una Factura (Domain Model — NexusMarket,
 * sección 16).
 */
public class Factura {

    private final Long idFactura;
    private final Long idPedido;
    private final String numero;
    private final LocalDate fecha;
    private final Dinero subtotal;
    private final Dinero total;

    public Factura(Long idFactura, Long idPedido, String numero, LocalDate fecha,
                    Dinero subtotal, Dinero total) {
        this.idFactura = Objects.requireNonNull(idFactura, "idFactura es obligatorio");
        this.idPedido = Objects.requireNonNull(idPedido, "idPedido es obligatorio");
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("numero es obligatorio");
        }
        this.numero = numero;
        this.fecha = Objects.requireNonNull(fecha, "fecha es obligatoria");
        this.subtotal = Objects.requireNonNull(subtotal, "subtotal es obligatorio");
        this.total = Objects.requireNonNull(total, "total es obligatorio");
    }

    public Long getIdFactura() {
        return idFactura;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public String getNumero() {
        return numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Dinero getSubtotal() {
        return subtotal;
    }

    public Dinero getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Factura factura)) return false;
        return Objects.equals(idFactura, factura.idFactura);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFactura);
    }
}
