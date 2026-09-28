package com.deepspace.groundstation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice extends BaseEntity {
    public enum Status { DRAFT, ISSUED, PARTIALLY_PAID, PAID, CANCELLED }

    @NotBlank @Column(nullable = false, unique = true) private String invoiceNumber;
    @ManyToOne(fetch = FetchType.LAZY) @NotNull private Customer customer;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "satellite_pass_id", unique = true)
    private SatellitePass satellitePass;
    @NotNull private LocalDate invoiceDate;
    private LocalDate dueDate;
    @NotNull @PositiveOrZero private BigDecimal subtotal = BigDecimal.ZERO;
    @NotNull @PositiveOrZero private BigDecimal tax = BigDecimal.ZERO;
    @NotNull @PositiveOrZero private BigDecimal totalAmount = BigDecimal.ZERO;
    @NotNull @Enumerated(EnumType.STRING) private Status status = Status.DRAFT;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "invoice")
    private List<Payment> payments = new ArrayList<>();

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public SatellitePass getSatellitePass() { return satellitePass; }
    public void setSatellitePass(SatellitePass satellitePass) { this.satellitePass = satellitePass; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
}
