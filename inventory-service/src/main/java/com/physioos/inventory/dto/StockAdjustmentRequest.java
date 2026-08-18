package com.physioos.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class StockAdjustmentRequest {

    @NotNull(message = "Quantity change is required")
    private Integer quantityChange;

    @NotBlank(message = "Movement type is required (e.g. USAGE, PURCHASE, SHRINKAGE)")
    private String movementType;

    @NotBlank(message = "Reason is required")
    private String reason;

    private UUID performedBy;
}
