package application.domain.models;

import application.domain.valueobjects.Dinero;
import application.domain.valueobjects.EstadoDevolucion;
import application.domain.valueobjects.EstadoReembolso;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Devolucion representa el proceso mediante el cual un comprador solicita
 * devolver un pedido (o parte de él) de acuerdo con las condiciones del
 * negocio. Un pedido puede originar cero o más devoluciones; una
 * devolución puede generar como máximo un reembolso (Domain Model —
 * NexusMarket, sección 18 / 21).
 */
public class Devolucion {

    private final Long idDevolucion;
    private final Long idPedido;
    private final String motivo;
    private final LocalDate fecha;
    private EstadoDevolucion estado;
    private Reembolso reembolso; // como máximo uno

    public Devolucion(Long idDevolucion, Long idPedido, String motivo, LocalDate fecha, EstadoDevolucion estado) {
        this.idDevolucion = Objects.requireNonNull(idDevolucion, "idDevolucion es obligatorio");
        this.idPedido = Objects.requireNonNull(idPedido, "idPedido es obligatorio");
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("motivo es obligatorio");
        }
        this.motivo = motivo;
        this.fecha = Objects.requireNonNull(fecha, "fecha es obligatoria");
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
    }

    public void cambiarEstado(EstadoDevolucion nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado);
    }

    /** Genera el reembolso asociado. Una devolución solo puede generar uno. */
    public Reembolso generarReembolso(Long idReembolso, Dinero monto, LocalDate fecha, EstadoReembolso estadoInicial) {
        if (this.reembolso != null) {
            throw new IllegalStateException("Esta devolución ya generó un reembolso.");
        }
        this.reembolso = new Reembolso(idReembolso, this.idDevolucion, monto, fecha, estadoInicial);
        return this.reembolso;
    }

    public Long getIdDevolucion() {
        return idDevolucion;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoDevolucion getEstado() {
        return estado;
    }

    public Optional<Reembolso> getReembolso() {
        return Optional.ofNullable(reembolso);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Devolucion that)) return false;
        return Objects.equals(idDevolucion, that.idDevolucion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDevolucion);
    }
}
