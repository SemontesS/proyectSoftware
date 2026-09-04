package application.domain.models;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

public class Carrito {

    private long idCarrito;
    private Date fecha;
    private double total;
    private String estado;
}
