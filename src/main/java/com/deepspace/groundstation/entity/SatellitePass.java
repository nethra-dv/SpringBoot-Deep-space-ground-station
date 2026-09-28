package com.deepspace.groundstation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "satellite_passes")
public class SatellitePass extends BaseEntity {
    public enum Status { SCHEDULED, ACTIVE, COMPLETED, CANCELLED }

    @NotBlank private String satelliteName;
    @ManyToOne(fetch = FetchType.LAZY) @NotNull private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY) @NotNull private Dish dish;
    @NotNull private LocalDateTime passStart;
    @NotNull private LocalDateTime passEnd;
    private String frequency;
    @PositiveOrZero private Integer expectedDurationMinutes;
    @NotNull @Enumerated(EnumType.STRING) private Status status = Status.SCHEDULED;

    @JsonIgnore
    @OneToMany(mappedBy = "satellitePass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telemetry> telemetryRecords = new ArrayList<>();

    public String getSatelliteName() { return satelliteName; }
    public void setSatelliteName(String satelliteName) { this.satelliteName = satelliteName; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }
    public LocalDateTime getPassStart() { return passStart; }
    public void setPassStart(LocalDateTime passStart) { this.passStart = passStart; }
    public LocalDateTime getPassEnd() { return passEnd; }
    public void setPassEnd(LocalDateTime passEnd) { this.passEnd = passEnd; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public Integer getExpectedDurationMinutes() { return expectedDurationMinutes; }
    public void setExpectedDurationMinutes(Integer expectedDurationMinutes) { this.expectedDurationMinutes = expectedDurationMinutes; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<Telemetry> getTelemetryRecords() { return telemetryRecords; }
    public void setTelemetryRecords(List<Telemetry> telemetryRecords) { this.telemetryRecords = telemetryRecords; }
}
