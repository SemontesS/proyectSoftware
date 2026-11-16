package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.*;

public record RegisterInitialInventoryRequestDTO(@NotNull Long productId, @NotBlank String sku,
        @NotNull Long warehouseId, @PositiveOrZero int initialQuantity) {}
