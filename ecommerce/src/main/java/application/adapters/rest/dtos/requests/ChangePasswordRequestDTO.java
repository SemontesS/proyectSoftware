package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.*;

public record ChangePasswordRequestDTO(@NotBlank String currentPassword, @NotBlank String newPassword) {}
