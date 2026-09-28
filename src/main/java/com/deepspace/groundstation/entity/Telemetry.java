package com.deepspace.groundstation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry")
public class Telemetry extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @NotNull @JsonIgnore private SatellitePass satellitePass;
    @ManyToOne(fetch = FetchType.LAZY) @NotNull @JsonIgnore private Dish dish;
    @NotNull @PositiveOrZero private BigDecimal dataReceivedGB;
    @NotNull @PositiveOrZero private BigDecimal trackingDurationHours;
    private BigDecimal elevation;
    private BigDecimal azimuth;
    private BigDecimal signalStrength;
    private BigDecimal motorTemperature;
    @NotNull private LocalDateTime recordedAt;

    public SatellitePass getSatellitePass() { return satellitePass; }
    public void setSatellitePass(SatellitePass satellitePass) { this.satellitePass = satellitePass; }
    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }
    public BigDecimal getDataReceivedGB() { return dataReceivedGB; }
    public void setDataReceivedGB(BigDecimal dataReceivedGB) { this.dataReceivedGB = dataReceivedGB; }
    public BigDecimal getTrackingDurationHours() { return trackingDurationHours; }
    public void setTrackingDurationHours(BigDecimal trackingDurationHours) { this.trackingDurationHours = trackingDurationHours; }
    public BigDecimal getElevation() { return elevation; }
    public void setElevation(BigDecimal elevation) { this.elevation = elevation; }
    public BigDecimal getAzimuth() { return azimuth; }
    public void setAzimuth(BigDecimal azimuth) { this.azimuth = azimuth; }
    public BigDecimal getSignalStrength() { return signalStrength; }
    public void setSignalStrength(BigDecimal signalStrength) { this.signalStrength = signalStrength; }
    public BigDecimal getMotorTemperature() { return motorTemperature; }
    public void setMotorTemperature(BigDecimal motorTemperature) { this.motorTemperature = motorTemperature; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
