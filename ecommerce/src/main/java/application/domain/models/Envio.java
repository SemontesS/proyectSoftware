package application.domain.models;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Envio {
    
    private long idEnvio;
    private String estado;
    private String direccionDeEntrega;
}
