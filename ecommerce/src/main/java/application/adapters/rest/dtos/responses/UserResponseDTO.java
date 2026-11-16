package application.adapters.rest.dtos.responses;


public record UserResponseDTO(Long userId, String name, String email, String identityDocument,
        String role, String status) {}
