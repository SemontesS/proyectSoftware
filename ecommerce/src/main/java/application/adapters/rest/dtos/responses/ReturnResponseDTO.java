package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;
import application.adapters.rest.dtos.responses.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ReturnResponseDTO(Long returnId, Long orderId, String reason, LocalDate date,
        String status, RefundResponseDTO refund) {}
