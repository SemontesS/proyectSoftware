package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.*;

public record RegisterInternalUserRequestDTO(@NotBlank String name, @NotBlank String email,
        @NotBlank String identityDocument, @NotBlank String role, @NotBlank String password) {}
