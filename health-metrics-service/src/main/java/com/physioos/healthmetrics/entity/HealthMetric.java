package com.physioos.healthmetrics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_metrics")
public class HealthMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private String metricType;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    public HealthMetric() {}

    public HealthMetric(Long patientId, String metricType, Double value, String unit, LocalDateTime recordedAt) {
        this.patientId = patientId;
        this.metricType = metricType;
        this.value = value;
        this.unit = unit;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
