package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.*;

public record UpdateCartItemRequestDTO(@NotBlank String sku, @Positive int quantity) {}
