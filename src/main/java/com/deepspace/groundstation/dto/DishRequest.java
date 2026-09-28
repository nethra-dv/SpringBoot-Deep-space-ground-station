package com.deepspace.groundstation.dto;

import com.deepspace.groundstation.entity.Dish;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record DishRequest(
        @NotBlank String dishCode,
        @NotBlank String dishName,
        String location,
        @NotNull @Positive BigDecimal diameterMeters,
        String frequencyBand,
        Dish.Status status,
        @PositiveOrZero BigDecimal maximumElevation,
        @PositiveOrZero BigDecimal minimumElevation,
        Long servoManufacturerId,
        Boolean active) {
}
