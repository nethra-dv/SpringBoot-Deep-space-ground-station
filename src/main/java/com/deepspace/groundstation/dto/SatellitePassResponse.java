package com.deepspace.groundstation.dto;

import com.deepspace.groundstation.entity.SatellitePass;
import java.time.LocalDateTime;

public record SatellitePassResponse(
        Long id,
        String satelliteName,
        Long customerId,
        String customerName,
        Long dishId,
        String dishCode,
        LocalDateTime passStart,
        LocalDateTime passEnd,
        String frequency,
        Integer expectedDurationMinutes,
        SatellitePass.Status status) {
}
