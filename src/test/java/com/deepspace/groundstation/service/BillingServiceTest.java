package com.deepspace.groundstation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deepspace.groundstation.dto.BillingCalculationResponse;
import com.deepspace.groundstation.dto.InvoiceResponse;
import com.deepspace.groundstation.entity.Customer;
import com.deepspace.groundstation.entity.Dish;
import com.deepspace.groundstation.entity.Invoice;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {
    @Mock private SatellitePassRepository satellitePassRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private TelemetryRepository telemetryRepository;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private BillingService billingService;

    private Customer customer;
    private Dish dish;
    private SatellitePass completedPass;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setOperatorName("AstroLink Communications");
        customer.setBillingRatePerGB(new BigDecimal("500"));
        customer.setTrackingRatePerHour(new BigDecimal("2000"));

        dish = new Dish();
        dish.setId(1L);
        dish.setDishCode("DS-01");

        completedPass = new SatellitePass();
        completedPass.setId(1L);
        completedPass.setSatelliteName("ORBIT-7");
        completedPass.setCustomer(customer);
        completedPass.setDish(dish);
        completedPass.setStatus(SatellitePass.Status.COMPLETED);
        completedPass.setPassStart(LocalDateTime.of(2026, 10, 1, 10, 0));
        completedPass.setPassEnd(LocalDateTime.of(2026, 10, 1, 15, 0));

        when(satellitePassRepository.findById(1L)).thenReturn(Optional.of(completedPass));
    }

    @Test
    void calculatesExpectedChargesFor250GbAndFiveHours() {
        stubCustomer();
        when(telemetryRepository.findAllBySatellitePass_Id(1L))
                .thenReturn(List.of(telemetry("250", "5")));

        BillingCalculationResponse result = billingService.calculate(1L);

        assertEquals(new BigDecimal("250"), result.dataReceivedGB());
        assertEquals(new BigDecimal("5"), result.trackingDurationHours());
        assertEquals(new BigDecimal("125000"), result.dataCharge());
        assertEquals(new BigDecimal("10000"), result.trackingCharge());
        assertEquals(new BigDecimal("135000"), result.subtotal());
    }

    @Test
    void aggregatesMultipleTelemetryRecords() {
        stubCustomer();
        when(telemetryRepository.findAllBySatellitePass_Id(1L)).thenReturn(List.of(
                telemetry("100", "2"), telemetry("80", "1.5"), telemetry("70", "1.5")));

        BillingCalculationResponse result = billingService.calculate(1L);

        assertEquals(new BigDecimal("250"), result.dataReceivedGB());
        assertEquals(new BigDecimal("5.0"), result.trackingDurationHours());
        assertEquals(new BigDecimal("135000.0"), result.subtotal());
    }

    @Test
    void rejectsNegativePersistedTelemetryValues() {
        stubCustomer();
        when(telemetryRepository.findAllBySatellitePass_Id(1L))
                .thenReturn(List.of(telemetry("-1", "2")));

        ApiException exception = assertThrows(ApiException.class, () -> billingService.calculate(1L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Telemetry data received GB must be present and cannot be negative.", exception.getMessage());
    }

    @Test
    void failsWhenPassCustomerDoesNotExist() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> billingService.calculate(1L));

        assertEquals("Customer with id 1 not found", exception.getMessage());
        verify(telemetryRepository, never()).findAllBySatellitePass_Id(anyLong());
    }

    @Test
    void invoiceGenerationRejectsScheduledPass() {
        completedPass.setStatus(SatellitePass.Status.SCHEDULED);

        ApiException exception = assertThrows(ApiException.class, () -> billingService.generateInvoice(1L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Billing is available only for a completed satellite pass.", exception.getMessage());
        verify(invoiceRepository, never()).saveAndFlush(any(Invoice.class));
    }

    @Test
    void secondInvoiceGenerationForPassReturnsConflict() {
        stubCustomer();
        when(telemetryRepository.findAllBySatellitePass_Id(1L))
                .thenReturn(List.of(telemetry("250", "5")));
        when(invoiceRepository.existsBySatellitePass_Id(1L)).thenReturn(false, true);
        when(invoiceRepository.existsByInvoiceNumber(any())).thenReturn(false);
        when(productRepository.findByProductCode("DOWNLINK-GB"))
                .thenReturn(Optional.of(product("DOWNLINK-GB", "Orbital Downlink Bandwidth GB")));
        when(productRepository.findByProductCode("TRACK-HOUR"))
                .thenReturn(Optional.of(product("TRACK-HOUR", "Ground Station Tracking Hour")));
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        InvoiceResponse firstInvoice = billingService.generateInvoice(1L);
        assertEquals("INV-" + LocalDate.now().getYear() + "-0001", firstInvoice.invoiceNumber());

        ApiException exception = assertThrows(ApiException.class, () -> billingService.generateInvoice(1L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("An invoice already exists for this satellite pass.", exception.getMessage());
    }

    @Test
    void generatedInvoiceContainsTwoLinesAndThirtyDayDueDate() {
        stubCustomer();
        when(telemetryRepository.findAllBySatellitePass_Id(1L))
                .thenReturn(List.of(telemetry("250", "5")));
        when(invoiceRepository.existsBySatellitePass_Id(1L)).thenReturn(false);
        when(invoiceRepository.existsByInvoiceNumber(any())).thenReturn(false);
        when(productRepository.findByProductCode("DOWNLINK-GB"))
                .thenReturn(Optional.of(product("DOWNLINK-GB", "Orbital Downlink Bandwidth GB")));
        when(productRepository.findByProductCode("TRACK-HOUR"))
                .thenReturn(Optional.of(product("TRACK-HOUR", "Ground Station Tracking Hour")));
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(7L);
            return invoice;
        });

        InvoiceResponse result = billingService.generateInvoice(1L);

        assertEquals(Invoice.Status.ISSUED, result.status());
        assertEquals(new BigDecimal("135000"), result.subtotal());
        assertEquals(BigDecimal.ZERO, result.tax());
        assertEquals(new BigDecimal("135000"), result.totalAmount());
        assertEquals(LocalDate.now().plusDays(30), result.dueDate());
        assertEquals(2, result.items().size());
        assertEquals(new BigDecimal("125000"), result.items().get(0).amount());
        assertEquals(new BigDecimal("10000"), result.items().get(1).amount());
    }

    private Telemetry telemetry(String dataReceivedGB, String trackingDurationHours) {
        Telemetry telemetry = new Telemetry();
        telemetry.setDataReceivedGB(new BigDecimal(dataReceivedGB));
        telemetry.setTrackingDurationHours(new BigDecimal(trackingDurationHours));
        return telemetry;
    }

    private void stubCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    }

    private Product product(String code, String name) {
        Product product = new Product();
        product.setProductCode(code);
        product.setProductName(name);
        return product;
    }
}
