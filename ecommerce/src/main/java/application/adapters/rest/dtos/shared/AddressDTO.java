package application.adapters.rest.dtos.shared;

import jakarta.validation.constraints.*;

public record AddressDTO(@NotBlank String type, @NotBlank String detail, @NotBlank String city) {}
