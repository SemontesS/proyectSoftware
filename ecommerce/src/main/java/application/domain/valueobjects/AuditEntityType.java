package application.domain.valueobjects;

/** Kind of business entity an audit entry refers to. */
public enum AuditEntityType {
    BUYER,
    CART,
    INVENTORY,
    INVOICE,
    ORDER,
    PRODUCT,
    RETURN,
    SELLER,
    SHIPMENT,
    USER,
    WAREHOUSE
}
