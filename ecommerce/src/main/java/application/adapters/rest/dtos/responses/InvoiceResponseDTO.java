package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

import java.time.LocalDate;

public record InvoiceResponseDTO(Long invoiceId, Long orderId, String number, LocalDate date,
        MoneyDTO subtotal, MoneyDTO total) {}
