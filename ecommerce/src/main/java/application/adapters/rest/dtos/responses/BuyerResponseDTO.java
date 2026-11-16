package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

import java.util.List;

public record BuyerResponseDTO(Long userId, String name, String email, String identityDocument,
        String status, String commercialStatus, AddressDTO primaryAddress,
        List<AddressDTO> additionalAddresses) {}
