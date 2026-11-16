package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(Long orderId, Long buyerId, String status, LocalDateTime createdAt,
        String currency, List<OrderLineResponseDTO> lines, MoneyDTO total) {}
