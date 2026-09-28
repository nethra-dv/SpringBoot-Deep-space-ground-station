package com.deepspace.groundstation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendors")
public class Vendor extends BaseEntity {
    @NotBlank private String vendorName;
    private String contactPerson;
    @Email private String email;
    private String phone;
    private String address;
    private boolean active = true;

    @JsonIgnore
    @OneToMany(mappedBy = "servoManufacturer")
    private List<Dish> dishes = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "vendor")
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "vendor")
    private List<VendorBill> bills = new ArrayList<>();

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<Dish> getDishes() { return dishes; }
    public void setDishes(List<Dish> dishes) { this.dishes = dishes; }
    public List<PurchaseOrder> getPurchaseOrders() { return purchaseOrders; }
    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) { this.purchaseOrders = purchaseOrders; }
    public List<VendorBill> getBills() { return bills; }
    public void setBills(List<VendorBill> bills) { this.bills = bills; }
}
