package application.adapters.rest.dtos.requests;

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

/** Both fields are optional; null means no change. */
public record UpdateProductRequestDTO(String name, @Valid MoneyDTO price) {}
