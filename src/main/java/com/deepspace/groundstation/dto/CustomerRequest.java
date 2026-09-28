package com.deepspace.groundstation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CustomerRequest(
        @NotBlank String operatorName,
        String contactPerson,
        @Email String email,
        String phone,
        String address,
        @NotNull @PositiveOrZero BigDecimal billingRatePerGB,
        @NotNull @PositiveOrZero BigDecimal trackingRatePerHour,
        Boolean active) {
}
