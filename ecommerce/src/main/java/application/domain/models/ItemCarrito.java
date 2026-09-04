package application.domain.models;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class ItemCarrito {

    private long idDetalle;
    private int cantidad;
    private double precioUnitario;
}