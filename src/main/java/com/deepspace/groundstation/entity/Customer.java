package com.deepspace.groundstation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {
    @NotBlank private String operatorName;
    private String contactPerson;
    @Email private String email;
    private String phone;
    private String address;
    @NotNull @PositiveOrZero private BigDecimal billingRatePerGB;
    @NotNull @PositiveOrZero private BigDecimal trackingRatePerHour;
    private boolean active = true;

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<SatellitePass> satellitePasses = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<SalesOrder> salesOrders = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<Invoice> invoices = new ArrayList<>();

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BigDecimal getBillingRatePerGB() { return billingRatePerGB; }
    public void setBillingRatePerGB(BigDecimal billingRatePerGB) { this.billingRatePerGB = billingRatePerGB; }
    public BigDecimal getTrackingRatePerHour() { return trackingRatePerHour; }
    public void setTrackingRatePerHour(BigDecimal trackingRatePerHour) { this.trackingRatePerHour = trackingRatePerHour; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<SatellitePass> getSatellitePasses() { return satellitePasses; }
    public void setSatellitePasses(List<SatellitePass> satellitePasses) { this.satellitePasses = satellitePasses; }
    public List<SalesOrder> getSalesOrders() { return salesOrders; }
    public void setSalesOrders(List<SalesOrder> salesOrders) { this.salesOrders = salesOrders; }
    public List<Invoice> getInvoices() { return invoices; }
    public void setInvoices(List<Invoice> invoices) { this.invoices = invoices; }
}
