package com.deepspace.groundstation.dto;

import com.deepspace.groundstation.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String productCode,
        @NotBlank String productName,
        String description,
        @NotNull Product.Type type,
        @NotBlank String unit,
        @NotNull @PositiveOrZero BigDecimal unitPrice,
        Boolean active) {
}
