package com.deepspace.groundstation.dto;

import com.deepspace.groundstation.entity.Product;
import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String productCode,
        String productName,
        String description,
        Product.Type type,
        String unit,
        BigDecimal unitPrice,
        boolean active) {
}
