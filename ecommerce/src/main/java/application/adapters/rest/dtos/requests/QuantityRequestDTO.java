package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.*;

public record QuantityRequestDTO(@Positive int quantity) {}
