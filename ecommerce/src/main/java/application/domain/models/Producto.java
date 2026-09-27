package application.domain.models;

import application.domain.valueobjects.Dinero;
import application.domain.valueobjects.EstadoProducto;
import application.domain.valueobjects.TipoProducto;
import application.domain.valueobjects.VarianteProducto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Producto representa un bien físico o digital que puede ser ofrecido
 * dentro del catálogo de NexusMarket.
 *
 * Reglas (Domain Model — NexusMarket, sección 7 / 21):
 * - Puede ser físico o digital.
 * - Puede tener variantes.
 * - Tiene un estado dentro del catálogo.
 * - Los productos físicos requieren inventario y despacho.
 * - Los productos digitales tienen entrega inmediata después del pago.
 */
public class Producto {

    private final Long idProducto;
    private final Long idVendedor;
    private String nombreProducto;
    private final TipoProducto tipoProducto;
    private final List<VarianteProducto> variantes = new ArrayList<>();
    private EstadoProducto estado;
    private Dinero precioActual;

    public Producto(Long idProducto, Long idVendedor, String nombreProducto,
                     TipoProducto tipoProducto, EstadoProducto estado, Dinero precioActual) {
        this.idProducto = Objects.requireNonNull(idProducto, "idProducto es obligatorio");
        this.idVendedor = Objects.requireNonNull(idVendedor, "idVendedor es obligatorio");
        this.tipoProducto = Objects.requireNonNull(tipoProducto, "tipoProducto es obligatorio");
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
        this.precioActual = Objects.requireNonNull(precioActual, "precioActual es obligatorio");
        setNombreProducto(nombreProducto);
    }

    public void setNombreProducto(String nombreProducto) {
        if (nombreProducto == null || nombreProducto.isBlank()) {
            throw new IllegalArgumentException("nombreProducto es obligatorio");
        }
        this.nombreProducto = nombreProducto;
    }

    public void agregarVariante(VarianteProducto variante) {
        variantes.add(Objects.requireNonNull(variante));
    }

    public void cambiarEstado(EstadoProducto nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado);
    }

    public void actualizarPrecio(Dinero nuevoPrecio) {
        this.precioActual = Objects.requireNonNull(nuevoPrecio);
    }

    /** true si el producto requiere inventario y despacho (regla de negocio). */
    public boolean requiereInventarioYDespacho() {
        return tipoProducto == TipoProducto.FISICO;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public Long getIdVendedor() {
        return idVendedor;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public List<VarianteProducto> getVariantes() {
        return List.copyOf(variantes);
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public Dinero getPrecioActual() {
        return precioActual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto producto)) return false;
        return Objects.equals(idProducto, producto.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProducto);
    }
}
