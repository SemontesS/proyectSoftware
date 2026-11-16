package application.adapters.rest.dtos.requests;

import application.adapters.rest.dtos.shared.*;
import jakarta.validation.Valid;

/** Both fields are optional; null means no change. */
public record UpdateProductRequestDTO(String name, @Valid MoneyDTO price) {}
