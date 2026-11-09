package application.infrastructure.seed;

import application.domain.models.InternalUser;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.models.SellerRegistration;
import application.domain.models.Shipment;
import application.domain.models.UserCredentials;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdministratorPort;
import application.domain.ports.in.BuyerPort;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.ports.in.PublicAccessPort;
import application.domain.ports.in.SellerPort;
import application.domain.ports.out.CredentialsRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.AddressType;
import application.domain.valueobjects.BuyerStatus;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.RawPassword;
import application.domain.valueobjects.Sku;
import application.domain.valueobjects.UserRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseStatus;
import application.domain.valueobjects.WarehouseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Loads demo data on startup so that every endpoint has something to work with: one account per
 * role, products in every status, distributed stock, and orders at every stage of the purchase
 * flow (pending payment, paid, shipped, delivered) with returns and refunds.
 *
 * <p>The data is created through the input ports, so every domain rule applies and the audit
 * trail is recorded. Idempotent: it does nothing if the administrator already exists. A single
 * password is used for every account: {@value #PASSWORD}.
 */
@Component
@org.springframework.core.annotation.Order(1)
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    static final String PASSWORD = "Password123*";
    static final String ADMIN_EMAIL = "admin@nexusmarket.com";

    private final UserRepositoryPort userRepositoryPort;
    private final CredentialsRepositoryPort credentialsRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final PublicAccessPort publicAccessPort;
    private final AdministratorPort administratorPort;
    private final SellerPort sellerPort;
    private final BuyerPort buyerPort;
    private final LogisticsOperatorPort logisticsPort;
    private final boolean enabled;

    public DatabaseSeeder(UserRepositoryPort userRepositoryPort,
                          CredentialsRepositoryPort credentialsRepositoryPort,
                          PasswordServicePort passwordServicePort,
                          PublicAccessPort publicAccessPort,
                          AdministratorPort administratorPort,
                          SellerPort sellerPort,
                          BuyerPort buyerPort,
                          LogisticsOperatorPort logisticsPort,
                          @Value("${app.seed.enabled:true}") boolean enabled) {
        this.userRepositoryPort = userRepositoryPort;
        this.credentialsRepositoryPort = credentialsRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.publicAccessPort = publicAccessPort;
        this.administratorPort = administratorPort;
        this.sellerPort = sellerPort;
        this.buyerPort = buyerPort;
        this.logisticsPort = logisticsPort;
        this.enabled = enabled;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!enabled) {
            log.info("Demo data disabled (app.seed.enabled=false).");
            return;
        }
        if (userRepositoryPort.existsByEmail(new Email(ADMIN_EMAIL))) {
            log.info("Demo data already loaded: skipping.");
            return;
        }
        log.info("Loading demo data...");

        // ---- Staff ----
        InternalUser admin = seedAdministrator();
        InternalUser logistics = administratorPort.registerInternalUser(admin, "Laura Logistica",
                new Email("logistica@nexusmarket.com"), new IdentityDocument("LOG-0001"),
                UserRole.LOGISTICS_OPERATOR, password());
        administratorPort.registerInternalUser(admin, "Sergio Supervisor",
                new Email("supervisor@nexusmarket.com"), new IdentityDocument("SUP-0001"),
                UserRole.SUPERVISOR, password());

        // ---- Sellers and warehouses (the first warehouse is created with the seller) ----
        SellerRegistration techRegistration = administratorPort.registerSeller(admin, "TechStore Medellin",
                new Email("tienda.tech@nexusmarket.com"), new IdentityDocument("SEL-1001"),
                address("Calle 10 # 20-30", "Bello"), password());
        SellerRegistration fashionRegistration = administratorPort.registerSeller(admin, "Moda Urbana",
                new Email("tienda.moda@nexusmarket.com"), new IdentityDocument("SEL-1002"),
                address("Carrera 45 # 30-12", "Medellin"), password());
        Warehouse techBello = techRegistration.firstWarehouse();
        Warehouse fashionMain = fashionRegistration.firstWarehouse();
        Warehouse techItagui = administratorPort.registerWarehouse(admin, WarehouseType.SELLER,
                techRegistration.seller().getUserId(), address("Carrera 50 # 10-20", "Itagui"));
        Warehouse central = administratorPort.registerWarehouse(admin, WarehouseType.MARKETPLACE, null,
                address("Zona Franca, bodega 4", "Medellin"));
        Warehouse oldWarehouse = administratorPort.registerWarehouse(admin, WarehouseType.MARKETPLACE, null,
                address("Calle 5 # 8-10", "Envigado"));
        administratorPort.changeWarehouseStatus(admin, oldWarehouse.getWarehouseId(), WarehouseStatus.INACTIVE);

        // ---- Buyers ----
        Buyer ana = publicAccessPort.registerBuyer("Ana Gomez", new Email("ana.gomez@mail.com"),
                new IdentityDocument("1001234567"), address("Calle 70 # 52-10", "Medellin"), password());
        Buyer luis = publicAccessPort.registerBuyer("Luis Perez", new Email("luis.perez@mail.com"),
                new IdentityDocument("1007654321"), address("Carrera 80 # 33-45", "Envigado"), password());
        Buyer carla = publicAccessPort.registerBuyer("Carla Restringida", new Email("carla.restringida@mail.com"),
                new IdentityDocument("1003333333"), address("Calle 30 # 10-15", "Sabaneta"), password());
        administratorPort.changeBuyerCommercialStatus(admin, carla.getUserId(), BuyerStatus.RESTRICTED);

        // ---- Catalog: every product status ----
        Product laptop = sellerPort.registerProduct(techRegistration.seller(), "Laptop Pro 14", ProductType.PHYSICAL,
                cop(4_500_000), List.of(variant("LAP14-8GB", "RAM", "8 GB"), variant("LAP14-16GB", "RAM", "16 GB")));
        Product headphones = sellerPort.registerProduct(techRegistration.seller(), "Auriculares Bluetooth",
                ProductType.PHYSICAL, cop(180_000), List.of(variant("AUR-BT-NEG", "Color", "Negro")));
        Product course = sellerPort.registerProduct(techRegistration.seller(), "Curso de Java (digital)",
                ProductType.DIGITAL, cop(120_000), List.of(variant("CURSO-JAVA", "Formato", "Video")));
        Product shirt = sellerPort.registerProduct(fashionRegistration.seller(), "Camiseta basica",
                ProductType.PHYSICAL, cop(45_000), List.of(variant("CAM-M-AZ", "Talla", "M azul"),
                        variant("CAM-L-AZ", "Talla", "L azul")));
        Product jacket = sellerPort.registerProduct(fashionRegistration.seller(), "Chaqueta de cuero",
                ProductType.PHYSICAL, cop(350_000), List.of(variant("CHA-M-NE", "Talla", "M negra")));
        Product cap = sellerPort.registerProduct(fashionRegistration.seller(), "Gorra edicion limitada",
                ProductType.PHYSICAL, cop(60_000), List.of(variant("GOR-UN", "Talla", "Unica")));
        sellerPort.registerProduct(fashionRegistration.seller(), "Zapatillas deportivas", ProductType.PHYSICAL,
                cop(280_000), List.of(variant("ZAP-42", "Talla", "42")));       // left SUSPENDED: not published yet
        for (Product product : List.of(laptop, headphones, course)) {
            sellerPort.publishProduct(techRegistration.seller(), product.getProductId());
        }
        for (Product product : List.of(shirt, jacket)) {
            sellerPort.publishProduct(fashionRegistration.seller(), product.getProductId());
        }
        sellerPort.suspendProduct(fashionRegistration.seller(), jacket.getProductId());           // PUBLISHED -> SUSPENDED
        sellerPort.discontinueProduct(fashionRegistration.seller(), cap.getProductId());          // DISCONTINUED

        // ---- Inventory: the laptop is split across two warehouses ----
        var tech = techRegistration.seller();
        var fashion = fashionRegistration.seller();
        sellerPort.registerInitialInventory(tech, laptop.getProductId(), new Sku("LAP14-8GB"), techBello.getWarehouseId(), 5);
        sellerPort.registerInitialInventory(tech, laptop.getProductId(), new Sku("LAP14-8GB"), techItagui.getWarehouseId(), 3);
        sellerPort.registerInitialInventory(tech, laptop.getProductId(), new Sku("LAP14-16GB"), techBello.getWarehouseId(), 2);
        sellerPort.registerInitialInventory(tech, headphones.getProductId(), new Sku("AUR-BT-NEG"), techBello.getWarehouseId(), 50);
        administratorPort.registerInitialInventory(admin, headphones.getProductId(), new Sku("AUR-BT-NEG"), central.getWarehouseId(), 20);
        sellerPort.registerInitialInventory(fashion, shirt.getProductId(), new Sku("CAM-M-AZ"), fashionMain.getWarehouseId(), 40);
        sellerPort.registerInitialInventory(fashion, shirt.getProductId(), new Sku("CAM-L-AZ"), fashionMain.getWarehouseId(), 25);
        sellerPort.registerInitialInventory(fashion, jacket.getProductId(), new Sku("CHA-M-NE"), fashionMain.getWarehouseId(), 8);

        // ---- Order A (Ana): delivered and completed, with a return waiting to be received ----
        buyerPort.addProductToCart(ana, laptop.getProductId(), new Sku("LAP14-8GB"), 1);
        buyerPort.addProductToCart(ana, headphones.getProductId(), new Sku("AUR-BT-NEG"), 2);
        Order orderA = buyerPort.createOrder(ana);
        buyerPort.payOrder(ana, orderA.getOrderId());
        buyerPort.generateInvoice(ana, orderA.getOrderId());
        for (Shipment shipment : logisticsPort.prepareShipment(logistics, orderA.getOrderId())) {
            logisticsPort.dispatchShipment(logistics, shipment.getShipmentId());
            logisticsPort.confirmDelivery(logistics, shipment.getShipmentId());
        }
        buyerPort.requestReturn(ana, orderA.getOrderId(), "El producto no cumple mis expectativas");

        // ---- Order B (Luis): paid, shipment being prepared ----
        buyerPort.addProductToCart(luis, shirt.getProductId(), new Sku("CAM-M-AZ"), 2);
        Order orderB = buyerPort.createOrder(luis);
        buyerPort.payOrder(luis, orderB.getOrderId());
        buyerPort.generateInvoice(luis, orderB.getOrderId());
        logisticsPort.prepareShipment(logistics, orderB.getOrderId());

        // ---- Order C (Ana): waiting for payment ----
        buyerPort.addProductToCart(ana, headphones.getProductId(), new Sku("AUR-BT-NEG"), 1);
        buyerPort.createOrder(ana);

        // ---- Order D (Luis): digital, completed on payment; return received, refund pending ----
        buyerPort.addProductToCart(luis, course.getProductId(), new Sku("CURSO-JAVA"), 1);
        Order orderD = buyerPort.createOrder(luis);
        buyerPort.payOrder(luis, orderD.getOrderId());
        buyerPort.generateInvoice(luis, orderD.getOrderId());
        Return returnD = buyerPort.requestReturn(luis, orderD.getOrderId(), "Compre el curso dos veces");
        logisticsPort.receiveReturn(logistics, returnD.getReturnId());
        administratorPort.generateRefund(admin, returnD.getReturnId(), cop(120_000));

        // ---- Order E (Luis): shipped, waiting for the delivery confirmation ----
        buyerPort.addProductToCart(luis, laptop.getProductId(), new Sku("LAP14-16GB"), 1);
        Order orderE = buyerPort.createOrder(luis);
        buyerPort.payOrder(luis, orderE.getOrderId());
        buyerPort.generateInvoice(luis, orderE.getOrderId());
        for (Shipment shipment : logisticsPort.prepareShipment(logistics, orderE.getOrderId())) {
            logisticsPort.dispatchShipment(logistics, shipment.getShipmentId());
        }

        log.info("Demo data loaded. All accounts use the password '{}': admin@nexusmarket.com, "
                        + "logistica@nexusmarket.com, supervisor@nexusmarket.com, tienda.tech@nexusmarket.com, "
                        + "tienda.moda@nexusmarket.com, ana.gomez@mail.com, luis.perez@mail.com, "
                        + "carla.restringida@mail.com (restricted buyer).", PASSWORD);
    }

    /** The first administrator cannot be registered by anyone else, so it is created directly. */
    private InternalUser seedAdministrator() {
        InternalUser admin = new InternalUser(userRepositoryPort.nextId(), "Administrador NexusMarket",
                new Email(ADMIN_EMAIL), new IdentityDocument("ADM-0001"), UserRole.ADMINISTRATOR, UserStatus.ACTIVE);
        userRepositoryPort.save(admin);
        credentialsRepositoryPort.save(UserCredentials.create(admin.getUserId(), passwordServicePort.encrypt(password())));
        return admin;
    }

    private static RawPassword password() {
        return new RawPassword(PASSWORD);
    }

    private static Address address(String detail, String city) {
        return new Address(AddressType.HOME, detail, city);
    }

    private static Money cop(long amount) {
        return Money.of(amount, Currency.COP);
    }

    private static ProductVariant variant(String sku, String attribute, String value) {
        return new ProductVariant(new Sku(sku), attribute, value);
    }
}
