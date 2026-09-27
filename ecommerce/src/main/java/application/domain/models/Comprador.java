package application.domain.models;

import application.domain.valueobjects.Direccion;
import application.domain.valueobjects.EstadoComprador;
import application.domain.valueobjects.EstadoUsuario;
import application.domain.valueobjects.RolUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Comprador representa al participante que adquiere productos dentro de
 * NexusMarket.
 *
 * Reglas (Domain Model — NexusMarket, sección 5 / 21):
 * - Debe contar con una dirección principal.
 * - Puede registrar direcciones adicionales.
 * - Puede utilizar un carrito activo (cero o uno).
 * - Puede realizar pedidos.
 */
public class Comprador extends Usuario {

    private Direccion direccionPrincipal;
    private final List<Direccion> direccionesAdicionales = new ArrayList<>();
    private EstadoComprador estadoComercial;

    public Comprador(Long idUsuario, String nombre, String correoElectronico,
                      String documentoIdentidad, EstadoUsuario estado,
                      Direccion direccionPrincipal, EstadoComprador estadoComercial) {
        super(idUsuario, nombre, correoElectronico, documentoIdentidad, RolUsuario.COMPRADOR, estado);
        this.direccionPrincipal = Objects.requireNonNull(direccionPrincipal, "direccionPrincipal es obligatoria");
        this.estadoComercial = Objects.requireNonNull(estadoComercial, "estadoComercial es obligatorio");
    }

    public void agregarDireccionAdicional(Direccion direccion) {
        direccionesAdicionales.add(Objects.requireNonNull(direccion));
    }

    public void cambiarDireccionPrincipal(Direccion direccion) {
        this.direccionPrincipal = Objects.requireNonNull(direccion);
    }

    public void cambiarEstadoComercial(EstadoComprador estadoComercial) {
        this.estadoComercial = Objects.requireNonNull(estadoComercial);
    }

    public Direccion getDireccionPrincipal() {
        return direccionPrincipal;
    }

    public List<Direccion> getDireccionesAdicionales() {
        return List.copyOf(direccionesAdicionales);
    }

    public EstadoComprador getEstadoComercial() {
        return estadoComercial;
    }
}
