package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.*;

public record StatusRequestDTO(@NotBlank String status) {}
