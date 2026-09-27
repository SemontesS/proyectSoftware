package application.domain.models;

import application.domain.valueobjects.TipoBodega;

import java.util.Objects;

/**
 * Bodega representa un lugar físico donde se almacenan productos.
 * NexusMarket contempla bodegas del marketplace y bodegas asociadas a
 * vendedores (Domain Model — NexusMarket, sección 9).
 */
public class Bodega {

    private final Long idBodega;
    private final Long idVendedor; // null si es bodega del Marketplace
    private String ubicacion;
    private final TipoBodega tipo;

    public Bodega(Long idBodega, Long idVendedor, String ubicacion, TipoBodega tipo) {
        this.idBodega = Objects.requireNonNull(idBodega, "idBodega es obligatorio");
        this.idVendedor = idVendedor; // opcional: solo aplica a TipoBodega.VENDEDOR
        this.tipo = Objects.requireNonNull(tipo, "tipo es obligatorio");
        setUbicacion(ubicacion);
        if (tipo == TipoBodega.VENDEDOR && idVendedor == null) {
            throw new IllegalArgumentException("Una bodega de tipo VENDEDOR debe estar asociada a un vendedor.");
        }
    }

    public void setUbicacion(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            throw new IllegalArgumentException("ubicacion es obligatoria");
        }
        this.ubicacion = ubicacion;
    }

    public Long getIdBodega() {
        return idBodega;
    }

    public Long getIdVendedor() {
        return idVendedor;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public TipoBodega getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bodega bodega)) return false;
        return Objects.equals(idBodega, bodega.idBodega);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBodega);
    }
}
