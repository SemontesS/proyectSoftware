package application.domain.models;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public abstract class Usuario {
    
    private long id_user;
    private String name;
    private String email;
    private String rolUsuario;
    private String statusUser;
    
    
}
