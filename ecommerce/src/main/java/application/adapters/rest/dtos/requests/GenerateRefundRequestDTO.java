package application.adapters.rest.dtos.requests;

import application.adapters.rest.dtos.shared.MoneyDTO;
import jakarta.validation.Valid;

/** The amount is optional: when omitted, the full price of the returned product is refunded. */
public record GenerateRefundRequestDTO(@Valid MoneyDTO amount) {}
