package application.domain.models;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor

public class LineaPedido{
    private long idLinea;
    private int cantidad;
    private double precioUnitario;

}