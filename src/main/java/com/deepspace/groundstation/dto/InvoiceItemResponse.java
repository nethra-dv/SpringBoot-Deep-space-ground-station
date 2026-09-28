package com.deepspace.groundstation.dto;

import java.math.BigDecimal;

public record InvoiceItemResponse(
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal amount) {
}
