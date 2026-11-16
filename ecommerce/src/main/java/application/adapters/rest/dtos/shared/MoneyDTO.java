package application.adapters.rest.dtos.shared;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record MoneyDTO(@NotNull @PositiveOrZero BigDecimal amount, @NotBlank String currency) {}
