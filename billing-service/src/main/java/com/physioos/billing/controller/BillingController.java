package com.physioos.billing.controller;

import com.physioos.billing.dto.InvoiceCreateRequest;
import com.physioos.billing.dto.InvoiceResponse;
import com.physioos.billing.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceCreateRequest request) {
        return new ResponseEntity<>(billingService.createInvoice(request), HttpStatus.CREATED);
    }

    @GetMapping("/patients/{patientId}/invoices")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(billingService.getInvoicesByPatient(patientId));
    }

    @PostMapping("/invoices/{invoiceId}/issue")
    public ResponseEntity<InvoiceResponse> issueInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(billingService.issueInvoice(invoiceId));
    }

    @PostMapping("/invoices/{invoiceId}/payments")
    public ResponseEntity<InvoiceResponse> recordPayment(
            @PathVariable UUID invoiceId, 
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(billingService.recordPayment(invoiceId, amount));
    }
}
