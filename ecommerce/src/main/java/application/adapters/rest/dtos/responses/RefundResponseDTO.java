package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

import java.time.LocalDate;

public record RefundResponseDTO(Long refundId, Long returnId, MoneyDTO amount, LocalDate date,
        String status) {}
