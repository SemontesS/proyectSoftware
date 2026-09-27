package application.domain.models;

import application.domain.valueobjects.Direccion;
import application.domain.valueobjects.EstadoEnvio;

import java.util.Objects;

/**
 * Envio representa el proceso logístico mediante el cual un pedido es
 * preparado, despachado, transportado y entregado (Domain Model —
 * NexusMarket, sección 17). Un pedido puede requerir cero o un envío; una
 * bodega puede despachar cero o más envíos.
 */
public class Envio {

    private final Long idEnvio;
    private final Long idPedido;
    private final Long idBodega;
    private EstadoEnvio estado;
    private final Direccion direccionEntrega;

    public Envio(Long idEnvio, Long idPedido, Long idBodega, EstadoEnvio estado, Direccion direccionEntrega) {
        this.idEnvio = Objects.requireNonNull(idEnvio, "idEnvio es obligatorio");
        this.idPedido = Objects.requireNonNull(idPedido, "idPedido es obligatorio");
        this.idBodega = Objects.requireNonNull(idBodega, "idBodega es obligatorio");
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
        this.direccionEntrega = Objects.requireNonNull(direccionEntrega, "direccionEntrega es obligatoria");
    }

    public void cambiarEstado(EstadoEnvio nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado);
    }

    public Long getIdEnvio() {
        return idEnvio;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public Long getIdBodega() {
        return idBodega;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public Direccion getDireccionEntrega() {
        return direccionEntrega;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Envio envio)) return false;
        return Objects.equals(idEnvio, envio.idEnvio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEnvio);
    }
}
