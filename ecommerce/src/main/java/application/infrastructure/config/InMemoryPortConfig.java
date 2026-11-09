package application.infrastructure.config;

import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Inventory;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.UserCredentials;
import application.domain.models.Warehouse;
import application.domain.valueobjects.CartStatus;
import application.domain.ports.out.AuditLogRepositoryPort;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.CredentialsRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.JwtServicePort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.InvoiceNumber;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.Sku;
import application.domain.valueobjects.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class InMemoryPortConfig {

    @Bean
    public UserRepositoryPort userRepositoryPort() {
        Map<Long, User> users = new ConcurrentHashMap<>();
        return new UserRepositoryPort() {
            @Override
            public Long nextId() {
                return (long) (users.size() + 1);
            }

            @Override
            public <T extends User> T save(T user) {
                users.put(user.getUserId(), user);
                return user;
            }

            @Override
            public Optional<User> findById(Long userId) {
                return Optional.ofNullable(users.get(userId));
            }

            @Override
            public Optional<User> findByEmail(Email email) {
                return users.values().stream()
                        .filter(user -> user.getEmail().equals(email))
                        .findFirst();
            }

            @Override
            public Optional<Buyer> findBuyerById(Long userId) {
                return Optional.ofNullable(users.get(userId))
                        .filter(Buyer.class::isInstance)
                        .map(Buyer.class::cast);
            }

            @Override
            public Optional<Seller> findSellerById(Long userId) {
                return Optional.ofNullable(users.get(userId))
                        .filter(Seller.class::isInstance)
                        .map(Seller.class::cast);
            }

            @Override
            public boolean existsByEmail(Email email) {
                return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
            }

            @Override
            public boolean existsByIdentityDocument(IdentityDocument identityDocument) {
                return users.values().stream().anyMatch(user -> user.getIdentityDocument().equals(identityDocument));
            }

            @Override
            public Map<UserRole, Long> countByRole() {
                return Collections.emptyMap();
            }
        };
    }

    @Bean
    public CredentialsRepositoryPort credentialsRepositoryPort() {
        Map<Long, UserCredentials> credentialsByUserId = new ConcurrentHashMap<>();
        return new CredentialsRepositoryPort() {
            @Override
            public UserCredentials save(UserCredentials credentials) {
                credentialsByUserId.put(credentials.getUserId(), credentials);
                return credentials;
            }

            @Override
            public Optional<UserCredentials> findByUserId(Long userId) {
                return Optional.ofNullable(credentialsByUserId.get(userId));
            }
        };
    }

    @Bean
    public PasswordServicePort passwordServicePort() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return new PasswordServicePort() {
            @Override
            public String encrypt(RawPassword rawPassword) {
                return encoder.encode(rawPassword.value());
            }

            @Override
            public boolean matches(RawPassword rawPassword, String passwordHash) {
                return encoder.matches(rawPassword.value(), passwordHash);
            }
        };
    }

    @Bean
    public JwtServicePort jwtServicePort() {
        return new JwtServicePort() {
            @Override
            public String generateToken(User user, int tokenVersion) {
                return user.getUserId() + ":" + tokenVersion;
            }

            @Override
            public Optional<TokenClaims> parseToken(String token) {
                if (token == null || token.isBlank()) {
                    return Optional.empty();
                }
                String[] parts = token.split(":");
                if (parts.length != 2) {
                    return Optional.empty();
                }
                try {
                    return Optional.of(new TokenClaims(Long.parseLong(parts[0]), Integer.parseInt(parts[1])));
                } catch (NumberFormatException ex) {
                    return Optional.empty();
                }
            }
        };
    }

    @Bean
    public CartRepositoryPort cartRepositoryPort() {
        Map<Long, Cart> carts = new ConcurrentHashMap<>();
        return new CartRepositoryPort() {
            @Override
            public Long nextId() {
                return (long) (carts.size() + 1);
            }

            @Override
            public Cart save(Cart cart) {
                carts.put(cart.getCartId(), cart);
                return cart;
            }

            @Override
            public Optional<Cart> findActiveByBuyerId(Long buyerId) {
                return carts.values().stream()
                        .filter(cart -> cart.getBuyerId().equals(buyerId) && cart.getStatus() == CartStatus.ACTIVE)
                        .findFirst();
            }
        };
    }

    @Bean
    public InventoryRepositoryPort inventoryRepositoryPort() {
        Map<Long, Inventory> inventories = new ConcurrentHashMap<>();
        return new InventoryRepositoryPort() {
            @Override
            public Long nextId() {
                return (long) (inventories.size() + 1);
            }

            @Override
            public Inventory save(Inventory inventory) {
                inventories.put(inventory.getInventoryId(), inventory);
                return inventory;
            }

            @Override
            public Optional<Inventory> findById(Long inventoryId) {
                return Optional.ofNullable(inventories.get(inventoryId));
            }

            @Override
            public List<Inventory> findByProductId(Long productId) {
                return inventories.values().stream()
                        .filter(inv -> inv.getProductId().equals(productId))
                        .toList();
            }

            @Override
            public List<Inventory> findByProductAndSku(Long productId, Sku sku) {
                return inventories.values().stream()
                        .filter(inv -> inv.getProductId().equals(productId) && inv.getSku().equals(sku))
                        .toList();
            }

            @Override
            public Optional<Inventory> findByProductSkuAndWarehouse(Long productId, Sku sku, Long warehouseId) {
                return inventories.values().stream()
                        .filter(inv -> inv.getProductId().equals(productId)
                                && inv.getSku().equals(sku)
                                && inv.getWarehouseId().equals(warehouseId))
                        .findFirst();
            }

            @Override
            public List<Inventory> findByWarehouseId(Long warehouseId) {
                return inventories.values().stream()
                        .filter(inv -> inv.getWarehouseId().equals(warehouseId))
                        .toList();
            }
        };
    }

    @Bean
    public InvoiceRepositoryPort invoiceRepositoryPort() {
        Map<Long, Invoice> invoices = new ConcurrentHashMap<>();
        return new InvoiceRepositoryPort() {
            @Override
            public Long nextId() {
                return (long) (invoices.size() + 1);
            }

            @Override
            public InvoiceNumber nextInvoiceNumber() {
                return new InvoiceNumber("INV-" + (invoices.size() + 1));
            }

            @Override
            public Invoice save(Invoice invoice) {
                invoices.put(invoice.getInvoiceId(), invoice);
                return invoice;
            }

            @Override
            public Optional<Invoice> findByOrderId(Long orderId) {
                return invoices.values().stream()
                        .filter(invoice -> invoice.getOrderId().equals(orderId))
                        .findFirst();
            }
        };
    }

    @Bean
    public OrderRepositoryPort orderRepositoryPort() {
        Map<Long, Order> orders = new ConcurrentHashMap<>();
        return new OrderRepositoryPort() {
            @Override
            public Long nextId() {
                return (long) (orders.size() + 1);
            }

            @Override
            public Order save(Order order) {
                orders.put(order.getOrderId(), order);
                return order;
            }

            @Override
            public Optional<Order> findById(Long orderId) {
                return Optional.ofNullable(orders.get(orderId));
            }

            @Override
            public List<Order> findByBuyerId(Long buyerId) {
                return orders.values().stream()
                        .filter(order -> order.getBuyerId().equals(buyerId))
                        .toList();
            }

            @Override
            public Map<OrderStatus, Long> countByStatus() {
                return Collections.emptyMap();
            }

            @Override
            public Map<Currency, Money> salesTotalsByCurrency() {
                return Collections.emptyMap();
            }
        };
    }

    @Bean
    public ProductRepositoryPort productRepositoryPort() {
        Map<Long, Product> products = new ConcurrentHashMap<>();
        return new ProductRepositoryPort() {
            @Override
            public Long nextId() {
                return (long) (products.size() + 1);
            }

            @Override
            public Product save(Product product) {
                products.put(product.getProductId(), product);
                return product;
            }

            @Override
            public Optional<Product> findById(Long productId) {
                return Optional.ofNullable(products.get(productId));
            }

            @Override
            public List<Product> findAll() {
                return List.copyOf(products.values());
            }

            @Override
            public List<Product> findByStatus(ProductStatus status) {
                return products.values().stream()
                        .filter(product -> product.getStatus() == status)
                        .toList();
            }

            @Override
            public Map<ProductStatus, Long> countByStatus() {
                return Collections.emptyMap();
            }
        };
    }

    @Bean
    public ReturnRepositoryPort returnRepositoryPort() {
        Map<Long, Return> returns = new ConcurrentHashMap<>();
        return new ReturnRepositoryPort() {
            @Override
            public Long nextId() {
                return (long) (returns.size() + 1);
            }

            @Override
            public Long nextRefundId() {
                return (long) (returns.size() + 1);
            }

            @Override
            public Return save(Return returnRequest) {
                returns.put(returnRequest.getReturnId(), returnRequest);
                return returnRequest;
            }

            @Override
            public Optional<Return> findById(Long returnId) {
                return Optional.ofNullable(returns.get(returnId));
            }

            @Override
            public List<Return> findByOrderId(Long orderId) {
                return returns.values().stream()
                        .filter(ret -> ret.getOrderId().equals(orderId))
                        .toList();
            }
        };
    }

    @Bean
    public ShipmentRepositoryPort shipmentRepositoryPort() {
        Map<Long, Shipment> shipments = new ConcurrentHashMap<>();
        return new ShipmentRepositoryPort() {
            @Override
            public Long nextId() {
                return (long) (shipments.size() + 1);
            }

            @Override
            public Shipment save(Shipment shipment) {
                shipments.put(shipment.getShipmentId(), shipment);
                return shipment;
            }

            @Override
            public Optional<Shipment> findById(Long shipmentId) {
                return Optional.ofNullable(shipments.get(shipmentId));
            }

            @Override
            public List<Shipment> findByOrderId(Long orderId) {
                return shipments.values().stream()
                        .filter(shipment -> shipment.getOrderId().equals(orderId))
                        .toList();
            }
        };
    }

    @Bean
    public WarehouseRepositoryPort warehouseRepositoryPort() {
        Map<Long, Warehouse> warehouses = new ConcurrentHashMap<>();
        return new WarehouseRepositoryPort() {
            @Override
            public Long nextId() {
                return (long) (warehouses.size() + 1);
            }

            @Override
            public Warehouse save(Warehouse warehouse) {
                warehouses.put(warehouse.getWarehouseId(), warehouse);
                return warehouse;
            }

            @Override
            public Optional<Warehouse> findById(Long warehouseId) {
                return Optional.ofNullable(warehouses.get(warehouseId));
            }

            @Override
            public List<Warehouse> findBySellerId(Long sellerId) {
                return warehouses.values().stream()
                        .filter(warehouse -> warehouse.getSellerId().equals(sellerId))
                        .toList();
            }
        };
    }

    @Bean
    public AuditLogRepositoryPort auditLogRepositoryPort() {
        Map<Long, AuditLog> logs = new ConcurrentHashMap<>();
        return new AuditLogRepositoryPort() {
            @Override
            public AuditLog save(AuditLog auditLog) {
                logs.put(Long.parseLong(auditLog.auditId()), auditLog);
                return auditLog;
            }

            @Override
            public List<AuditLog> findByUserId(Long userId) {
                return logs.values().stream()
                        .filter(log -> log.userId().equals(userId))
                        .toList();
            }

            @Override
            public List<AuditLog> findByEntity(String entityType, Long entityId) {
                return logs.values().stream()
                        .filter(log -> log.entityType().equals(entityType)
                                && log.entityId().equals(entityId))
                        .toList();
            }
        };
    }
}
