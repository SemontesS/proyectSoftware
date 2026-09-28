package application.domain.models;

import application.domain.valueobjects.WarehouseType;

import java.util.Objects;

/**
 * Warehouse represents un lugar físico donde se store products.
 * NexusMarket contempla warehouses del marketplace y warehouses associated a
 * vendedores (Domain Model — NexusMarket, sección 9).
 */
public class Warehouse {

    private final Long warehouseId;
    private final Long sellerId; // null si es warehouse del Marketplace
    private String location;
    private final WarehouseType type;

    public Warehouse(Long warehouseId, Long sellerId, String location, WarehouseType type) {
        this.warehouseId = Objects.requireNonNull(warehouseId, "warehouseId es obligatorio");
        this.sellerId = sellerId; // opcional: solo applies a WarehouseType.SELLER
        this.type = Objects.requireNonNull(type, "type es obligatorio");
        setLocation(location);
        if (type == WarehouseType.SELLER && sellerId == null) {
            throw new IllegalArgumentException("A warehouse de type SELLER debe estar associated a un seller.");
        }
    }

    public void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("location es obligatoria");
        }
        this.location = location;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getLocation() {
        return location;
    }

    public WarehouseType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Warehouse warehouse)) return false;
        return Objects.equals(warehouseId, warehouse.warehouseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouseId);
    }
}
