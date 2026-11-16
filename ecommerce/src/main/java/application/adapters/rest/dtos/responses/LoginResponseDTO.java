package application.adapters.rest.dtos.responses;


public record LoginResponseDTO(String token, long expiresIn, UserResponseDTO user) {}
