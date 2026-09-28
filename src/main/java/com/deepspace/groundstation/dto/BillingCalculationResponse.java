package com.deepspace.groundstation.dto;

import java.math.BigDecimal;

public record BillingCalculationResponse(
        Long customerId,
        String customerName,
        Long satellitePassId,
        String satelliteName,
        Long dishId,
        String dishCode,
        BigDecimal dataReceivedGB,
        BigDecimal trackingDurationHours,
        BigDecimal billingRatePerGB,
        BigDecimal trackingRatePerHour,
        BigDecimal dataCharge,
        BigDecimal trackingCharge,
        BigDecimal subtotal) {
}
