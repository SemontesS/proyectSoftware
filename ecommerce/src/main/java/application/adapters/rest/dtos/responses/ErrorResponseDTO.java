package application.adapters.rest.dtos.responses;


import java.time.Instant;
import java.util.Map;

public record ErrorResponseDTO(Instant timestamp, int status, String error, String message,
        String path, String requestId, Map<String, String> fieldErrors) {}
