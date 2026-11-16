package application.adapters.rest.dtos.responses;


public record SellerResponseDTO(Long userId, String name, String email, String identityDocument,
        String status, String sellerStatus) {}
