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

/** One table for every user type; the role column tells which domain class to rebuild. */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserJpaEntity {
    @Id
    private Long id;
    @Version
    private Long version;
    @Column(name = "role", nullable = false, length = 30)
    private String role;
    @Column(name = "name", nullable = false, length = 120)
    private String name;
    @Column(name = "email", nullable = false, unique = true, length = 180)
    private String email;
    @Column(name = "identity_document", nullable = false, unique = true, length = 60)
    private String identityDocument;
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // Buyer only
    @Column(name = "commercial_status", length = 20)
    private String commercialStatus;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "primary_address_type", length = 20)),
            @AttributeOverride(name = "detail", column = @Column(name = "primary_address_detail", length = 255)),
            @AttributeOverride(name = "city", column = @Column(name = "primary_address_city", length = 100))})
    private AddressEmbeddable primaryAddress;
    @ElementCollection
    @CollectionTable(name = "buyer_additional_addresses", joinColumns = @JoinColumn(name = "buyer_id"))
    @OrderColumn(name = "position")
    private List<AddressEmbeddable> additionalAddresses = new ArrayList<>();

    // Seller only
    @Column(name = "seller_status", length = 20)
    private String sellerStatus;
}
