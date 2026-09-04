package application.domain.models;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class factura {
    private long idFactura;
    private String numero;
    private Date fecha;
    private double subTotal;
    private double total;
}
