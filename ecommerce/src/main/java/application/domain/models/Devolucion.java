package application.domain.models;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Devolucion {
    private long idDevolucion;
    private String motivo;
    private Date fecha;
    private String estado;
}
