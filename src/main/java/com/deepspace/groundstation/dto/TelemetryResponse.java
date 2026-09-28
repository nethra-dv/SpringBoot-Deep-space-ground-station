package com.deepspace.groundstation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TelemetryResponse(
        Long id,
        Long satellitePassId,
        String satelliteName,
        Long dishId,
        String dishCode,
        BigDecimal dataReceivedGB,
        BigDecimal trackingDurationHours,
        BigDecimal elevation,
        BigDecimal azimuth,
        BigDecimal signalStrength,
        BigDecimal motorTemperature,
        LocalDateTime recordedAt) {
}
