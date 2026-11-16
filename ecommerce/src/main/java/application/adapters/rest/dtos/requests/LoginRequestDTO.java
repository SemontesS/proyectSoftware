package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.*;

public record LoginRequestDTO(@NotBlank String email, @NotBlank String password) {}
