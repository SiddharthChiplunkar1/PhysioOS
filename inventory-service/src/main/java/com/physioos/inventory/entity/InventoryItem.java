package com.physioos.inventory.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_items")
@Data
@Builder
public class InventoryItem {

    public InventoryItem() {
    }

    public InventoryItem(UUID id, UUID organizationId, UUID clinicId, String name, String sku, ItemCategory category, Integer currentStock, Integer minimumThreshold, BigDecimal unitPrice, LocalDateTime createdAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.clinicId = clinicId;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.currentStock = currentStock;
        this.minimumThreshold = minimumThreshold;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "clinic_id", nullable = false)
    private UUID clinicId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Column(name = "minimum_threshold", nullable = false)
    private Integer minimumThreshold;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currentStock == null) this.currentStock = 0;
        if (this.minimumThreshold == null) this.minimumThreshold = 0;
    }
}
