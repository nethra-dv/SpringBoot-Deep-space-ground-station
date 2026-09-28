package com.deepspace.groundstation.service;

import com.deepspace.groundstation.dto.BillingCalculationResponse;
import com.deepspace.groundstation.dto.InvoiceItemResponse;
import com.deepspace.groundstation.dto.InvoiceResponse;
import com.deepspace.groundstation.entity.Customer;
import com.deepspace.groundstation.entity.Dish;
import com.deepspace.groundstation.entity.Invoice;
import com.deepspace.groundstation.entity.InvoiceItem;
import com.deepspace.groundstation.entity.Product;
import com.deepspace.groundstation.entity.SatellitePass;
import com.deepspace.groundstation.entity.Telemetry;
import com.deepspace.groundstation.exception.ApiException;
import com.deepspace.groundstation.exception.ResourceNotFoundException;
import com.deepspace.groundstation.repository.CustomerRepository;
import com.deepspace.groundstation.repository.InvoiceRepository;
import com.deepspace.groundstation.repository.ProductRepository;
import com.deepspace.groundstation.repository.SatellitePassRepository;
import com.deepspace.groundstation.repository.TelemetryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {
    private static final String DOWNLINK_PRODUCT_CODE = "DOWNLINK-GB";
    private static final String TRACKING_PRODUCT_CODE = "TRACK-HOUR";
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final SatellitePassRepository satellitePassRepository;
    private final CustomerRepository customerRepository;
    private final TelemetryRepository telemetryRepository;
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;

    public BillingService(SatellitePassRepository satellitePassRepository,
                          CustomerRepository customerRepository,
                          TelemetryRepository telemetryRepository,
                          InvoiceRepository invoiceRepository,
                          ProductRepository productRepository) {
        this.satellitePassRepository = satellitePassRepository;
        this.customerRepository = customerRepository;
        this.telemetryRepository = telemetryRepository;
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public BillingCalculationResponse calculate(Long passId) {
        return calculationResponse(loadBillingData(passId));
    }

    @Transactional
    public InvoiceResponse generateInvoice(Long passId) {
        BillingData data = loadBillingData(passId);
        if (invoiceRepository.existsBySatellitePass_Id(passId)) {
            throw new ApiException(HttpStatus.CONFLICT, "An invoice already exists for this satellite pass.");
        }

        Product downlinkProduct = findProduct(DOWNLINK_PRODUCT_CODE, "Orbital Downlink Bandwidth");
        Product trackingProduct = findProduct(TRACKING_PRODUCT_CODE, "Ground Station Tracking Hour");
        LocalDate invoiceDate = LocalDate.now();
        BigDecimal tax = ZERO;

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(nextInvoiceNumber(invoiceDate.getYear(), passId));
        invoice.setCustomer(data.customer());
        invoice.setSatellitePass(data.satellitePass());
        invoice.setInvoiceDate(invoiceDate);
        invoice.setDueDate(invoiceDate.plusDays(30));
        invoice.setSubtotal(data.subtotal());
        invoice.setTax(tax);
        invoice.setTotalAmount(data.subtotal().add(tax));
        invoice.setStatus(Invoice.Status.ISSUED);

        List<InvoiceItem> items = new ArrayList<>();
        items.add(invoiceItem(invoice, downlinkProduct, "Orbital Downlink Bandwidth",
                data.dataReceivedGB(), data.billingRatePerGB(), data.dataCharge()));
        items.add(invoiceItem(invoice, trackingProduct, "Ground Station Tracking Hour",
                data.trackingDurationHours(), data.trackingRatePerHour(), data.trackingCharge()));
        invoice.setItems(items);

        try {
            return invoiceResponse(invoiceRepository.saveAndFlush(invoice));
        } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "An invoice already exists for this satellite pass or invoice number.");
        }
    }

    private BillingData loadBillingData(Long passId) {
        SatellitePass satellitePass = satellitePassRepository.findById(passId)
                .orElseThrow(() -> new ResourceNotFoundException("Satellite pass with id " + passId + " not found"));
        if (satellitePass.getStatus() != SatellitePass.Status.COMPLETED) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Billing is available only for a completed satellite pass.");
        }

        if (satellitePass.getCustomer() == null || satellitePass.getCustomer().getId() == null) {
            throw new ResourceNotFoundException("Customer for satellite pass " + passId + " not found");
        }
        Long customerId = satellitePass.getCustomer().getId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customerId + " not found"));
        Dish dish = satellitePass.getDish();
        if (dish == null || dish.getId() == null) {
            throw new ResourceNotFoundException("Dish for satellite pass " + passId + " not found");
        }

        BigDecimal billingRatePerGB = requireNonNegative(customer.getBillingRatePerGB(), "Customer billing rate per GB");
        BigDecimal trackingRatePerHour = requireNonNegative(customer.getTrackingRatePerHour(), "Customer tracking rate per hour");
        List<Telemetry> telemetryRecords = telemetryRepository.findAllBySatellitePass_Id(passId);
        if (telemetryRecords.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No telemetry records exist for this satellite pass.");
        }

        BigDecimal dataReceivedGB = ZERO;
        BigDecimal trackingDurationHours = ZERO;
        for (Telemetry telemetry : telemetryRecords) {
            dataReceivedGB = dataReceivedGB.add(requireNonNegative(telemetry.getDataReceivedGB(), "Telemetry data received GB"));
            trackingDurationHours = trackingDurationHours.add(
                    requireNonNegative(telemetry.getTrackingDurationHours(), "Telemetry tracking duration hours"));
        }

        BigDecimal dataCharge = dataReceivedGB.multiply(billingRatePerGB);
        BigDecimal trackingCharge = trackingDurationHours.multiply(trackingRatePerHour);
        BigDecimal subtotal = dataCharge.add(trackingCharge);
        return new BillingData(satellitePass, customer, dish, dataReceivedGB, trackingDurationHours,
                billingRatePerGB, trackingRatePerHour, dataCharge, trackingCharge, subtotal);
    }

    private BigDecimal requireNonNegative(BigDecimal amount, String fieldName) {
        if (amount == null || amount.compareTo(ZERO) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, fieldName + " must be present and cannot be negative.");
        }
        return amount;
    }

    private Product findProduct(String productCode, String description) {
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Required billing product " + description + " is not configured."));
    }

    private String nextInvoiceNumber(int year, Long passId) {
        long sequence = Math.max(1L, passId);
        String candidate;
        do {
            candidate = String.format(Locale.ROOT, "INV-%d-%04d", year, sequence++);
        } while (invoiceRepository.existsByInvoiceNumber(candidate));
        return candidate;
    }

    private InvoiceItem invoiceItem(Invoice invoice, Product product, String description,
                                    BigDecimal quantity, BigDecimal unitPrice, BigDecimal amount) {
        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setProduct(product);
        item.setDescription(description);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setAmount(amount);
        return item;
    }

    private BillingCalculationResponse calculationResponse(BillingData data) {
        return new BillingCalculationResponse(data.customer().getId(), data.customer().getOperatorName(),
                data.satellitePass().getId(), data.satellitePass().getSatelliteName(), data.dish().getId(),
                data.dish().getDishCode(), data.dataReceivedGB(), data.trackingDurationHours(),
                data.billingRatePerGB(), data.trackingRatePerHour(), data.dataCharge(),
                data.trackingCharge(), data.subtotal());
    }

    private InvoiceResponse invoiceResponse(Invoice invoice) {
        List<InvoiceItemResponse> items = invoice.getItems().stream()
                .map(item -> new InvoiceItemResponse(item.getDescription(), item.getQuantity(),
                        item.getUnitPrice(), item.getAmount()))
                .toList();
        return new InvoiceResponse(invoice.getId(), invoice.getInvoiceNumber(), invoice.getCustomer().getId(),
                invoice.getCustomer().getOperatorName(), invoice.getSatellitePass().getId(), invoice.getInvoiceDate(),
                invoice.getDueDate(), invoice.getSubtotal(), invoice.getTax(), invoice.getTotalAmount(),
                invoice.getStatus(), items);
    }

    private record BillingData(SatellitePass satellitePass, Customer customer, Dish dish,
                               BigDecimal dataReceivedGB, BigDecimal trackingDurationHours,
                               BigDecimal billingRatePerGB, BigDecimal trackingRatePerHour,
                               BigDecimal dataCharge, BigDecimal trackingCharge, BigDecimal subtotal) {
    }
}
