package com.physioos.billing.service;

import com.physioos.billing.dto.InvoiceCreateRequest;
import com.physioos.billing.dto.InvoiceLineItemRequest;
import com.physioos.billing.dto.InvoiceResponse;
import com.physioos.billing.entity.Invoice;
import com.physioos.billing.entity.InvoiceStatus;
import com.physioos.billing.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private BillingService billingService;

    @Test
    void testCreateInvoice_Success() {
        // Arrange
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setPatientId(UUID.randomUUID());
        request.setTaxAmount(new BigDecimal("10.00"));
        request.setDiscountAmount(new BigDecimal("5.00"));

        InvoiceLineItemRequest item = new InvoiceLineItemRequest();
        item.setDescription("Consultation");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        request.setItems(List.of(item));

        // Subtotal = 100
        // Total = 100 + 10 - 5 = 105

        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> {
            Invoice inv = i.getArgument(0);
            inv.setId(UUID.randomUUID());
            return inv;
        });

        // Act
        InvoiceResponse response = billingService.createInvoice(request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("105.00"), response.getTotalAmount());
        assertEquals(InvoiceStatus.DRAFT, response.getStatus());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testCreateInvoice_NegativeTotal_ThrowsException() {
        // Arrange
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setTaxAmount(new BigDecimal("0.00"));
        request.setDiscountAmount(new BigDecimal("100.00")); // Discount greater than subtotal

        InvoiceLineItemRequest item = new InvoiceLineItemRequest();
        item.setDescription("Ice Pack");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("20.00"));
        request.setItems(List.of(item));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> billingService.createInvoice(request));
        assertTrue(exception.getMessage().contains("cannot be negative"));
    }

    @Test
    void testRecordPayment_Success() {
        // Arrange
        UUID invoiceId = UUID.randomUUID();
        Invoice invoice = Invoice.builder()
                .id(invoiceId)
                .totalAmount(new BigDecimal("100.00"))
                .outstandingBalance(new BigDecimal("100.00"))
                .status(InvoiceStatus.ISSUED)
                .build();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        InvoiceResponse response = billingService.recordPayment(invoiceId, new BigDecimal("60.00"));

        // Assert
        assertEquals(InvoiceStatus.PARTIALLY_PAID, response.getStatus());
        assertEquals(new BigDecimal("40.00"), response.getOutstandingBalance());
    }

    @Test
    void testRecordPayment_FullPayment() {
        // Arrange
        UUID invoiceId = UUID.randomUUID();
        Invoice invoice = Invoice.builder()
                .id(invoiceId)
                .totalAmount(new BigDecimal("100.00"))
                .outstandingBalance(new BigDecimal("100.00"))
                .status(InvoiceStatus.ISSUED)
                .build();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        InvoiceResponse response = billingService.recordPayment(invoiceId, new BigDecimal("100.00"));

        // Assert
        assertEquals(InvoiceStatus.PAID, response.getStatus());
        assertEquals(new BigDecimal("0.00"), response.getOutstandingBalance());
    }
}
