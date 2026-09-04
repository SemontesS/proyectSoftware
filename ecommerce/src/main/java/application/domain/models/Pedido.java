package application.domain.models;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Pedido {
    
    private long idPedido;
    private Date fechaCreacion;
    private String estado;
    private double total;
    
}
