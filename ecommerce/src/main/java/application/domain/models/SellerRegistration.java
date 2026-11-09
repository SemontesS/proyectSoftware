package application.domain.models;

/** Result of onboarding a seller: the seller and its first warehouse. */
public record SellerRegistration(Seller seller, Warehouse firstWarehouse) {
}
