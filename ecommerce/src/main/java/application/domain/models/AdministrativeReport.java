package application.domain.models;

import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.UserRole;

import java.util.Map;

/** Read model with consolidated administrative information. */
public record AdministrativeReport(
        Map<UserRole, Long> usersByRole,
        Map<ProductStatus, Long> productsByStatus,
        Map<OrderStatus, Long> ordersByStatus,
        Map<Currency, Money> salesTotals) {
}
