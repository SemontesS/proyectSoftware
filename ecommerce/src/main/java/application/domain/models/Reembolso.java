package application.domain.models;

import application.domain.valueobjects.Dinero;
import application.domain.valueobjects.EstadoReembolso;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Reembolso representa la devolución del dinero asociada a una devolución
 * procesada. Una Devolucion puede generar como máximo un Reembolso
 * (Domain Model — NexusMarket, sección 19).
 */
public class Reembolso {

    private final Long idReembolso;
    private final Long idDevolucion;
    private final Dinero monto;
    private final LocalDate fecha;
    private EstadoReembolso estado;

    public Reembolso(Long idReembolso, Long idDevolucion, Dinero monto, LocalDate fecha, EstadoReembolso estado) {
        this.idReembolso = Objects.requireNonNull(idReembolso, "idReembolso es obligatorio");
        this.idDevolucion = Objects.requireNonNull(idDevolucion, "idDevolucion es obligatorio");
        this.monto = Objects.requireNonNull(monto, "monto es obligatorio");
        this.fecha = Objects.requireNonNull(fecha, "fecha es obligatoria");
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
    }

    public void cambiarEstado(EstadoReembolso nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado);
    }

    public Long getIdReembolso() {
        return idReembolso;
    }

    public Long getIdDevolucion() {
        return idDevolucion;
    }

    public Dinero getMonto() {
        return monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoReembolso getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reembolso that)) return false;
        return Objects.equals(idReembolso, that.idReembolso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReembolso);
    }
}
