package com.physioos.healthmetrics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.physioos.healthmetrics.dto.MetricCreateRequest;
import com.physioos.healthmetrics.entity.HealthMetric;
import com.physioos.healthmetrics.service.HealthMetricService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthMetricController.class)
public class HealthMetricControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthMetricService service;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateMetric_Success() throws Exception {
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        MetricCreateRequest request = new MetricCreateRequest(1L, "HeartRate", 72.0, "bpm", now);
        HealthMetric metric = new HealthMetric(1L, "HeartRate", 72.0, "bpm", now);
        metric.setId(10L);

        when(service.createMetric(any(MetricCreateRequest.class))).thenReturn(metric);

        mockMvc.perform(post("/api/v1/health-metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.patientId").value(1));
    }

    @Test
    void testCreateMetric_ValidationFails_NullPatientId() throws Exception {
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        MetricCreateRequest request = new MetricCreateRequest(null, "HeartRate", 72.0, "bpm", now);

        mockMvc.perform(post("/api/v1/health-metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMetric_ValidationFails_NegativePatientId() throws Exception {
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        MetricCreateRequest request = new MetricCreateRequest(-1L, "HeartRate", 72.0, "bpm", now);

        mockMvc.perform(post("/api/v1/health-metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMetric_ValidationFails_BlankMetricType() throws Exception {
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        MetricCreateRequest request = new MetricCreateRequest(1L, "", 72.0, "bpm", now);

        mockMvc.perform(post("/api/v1/health-metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMetric_ValidationFails_NullValue() throws Exception {
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        MetricCreateRequest request = new MetricCreateRequest(1L, "HeartRate", null, "bpm", now);

        mockMvc.perform(post("/api/v1/health-metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMetric_ValidationFails_FutureDate() throws Exception {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        MetricCreateRequest request = new MetricCreateRequest(1L, "HeartRate", 72.0, "bpm", future);

        mockMvc.perform(post("/api/v1/health-metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMetricsByPatientId() throws Exception {
        when(service.getMetricsByPatientId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/health-metrics/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
