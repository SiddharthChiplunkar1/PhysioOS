package com.physioos.healthmetrics.service;

import com.physioos.healthmetrics.dto.MetricCreateRequest;
import com.physioos.healthmetrics.entity.HealthMetric;
import com.physioos.healthmetrics.repository.HealthMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HealthMetricServiceTest {

    @Mock
    private HealthMetricRepository repository;

    private HealthMetricService service;

    @BeforeEach
    void setUp() {
        service = new HealthMetricService(repository);
    }

    @Test
    void testCreateMetric() {
        LocalDateTime now = LocalDateTime.now();
        MetricCreateRequest request = new MetricCreateRequest(1L, "HeartRate", 72.0, "bpm", now);
        HealthMetric savedMetric = new HealthMetric(1L, "HeartRate", 72.0, "bpm", now);
        savedMetric.setId(100L);

        when(repository.save(any(HealthMetric.class))).thenReturn(savedMetric);

        HealthMetric result = service.createMetric(request);

        ArgumentCaptor<HealthMetric> captor = ArgumentCaptor.forClass(HealthMetric.class);
        verify(repository).save(captor.capture());

        HealthMetric captured = captor.getValue();
        assertEquals(1L, captured.getPatientId());
        assertEquals("HeartRate", captured.getMetricType());
        assertEquals(72.0, captured.getValue());
        assertEquals("bpm", captured.getUnit());
        assertEquals(now, captured.getRecordedAt());

        assertEquals(100L, result.getId());
    }

    @Test
    void testGetMetricsByPatientId() {
        LocalDateTime now = LocalDateTime.now();
        List<HealthMetric> metrics = Arrays.asList(
                new HealthMetric(1L, "HeartRate", 72.0, "bpm", now)
        );

        when(repository.findByPatientId(1L)).thenReturn(metrics);

        List<HealthMetric> result = service.getMetricsByPatientId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPatientId());
        verify(repository, times(1)).findByPatientId(1L);
    }
}
