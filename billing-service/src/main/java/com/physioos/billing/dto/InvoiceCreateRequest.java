package com.physioos.billing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceCreateRequest {

    @NotNull(message = "Organization ID is required")
    private UUID organizationId;

    @NotNull(message = "Clinic ID is required")
    private UUID clinicId;

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount cannot be negative")
    private BigDecimal taxAmount;

    @NotNull(message = "Discount amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount cannot be negative")
    private BigDecimal discountAmount;

    @NotEmpty(message = "Invoice must contain at least one line item")
    @Valid
    private List<InvoiceLineItemRequest> items;
}
