package com.physioos.healthmetrics.service;

import com.physioos.healthmetrics.dto.MetricCreateRequest;
import com.physioos.healthmetrics.entity.HealthMetric;
import com.physioos.healthmetrics.repository.HealthMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HealthMetricService {

    private final HealthMetricRepository repository;

    public HealthMetricService(HealthMetricRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public HealthMetric createMetric(MetricCreateRequest request) {
        HealthMetric metric = new HealthMetric(
                request.getPatientId(),
                request.getMetricType(),
                request.getValue(),
                request.getUnit(),
                request.getRecordedAt()
        );
        return repository.save(metric);
    }

    @Transactional(readOnly = true)
    public List<HealthMetric> getMetricsByPatientId(Long patientId) {
        return repository.findByPatientId(patientId);
    }
}
