package application.adapters.rest.dtos.requests;

import application.adapters.rest.dtos.shared.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RegisterSellerRequestDTO(@NotBlank String name, @NotBlank String email,
        @NotBlank String identityDocument, @NotNull @Valid AddressDTO warehouseLocation,
        @NotBlank String password) {}
