package application.adapters.rest.dtos.requests;

import application.adapters.rest.dtos.shared.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record RegisterProductRequestDTO(@NotBlank String name, @NotBlank String type,
        @NotNull @Valid MoneyDTO price, @NotEmpty List<@Valid VariantDTO> variants) {}
