package application.adapters.rest.dtos.responses;


import java.time.LocalDateTime;

public record MovementResponseDTO(Long movementId, String type, int quantity, LocalDateTime date) {}
