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

/** Last-issued counter per entity type; the domain needs every ID before the entity is saved. */
@Entity
@Table(name = "id_sequences")
@Getter
@Setter
@NoArgsConstructor
public class IdSequenceJpaEntity {
    @Id
    @Column(name = "name", length = 60)
    private String name;
    @Column(name = "next_value", nullable = false)
    private long nextValue;

    public IdSequenceJpaEntity(String name, long nextValue) {
        this.name = name;
        this.nextValue = nextValue;
    }
}
