package com.physioos.inventory.dto;

import com.physioos.inventory.entity.ItemCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ItemCreateRequest {

    @NotNull(message = "Organization ID is required")
    private UUID organizationId;

    @NotNull(message = "Clinic ID is required")
    private UUID clinicId;

    @NotBlank(message = "Name is required")
    private String name;

    private String sku;

    @NotNull(message = "Category is required")
    private ItemCategory category;

    @Min(value = 0, message = "Initial stock cannot be negative")
    private Integer initialStock = 0;

    @Min(value = 0, message = "Minimum threshold cannot be negative")
    private Integer minimumThreshold = 0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative")
    private BigDecimal unitPrice;
}
