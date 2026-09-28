package com.deepspace.groundstation.dto;

import com.deepspace.groundstation.entity.SatellitePass;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record SatellitePassRequest(
        @NotBlank String satelliteName,
        @NotNull Long customerId,
        @NotNull Long dishId,
        @NotNull LocalDateTime passStart,
        @NotNull LocalDateTime passEnd,
        String frequency,
        @PositiveOrZero Integer expectedDurationMinutes,
        SatellitePass.Status status) {

    @AssertTrue(message = "passEnd must be after passStart")
    public boolean isPassEndAfterPassStart() {
        return passStart == null || passEnd == null || passEnd.isAfter(passStart);
    }
}
