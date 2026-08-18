package com.physioos.healthmetrics.repository;

import com.physioos.healthmetrics.entity.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {
    List<HealthMetric> findByPatientId(Long patientId);
}
