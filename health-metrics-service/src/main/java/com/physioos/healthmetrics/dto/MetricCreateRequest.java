package com.physioos.healthmetrics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public class MetricCreateRequest {

    @NotNull(message = "Patient ID must not be null")
    @Positive(message = "Patient ID must be a positive number")
    private Long patientId;

    @NotBlank(message = "Metric type must not be blank")
    private String metricType;

    @NotNull(message = "Value must not be null")
    private Double value;

    @NotBlank(message = "Unit must not be blank")
    private String unit;

    @NotNull(message = "Recorded at must not be null")
    @PastOrPresent(message = "Recorded at cannot be in the future")
    private LocalDateTime recordedAt;

    public MetricCreateRequest() {}

    public MetricCreateRequest(Long patientId, String metricType, Double value, String unit, LocalDateTime recordedAt) {
        this.patientId = patientId;
        this.metricType = metricType;
        this.value = value;
        this.unit = unit;
        this.recordedAt = recordedAt;
    }

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
