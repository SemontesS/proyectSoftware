package application.adapters.rest.dtos.responses;


import java.time.LocalDate;

public record ReturnResponseDTO(Long returnId, Long orderId, Long productId, String sku, Long sellerId, int quantity, String reason, LocalDate date,
        String status, RefundResponseDTO refund) {}
