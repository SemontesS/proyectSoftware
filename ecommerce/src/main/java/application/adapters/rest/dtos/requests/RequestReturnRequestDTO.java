package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Return of one product (and SKU) of a delivered order. */
public record RequestReturnRequestDTO(@NotNull Long productId, @NotBlank String sku, @NotBlank String reason) {}
