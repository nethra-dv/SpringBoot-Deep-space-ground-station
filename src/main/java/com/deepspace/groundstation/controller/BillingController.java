package com.deepspace.groundstation.controller;

import com.deepspace.groundstation.dto.BillingCalculationResponse;
import com.deepspace.groundstation.dto.InvoiceResponse;
import com.deepspace.groundstation.service.BillingService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing/pass/{passId}")
public class BillingController {
    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/calculate")
    public BillingCalculationResponse calculate(@PathVariable Long passId) {
        return billingService.calculate(passId);
    }

    @PostMapping("/invoice")
    public ResponseEntity<InvoiceResponse> generateInvoice(@PathVariable Long passId) {
        InvoiceResponse response = billingService.generateInvoice(passId);
        URI location = URI.create("/api/billing/pass/" + passId + "/invoice");
        return ResponseEntity.created(location).body(response);
    }
}
