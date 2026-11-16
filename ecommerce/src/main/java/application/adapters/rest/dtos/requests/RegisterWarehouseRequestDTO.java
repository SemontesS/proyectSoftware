package application.adapters.rest.dtos.requests;

import application.adapters.rest.dtos.shared.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RegisterWarehouseRequestDTO(@NotBlank String type, Long sellerId,
        @NotNull @Valid AddressDTO location) {}
