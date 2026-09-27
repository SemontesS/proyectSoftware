package application.domain.models;

import application.domain.valueobjects.Dinero;
import application.domain.valueobjects.EstadoPedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Pedido es un Aggregate Root (Domain Model — NexusMarket, sección 20).
 * Representa el compromiso comercial generado después de que el comprador
 * confirma su carrito.
 *
 * Regla: un pedido en estado FINALIZADO no puede modificarse
 * (Domain Model — NexusMarket, sección 14 / 21).
 */
public class Pedido {

    private final Long idPedido;
    private final Long idComprador;
    private final LocalDateTime fechaCreacion;
    private EstadoPedido estado;
    private final List<LineaPedido> lineas = new ArrayList<>();
    private final String moneda;

    public static Pedido confirmarDesdeCarrito(Long idPedido, Carrito carrito) {
        if (carrito.getItems().isEmpty()) {
            throw new IllegalStateException("No se puede generar un pedido de un carrito vacío.");
        }
        Pedido pedido = new Pedido(idPedido, carrito.getIdComprador(), LocalDateTime.now(),
                carrito.getTotal().moneda());
        for (ItemCarrito item : carrito.getItems()) {
            pedido.lineas.add(new LineaPedido(item.getIdProducto(), item.getCantidad(), item.getPrecioUnitario()));
        }
        return pedido;
    }

    public Pedido(Long idPedido, Long idComprador, LocalDateTime fechaCreacion, String moneda) {
        this.idPedido = Objects.requireNonNull(idPedido, "idPedido es obligatorio");
        this.idComprador = Objects.requireNonNull(idComprador, "idComprador es obligatorio");
        this.fechaCreacion = Objects.requireNonNullElseGet(fechaCreacion, LocalDateTime::now);
        this.estado = EstadoPedido.PENDIENTE_PAGO;
        this.moneda = Objects.requireNonNull(moneda, "moneda es obligatoria");
    }

    public void confirmarPago() {
        cambiarEstado(EstadoPedido.PAGADO);
    }

    public void despachar() {
        cambiarEstado(EstadoPedido.DESPACHADO);
    }

    public void entregar() {
        cambiarEstado(EstadoPedido.ENTREGADO);
    }

    public void finalizar() {
        cambiarEstado(EstadoPedido.FINALIZADO);
    }

    private void cambiarEstado(EstadoPedido nuevoEstado) {
        validarModificable();
        this.estado = Objects.requireNonNull(nuevoEstado);
    }

    public boolean puedeModificarse() {
        return estado != EstadoPedido.FINALIZADO;
    }

    private void validarModificable() {
        if (!puedeModificarse()) {
            throw new IllegalStateException("Un pedido finalizado no puede modificarse.");
        }
    }

    public Dinero getTotal() {
        return lineas.stream()
                .map(LineaPedido::getSubtotal)
                .reduce(Dinero.of(0, moneda), Dinero::sumar);
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public Long getIdComprador() {
        return idComprador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public List<LineaPedido> getLineas() {
        return List.copyOf(lineas);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido pedido)) return false;
        return Objects.equals(idPedido, pedido.idPedido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido);
    }
}
