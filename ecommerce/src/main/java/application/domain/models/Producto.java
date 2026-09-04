package application.domain.models;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class Producto {
    private long idProducto;
    private String nombreProducto;
    private String tipoProducto;
    private String variantes;
    private String estado;
    private double precioActual;
}
