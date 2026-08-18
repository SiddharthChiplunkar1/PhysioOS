package com.physioos.billing.dto;

import com.physioos.billing.entity.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceResponse {
    private UUID id;
    private UUID organizationId;
    private UUID clinicId;
    private UUID patientId;
    private InvoiceStatus status;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal outstandingBalance;
    private LocalDateTime createdAt;
    // Leaving out line items for top-level list responses to keep it light
}
