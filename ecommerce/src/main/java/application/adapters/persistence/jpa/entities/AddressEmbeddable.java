package application.adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Address columns; each owning entity renames them with @AttributeOverrides. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class AddressEmbeddable {
    @Column(name = "address_type", length = 20)
    private String type;
    @Column(name = "address_detail", length = 255)
    private String detail;
    @Column(name = "address_city", length = 100)
    private String city;

    public AddressEmbeddable(String type, String detail, String city) {
        this.type = type;
        this.detail = detail;
        this.city = city;
    }
}
