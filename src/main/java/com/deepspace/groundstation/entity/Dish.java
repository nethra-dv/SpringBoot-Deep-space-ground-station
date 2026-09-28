package com.deepspace.groundstation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dishes")
public class Dish extends BaseEntity {
    public enum Status { AVAILABLE, TRACKING, MAINTENANCE, OFFLINE }

    @NotBlank private String dishCode;
    @NotBlank private String dishName;
    private String location;
    @Positive private BigDecimal diameterMeters;
    private String frequencyBand;
    @Enumerated(EnumType.STRING) private Status status = Status.AVAILABLE;
    private BigDecimal maximumElevation;
    private BigDecimal minimumElevation;
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private Vendor servoManufacturer;
    @JsonIgnore
    @OneToMany(mappedBy = "dish")
    private List<SatellitePass> satellitePasses = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "dish")
    private List<Telemetry> telemetryRecords = new ArrayList<>();

    public String getDishCode() { return dishCode; }
    public void setDishCode(String dishCode) { this.dishCode = dishCode; }
    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public BigDecimal getDiameterMeters() { return diameterMeters; }
    public void setDiameterMeters(BigDecimal diameterMeters) { this.diameterMeters = diameterMeters; }
    public String getFrequencyBand() { return frequencyBand; }
    public void setFrequencyBand(String frequencyBand) { this.frequencyBand = frequencyBand; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public BigDecimal getMaximumElevation() { return maximumElevation; }
    public void setMaximumElevation(BigDecimal maximumElevation) { this.maximumElevation = maximumElevation; }
    public BigDecimal getMinimumElevation() { return minimumElevation; }
    public void setMinimumElevation(BigDecimal minimumElevation) { this.minimumElevation = minimumElevation; }
    public Vendor getServoManufacturer() { return servoManufacturer; }
    public void setServoManufacturer(Vendor servoManufacturer) { this.servoManufacturer = servoManufacturer; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<SatellitePass> getSatellitePasses() { return satellitePasses; }
    public void setSatellitePasses(List<SatellitePass> satellitePasses) { this.satellitePasses = satellitePasses; }
    public List<Telemetry> getTelemetryRecords() { return telemetryRecords; }
    public void setTelemetryRecords(List<Telemetry> telemetryRecords) { this.telemetryRecords = telemetryRecords; }
}
