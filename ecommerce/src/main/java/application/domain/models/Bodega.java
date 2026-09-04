package application.domain.models;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class Bodega {
    private long idBodega;
    private String ubicacionBodega;
    private String tipoBodega;
}
