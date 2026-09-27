package application.domain.models;

import application.domain.valueobjects.Dinero;
import application.domain.valueobjects.EstadoCarrito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Carrito es un Aggregate Root (Domain Model — NexusMarket, sección 20).
 * Representa el espacio temporal donde el comprador selecciona productos
 * antes de confirmar la compra.
 *
 * Regla: un comprador puede tener cero o un carrito activo
 * (esa unicidad se aplica a nivel de caso de uso / repositorio, no dentro
 * de esta entidad).
 */
public class Carrito {

    private final Long idCarrito;
    private final Long idComprador;
    private final LocalDateTime fechaCreacion;
    private EstadoCarrito estado;
    private final List<ItemCarrito> items = new ArrayList<>();
    private final String moneda;

    public Carrito(Long idCarrito, Long idComprador, LocalDateTime fechaCreacion, String moneda) {
        this.idCarrito = Objects.requireNonNull(idCarrito, "idCarrito es obligatorio");
        this.idComprador = Objects.requireNonNull(idComprador, "idComprador es obligatorio");
        this.fechaCreacion = Objects.requireNonNullElseGet(fechaCreacion, LocalDateTime::now);
        this.estado = EstadoCarrito.ACTIVO;
        this.moneda = Objects.requireNonNull(moneda, "moneda es obligatoria");
    }

    public void agregarItem(ItemCarrito item) {
        validarActivo();
        items.add(Objects.requireNonNull(item));
    }

    public void quitarItem(Long idProducto) {
        validarActivo();
        items.removeIf(i -> i.getIdProducto().equals(idProducto));
    }

    /** Confirma el carrito, dejándolo listo para generar un Pedido. */
    public void confirmar() {
        validarActivo();
        if (items.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar un carrito sin productos.");
        }
        this.estado = EstadoCarrito.CONFIRMADO;
    }

    public void abandonar() {
        this.estado = EstadoCarrito.ABANDONADO;
    }

    public Dinero getTotal() {
        return items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(Dinero.of(0, moneda), Dinero::sumar);
    }

    private void validarActivo() {
        if (estado != EstadoCarrito.ACTIVO) {
            throw new IllegalStateException("El carrito no está activo.");
        }
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public Long getIdComprador() {
        return idComprador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoCarrito getEstado() {
        return estado;
    }

    public List<ItemCarrito> getItems() {
        return List.copyOf(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carrito carrito)) return false;
        return Objects.equals(idCarrito, carrito.idCarrito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCarrito);
    }
}
