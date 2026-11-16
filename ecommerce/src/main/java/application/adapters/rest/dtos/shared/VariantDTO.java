package application.adapters.rest.dtos.shared;

import jakarta.validation.constraints.*;

public record VariantDTO(@NotBlank String sku, @NotBlank String attribute, @NotBlank String value) {}
