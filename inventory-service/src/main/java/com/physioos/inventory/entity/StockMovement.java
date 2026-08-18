package com.physioos.inventory.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Data
@Builder
public class StockMovement {

    public StockMovement() {
    }

    public StockMovement(UUID id, InventoryItem item, Integer quantity, String movementType, String reason, UUID performedBy, LocalDateTime createdAt) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
        this.movementType = movementType;
        this.reason = reason;
        this.performedBy = performedBy;
        this.createdAt = createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Column(nullable = false)
    private Integer quantity; // Positive for addition, negative for deduction

    @Column(name = "movement_type", nullable = false)
    private String movementType; // e.g. PURCHASE, USAGE, ADJUSTMENT, SHRINKAGE

    @Column(nullable = false)
    private String reason;

    @Column(name = "performed_by")
    private UUID performedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
