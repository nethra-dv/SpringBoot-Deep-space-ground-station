package com.deepspace.groundstation.config;

import com.deepspace.groundstation.entity.Account;
import com.deepspace.groundstation.entity.AnalyticAccount;
import com.deepspace.groundstation.entity.Customer;
import com.deepspace.groundstation.entity.Dish;
import com.deepspace.groundstation.entity.Product;
import com.deepspace.groundstation.entity.Vendor;
import com.deepspace.groundstation.repository.AccountRepository;
import com.deepspace.groundstation.repository.AnalyticAccountRepository;
import com.deepspace.groundstation.repository.CustomerRepository;
import com.deepspace.groundstation.repository.DishRepository;
import com.deepspace.groundstation.repository.ProductRepository;
import com.deepspace.groundstation.repository.VendorRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private final DishRepository dishRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final AnalyticAccountRepository analyticAccountRepository;

    public DataInitializer(CustomerRepository customerRepository,
                           VendorRepository vendorRepository,
                           DishRepository dishRepository,
                           ProductRepository productRepository,
                           AccountRepository accountRepository,
                           AnalyticAccountRepository analyticAccountRepository) {
        this.customerRepository = customerRepository;
        this.vendorRepository = vendorRepository;
        this.dishRepository = dishRepository;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
        this.analyticAccountRepository = analyticAccountRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initializeCustomers();
        initializeVendors();
        initializeDishes();
        initializeProducts();
        initializeAccounts();
        initializeAnalyticAccount();
    }

    private void initializeCustomers() {
        if (customerRepository.count() != 0) return;
        customerRepository.saveAll(List.of(
                customer("AstroLink Communications"),
                customer("OrbitalNet Systems")));
    }

    private Customer customer(String name) {
        Customer customer = new Customer();
        customer.setOperatorName(name);
        customer.setBillingRatePerGB(new BigDecimal("12.50"));
        customer.setTrackingRatePerHour(new BigDecimal("180.00"));
        customer.setActive(true);
        return customer;
    }

    private void initializeVendors() {
        if (vendorRepository.count() != 0) return;
        Vendor first = vendor("SpaceServo Technologies");
        Vendor second = vendor("OrbitDrive Industries");
        vendorRepository.saveAll(List.of(first, second));
    }

    private Vendor vendor(String name) {
        Vendor vendor = new Vendor();
        vendor.setVendorName(name);
        vendor.setActive(true);
        return vendor;
    }

    private void initializeDishes() {
        if (dishRepository.count() != 0) return;
        Vendor manufacturer = vendorRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No servo manufacturer is available"));
        dishRepository.saveAll(List.of(
                dish("DS-01", "Deep-Space Dish 01", manufacturer),
                dish("DS-02", "Deep-Space Dish 02", manufacturer)));
    }

    private Dish dish(String code, String name, Vendor manufacturer) {
        Dish dish = new Dish();
        dish.setDishCode(code);
        dish.setDishName(name);
        dish.setLocation("Ground Station");
        dish.setDiameterMeters(new BigDecimal("18.00"));
        dish.setFrequencyBand("X-band");
        dish.setStatus(Dish.Status.AVAILABLE);
        dish.setMinimumElevation(new BigDecimal("5.00"));
        dish.setMaximumElevation(new BigDecimal("90.00"));
        dish.setServoManufacturer(manufacturer);
        dish.setActive(true);
        return dish;
    }

    private void initializeProducts() {
        if (productRepository.count() != 0) return;
        productRepository.saveAll(List.of(
                product("DOWNLINK-GB", "Orbital Downlink Bandwidth GB", "Received satellite downlink data", Product.Type.SERVICE, "GB", "12.50"),
                product("TRACK-HOUR", "Ground Station Tracking Hour", "Antenna tracking time", Product.Type.SERVICE, "hour", "180.00"),
                product("SERVO-DRIVE", "Servo Drive Replacement", "Replacement antenna servo drive", Product.Type.GOOD, "unit", "2500.00")));
    }

    private Product product(String code, String name, String description, Product.Type type, String unit, String price) {
        Product product = new Product();
        product.setProductCode(code);
        product.setProductName(name);
        product.setDescription(description);
        product.setType(type);
        product.setUnit(unit);
        product.setUnitPrice(new BigDecimal(price));
        product.setActive(true);
        return product;
    }

    private void initializeAccounts() {
        if (accountRepository.count() != 0) return;
        accountRepository.saveAll(List.of(
                account("Parabolic Antenna Array", Account.Category.ASSET),
                account("Signal Processing Equipment", Account.Category.ASSET),
                account("Cash", Account.Category.ASSET),
                account("Bank", Account.Category.ASSET),
                account("Servo Vendor Creditors", Account.Category.LIABILITY),
                account("Power Utility Payables", Account.Category.LIABILITY),
                account("Downlink Telemetry Revenues", Account.Category.INCOME),
                account("Satellite Command Fees", Account.Category.INCOME),
                account("Antenna Power Expenditures", Account.Category.EXPENSE),
                account("Mechanical Servo Maintenance", Account.Category.EXPENSE)));
    }

    private Account account(String name, Account.Category category) {
        Account account = new Account();
        account.setAccountName(name);
        account.setCategory(category);
        return account;
    }

    private void initializeAnalyticAccount() {
        String name = "Deep-Space Dish Unit 1";
        if (analyticAccountRepository.existsByName(name)) return;

        AnalyticAccount analyticAccount = new AnalyticAccount();
        analyticAccount.setName(name);
        analyticAccount.setCode("DS-DISH-01");
        analyticAccount.setDescription("Operational budget and cost center for Deep-Space Dish 01");
        analyticAccount.setActive(true);
        analyticAccountRepository.save(analyticAccount);
    }
}
