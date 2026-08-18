package com.physioos.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.physioos.appointment.domain.Appointment;
import com.physioos.appointment.domain.AppointmentStatus;
import com.physioos.appointment.dto.AppointmentCreateRequest;
import com.physioos.appointment.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testCreateAppointment_Success() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID clinicId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);

        AppointmentCreateRequest request = new AppointmentCreateRequest(patientId, therapistId, clinicId, startTime, endTime);
        Appointment appointment = new Appointment(patientId, therapistId, clinicId, startTime, endTime, AppointmentStatus.SCHEDULED);
        appointment.setId(UUID.randomUUID());

        when(appointmentService.createAppointment(any(AppointmentCreateRequest.class))).thenReturn(appointment);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void testCreateAppointment_ValidationError() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID clinicId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().minusDays(1); // Past date to trigger @Future validation
        LocalDateTime endTime = startTime.plusHours(1);

        AppointmentCreateRequest request = new AppointmentCreateRequest(patientId, therapistId, clinicId, startTime, endTime);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAppointment_DoubleBookingError() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID clinicId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);

        AppointmentCreateRequest request = new AppointmentCreateRequest(patientId, therapistId, clinicId, startTime, endTime);

        when(appointmentService.createAppointment(any(AppointmentCreateRequest.class)))
                .thenThrow(new IllegalStateException("Therapist is already booked for the given time slot"));

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Therapist is already booked for the given time slot"));
    }
}
