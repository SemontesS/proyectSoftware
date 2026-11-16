package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class VariantEmbeddable {
    @Column(name = "sku", nullable = false, length = 64)
    private String sku;
    @Column(name = "variant_attribute", nullable = false, length = 100)
    private String attribute;
    @Column(name = "variant_value", nullable = false, length = 100)
    private String value;

    public VariantEmbeddable(String sku, String attribute, String value) {
        this.sku = sku;
        this.attribute = attribute;
        this.value = value;
    }
}
