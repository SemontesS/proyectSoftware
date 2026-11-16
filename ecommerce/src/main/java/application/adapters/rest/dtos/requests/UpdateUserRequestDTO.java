package application.adapters.rest.dtos.requests;

/** Both fields are optional; null means no change. */
public record UpdateUserRequestDTO(String name, String email) {}
