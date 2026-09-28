package com.deepspace.groundstation.dto;

import java.math.BigDecimal;

public record CustomerResponse(
        Long id,
        String operatorName,
        String contactPerson,
        String email,
        String phone,
        String address,
        BigDecimal billingRatePerGB,
        BigDecimal trackingRatePerHour,
        boolean active) {
}
