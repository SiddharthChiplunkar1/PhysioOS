package com.physioos.billing.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Data
@Builder
public class Invoice {

    public Invoice() {
    }

    public Invoice(UUID id, UUID organizationId, UUID clinicId, UUID patientId, InvoiceStatus status, BigDecimal totalAmount, BigDecimal taxAmount, BigDecimal discountAmount, BigDecimal outstandingBalance, List<InvoiceLineItem> lineItems, LocalDateTime createdAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.clinicId = clinicId;
        this.patientId = patientId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.outstandingBalance = outstandingBalance;
        this.lineItems = lineItems == null ? new ArrayList<>() : lineItems;
        this.createdAt = createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "clinic_id", nullable = false)
    private UUID clinicId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "outstanding_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal outstandingBalance;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = InvoiceStatus.DRAFT;
        }
    }

    public void addLineItem(InvoiceLineItem item) {
        lineItems.add(item);
        item.setInvoice(this);
    }
}
