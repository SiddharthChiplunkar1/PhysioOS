package com.physioos.healthmetrics.controller;

import com.physioos.healthmetrics.dto.MetricCreateRequest;
import com.physioos.healthmetrics.entity.HealthMetric;
import com.physioos.healthmetrics.service.HealthMetricService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/health-metrics")
public class HealthMetricController {

    private final HealthMetricService service;

    public HealthMetricController(HealthMetricService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<HealthMetric> createMetric(@Valid @RequestBody MetricCreateRequest request) {
        HealthMetric created = service.createMetric(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<HealthMetric>> getMetricsByPatientId(@PathVariable Long patientId) {
        List<HealthMetric> metrics = service.getMetricsByPatientId(patientId);
        return ResponseEntity.ok(metrics);
    }
}
