package com.deepspace.groundstation.dto;

import com.deepspace.groundstation.entity.Invoice;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceResponse(
        Long id,
        String invoiceNumber,
        Long customerId,
        String customerName,
        Long satellitePassId,
        LocalDate invoiceDate,
        LocalDate dueDate,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal totalAmount,
        Invoice.Status status,
        List<InvoiceItemResponse> items) {
}
