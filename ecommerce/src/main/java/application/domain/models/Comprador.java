package application.domain.models;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class Comprador extends Usuario{
    private String direccionPrincipal;
    private List<String> direccionAdicional;
    private String estadoComprador; 
}