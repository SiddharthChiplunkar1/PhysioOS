package com.physioos.inventory.dto;

import com.physioos.inventory.entity.ItemCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InventoryResponse {
    private UUID id;
    private String name;
    private String sku;
    private ItemCategory category;
    private Integer currentStock;
    private Integer minimumThreshold;
    private BigDecimal unitPrice;
    private String alertStatus; // e.g. "NORMAL", "LOW STOCK", "OUT OF STOCK"
}
