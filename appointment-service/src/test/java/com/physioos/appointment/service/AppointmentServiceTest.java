package com.physioos.appointment.service;

import com.physioos.appointment.domain.Appointment;
import com.physioos.appointment.domain.AppointmentStatus;
import com.physioos.appointment.dto.AppointmentCreateRequest;
import com.physioos.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAppointment_Success() {
        UUID patientId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID clinicId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);

        AppointmentCreateRequest request = new AppointmentCreateRequest(patientId, therapistId, clinicId, startTime, endTime);

        when(appointmentRepository.findOverlappingAppointmentsForTherapist(therapistId, startTime, endTime)).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment created = appointmentService.createAppointment(request);

        assertNotNull(created);
        assertEquals(patientId, created.getPatientId());
        assertEquals(AppointmentStatus.SCHEDULED, created.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testCreateAppointment_DoubleBooking() {
        UUID patientId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID clinicId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(1);

        AppointmentCreateRequest request = new AppointmentCreateRequest(patientId, therapistId, clinicId, startTime, endTime);

        when(appointmentRepository.findOverlappingAppointmentsForTherapist(therapistId, startTime, endTime))
                .thenReturn(List.of(new Appointment()));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            appointmentService.createAppointment(request);
        });

        assertEquals("Therapist is already booked for the given time slot", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testCreateAppointment_InvalidTime() {
        UUID patientId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID clinicId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.minusHours(1);

        AppointmentCreateRequest request = new AppointmentCreateRequest(patientId, therapistId, clinicId, startTime, endTime);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.createAppointment(request);
        });

        assertEquals("Start time must be before end time", exception.getMessage());
    }
}
