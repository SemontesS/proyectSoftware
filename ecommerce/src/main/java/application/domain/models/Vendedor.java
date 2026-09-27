package application.domain.models;

import application.domain.valueobjects.EstadoUsuario;
import application.domain.valueobjects.EstadoVendedor;
import application.domain.valueobjects.RolUsuario;

import java.util.Objects;

/**
 * Vendedor representa al participante encargado de registrar y administrar
 * los productos que comercializa dentro de NexusMarket.
 *
 * Reglas (Domain Model — NexusMarket, sección 6 / 21):
 * - Debe ser incorporado por el Administrador (no puede auto-registrarse;
 *   la creación se orquesta desde el caso de uso correspondiente, no desde
 *   esta entidad).
 * - Puede registrar y administrar productos.
 * - Puede trabajar con las bodegas asociadas.
 */
public class Vendedor extends Usuario {

    private EstadoVendedor estadoVendedor;

    public Vendedor(Long idUsuario, String nombre, String correoElectronico,
                     String documentoIdentidad, EstadoUsuario estado,
                     EstadoVendedor estadoVendedor) {
        super(idUsuario, nombre, correoElectronico, documentoIdentidad, RolUsuario.VENDEDOR, estado);
        this.estadoVendedor = Objects.requireNonNull(estadoVendedor, "estadoVendedor es obligatorio");
    }

    public void cambiarEstadoVendedor(EstadoVendedor estadoVendedor) {
        this.estadoVendedor = Objects.requireNonNull(estadoVendedor);
    }

    public EstadoVendedor getEstadoVendedor() {
        return estadoVendedor;
    }
}
