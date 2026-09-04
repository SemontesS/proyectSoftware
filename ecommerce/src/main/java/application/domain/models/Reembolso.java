package application.domain.models;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Reembolso {
    
    private long idReembolso;
    private double monto;
    private Date fecha;
    private String estado;
}
