package com.deepspace.groundstation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TelemetryRequest(
        @NotNull Long satellitePassId,
        @NotNull Long dishId,
        @NotNull @PositiveOrZero BigDecimal dataReceivedGB,
        @NotNull @PositiveOrZero BigDecimal trackingDurationHours,
        @PositiveOrZero BigDecimal elevation,
        @PositiveOrZero BigDecimal azimuth,
        BigDecimal signalStrength,
        BigDecimal motorTemperature,
        @NotNull LocalDateTime recordedAt) {
}
