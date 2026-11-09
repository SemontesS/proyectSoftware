package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.adapters.rest.dtos.responses.LoginResponseDTO;
import application.adapters.rest.dtos.responses.SellerRegistrationResponseDTO;
import application.adapters.rest.dtos.responses.SellerResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.Seller;
import application.domain.models.SellerRegistration;
import application.domain.models.User;

public final class UserRestMapper {

    private UserRestMapper() {
    }

    public static UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(user.getUserId(), user.getName(), user.getEmail().value(),
                user.getIdentityDocument().value(), user.getRole().name(), user.getStatus().name());
    }

    public static LoginResponseDTO toLoginDTO(AuthenticationResult result, long expiresInSeconds) {
        return new LoginResponseDTO(result.token(), expiresInSeconds, toDTO(result.user()));
    }

    public static BuyerResponseDTO toBuyerDTO(Buyer buyer) {
        return new BuyerResponseDTO(buyer.getUserId(), buyer.getName(), buyer.getEmail().value(),
                buyer.getIdentityDocument().value(), buyer.getStatus().name(),
                buyer.getCommercialStatus().name(), CommonRestMapper.toDTO(buyer.getPrimaryAddress()),
                buyer.getAdditionalAddresses().stream().map(CommonRestMapper::toDTO).toList());
    }

    public static SellerResponseDTO toSellerDTO(Seller seller) {
        return new SellerResponseDTO(seller.getUserId(), seller.getName(), seller.getEmail().value(),
                seller.getIdentityDocument().value(), seller.getStatus().name(), seller.getSellerStatus().name());
    }

    public static SellerRegistrationResponseDTO toSellerRegistrationDTO(SellerRegistration registration) {
        return new SellerRegistrationResponseDTO(toSellerDTO(registration.seller()),
                WarehouseRestMapper.toDTO(registration.firstWarehouse()));
    }
}
