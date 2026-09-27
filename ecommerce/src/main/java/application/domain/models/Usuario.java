package application.domain.models;

import application.domain.valueobjects.EstadoUsuario;
import application.domain.valueobjects.RolUsuario;

import java.util.Objects;

/**
 * Usuario representa a una persona que participa en la plataforma.
 * Es una clase abstracta porque reúne la información común que necesitan
 * los diferentes participantes (Comprador, Vendedor).
 *
 * Reglas (Domain Model — NexusMarket, sección 4 / 21):
 * - El identificador del usuario debe ser único.
 * - El correo electrónico debe ser único.
 * - El documento de identidad debe ser único.
 * - Cada usuario tiene un único rol.
 * - El usuario opera de acuerdo con las responsabilidades de su rol.
 */
public abstract class Usuario {

    private final Long idUsuario;
    private String nombre;
    private String correoElectronico;
    private final String documentoIdentidad;
    private final RolUsuario rol;
    private EstadoUsuario estado;

    protected Usuario(Long idUsuario, String nombre, String correoElectronico,
                       String documentoIdentidad, RolUsuario rol, EstadoUsuario estado) {
        this.idUsuario = Objects.requireNonNull(idUsuario, "idUsuario es obligatorio");
        this.rol = Objects.requireNonNull(rol, "rol es obligatorio");
        this.estado = Objects.requireNonNull(estado, "estado es obligatorio");
        setNombre(nombre);
        setCorreoElectronico(correoElectronico);
        this.documentoIdentidad = requireNonBlank(documentoIdentidad, "documentoIdentidad es obligatorio");
    }

    public void setNombre(String nombre) {
        this.nombre = requireNonBlank(nombre, "nombre es obligatorio");
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = requireNonBlank(correoElectronico, "correoElectronico es obligatorio");
    }

    public void cambiarEstado(EstadoUsuario nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado, "estado es obligatorio");
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    private static String requireNonBlank(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(idUsuario, usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}
