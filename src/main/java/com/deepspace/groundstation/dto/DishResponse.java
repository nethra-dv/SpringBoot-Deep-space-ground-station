package com.deepspace.groundstation.dto;

import com.deepspace.groundstation.entity.Dish;
import java.math.BigDecimal;

public record DishResponse(
        Long id,
        String dishCode,
        String dishName,
        String location,
        BigDecimal diameterMeters,
        String frequencyBand,
        Dish.Status status,
        BigDecimal maximumElevation,
        BigDecimal minimumElevation,
        Long servoManufacturerId,
        String servoManufacturerName,
        boolean active) {
}
