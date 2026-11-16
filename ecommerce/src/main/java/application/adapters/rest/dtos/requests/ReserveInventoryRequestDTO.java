package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.*;

public record ReserveInventoryRequestDTO(@NotNull Long productId, @NotBlank String sku, @Positive int quantity) {}
