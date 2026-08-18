package com.physioos.billing.service;

import com.physioos.billing.dto.InvoiceCreateRequest;
import com.physioos.billing.dto.InvoiceLineItemRequest;
import com.physioos.billing.dto.InvoiceResponse;
import com.physioos.billing.entity.Invoice;
import com.physioos.billing.entity.InvoiceLineItem;
import com.physioos.billing.entity.InvoiceStatus;
import com.physioos.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceCreateRequest request) {
        
        BigDecimal subTotal = request.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // SubTotal + Tax - Discount
        BigDecimal calculatedTotal = subTotal.add(request.getTaxAmount()).subtract(request.getDiscountAmount());

        if (calculatedTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total invoice amount cannot be negative after discounts");
        }

        Invoice invoice = Invoice.builder()
                .organizationId(request.getOrganizationId())
                .clinicId(request.getClinicId())
                .patientId(request.getPatientId())
                .status(InvoiceStatus.DRAFT)
                .taxAmount(request.getTaxAmount())
                .discountAmount(request.getDiscountAmount())
                .totalAmount(calculatedTotal)
                .outstandingBalance(calculatedTotal) // Initially, outstanding is the total
                .build();

        for (InvoiceLineItemRequest itemReq : request.getItems()) {
            InvoiceLineItem item = InvoiceLineItem.builder()
                    .description(itemReq.getDescription())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .totalPrice(itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())))
                    .build();
            invoice.addLineItem(item);
        }

        return mapToResponse(invoiceRepository.save(invoice));
    }

    public List<InvoiceResponse> getInvoicesByPatient(UUID patientId) {
        return invoiceRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InvoiceResponse issueInvoice(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT invoices can be issued");
        }

        invoice.setStatus(InvoiceStatus.ISSUED);
        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Transactional
    public InvoiceResponse recordPayment(UUID invoiceId, BigDecimal paymentAmount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED || invoice.getStatus() == InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Cannot apply payment to a DRAFT, PAID or CANCELLED invoice");
        }

        BigDecimal newBalance = invoice.getOutstandingBalance().subtract(paymentAmount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Payment exceeds outstanding balance");
        }

        invoice.setOutstandingBalance(newBalance);

        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        return mapToResponse(invoiceRepository.save(invoice));
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setOrganizationId(invoice.getOrganizationId());
        response.setClinicId(invoice.getClinicId());
        response.setPatientId(invoice.getPatientId());
        response.setStatus(invoice.getStatus());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setTaxAmount(invoice.getTaxAmount());
        response.setDiscountAmount(invoice.getDiscountAmount());
        response.setOutstandingBalance(invoice.getOutstandingBalance());
        response.setCreatedAt(invoice.getCreatedAt());
        return response;
    }
}
