package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.CartItemResponseDTO;
import application.adapters.rest.dtos.responses.CartResponseDTO;
import application.domain.models.Cart;
import application.domain.models.CartItem;

public final class CartRestMapper {

    private CartRestMapper() {
    }

    public static CartResponseDTO toDTO(Cart cart) {
        return new CartResponseDTO(cart.getCartId(), cart.getBuyerId(), cart.getStatus().name(),
                cart.getCurrency().name(), cart.getItems().stream().map(CartRestMapper::toItemDTO).toList(),
                CommonRestMapper.toDTO(cart.getTotal()));
    }

    private static CartItemResponseDTO toItemDTO(CartItem item) {
        return new CartItemResponseDTO(item.getProductId(), item.getSku().value(), item.getQuantity(),
                CommonRestMapper.toDTO(item.getUnitPrice()), CommonRestMapper.toDTO(item.getSubtotal()));
    }
}
