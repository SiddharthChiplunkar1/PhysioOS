package com.physioos.patient.service;

import com.physioos.patient.dto.PatientCreateRequest;
import com.physioos.patient.dto.PatientResponse;
import com.physioos.patient.entity.Patient;
import com.physioos.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private PatientCreateRequest request;
    private UUID patientId;
    private UUID orgId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        orgId = UUID.randomUUID();

        patient = new Patient();
        patient.setId(patientId);
        patient.setOrganizationId(orgId);
        patient.setName("John Doe");
        patient.setEmail("john@example.com");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setCreatedAt(LocalDateTime.now());

        request = new PatientCreateRequest();
        request.setOrganizationId(orgId);
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createPatient_Success() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientResponse response = patientService.createPatient(request);

        assertNotNull(response);
        assertEquals(patientId, response.getId());
        assertEquals("John Doe", response.getName());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void getPatientById_Success() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        PatientResponse response = patientService.getPatientById(patientId);

        assertNotNull(response);
        assertEquals(patientId, response.getId());
    }

    @Test
    void getPatientById_NotFound() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> patientService.getPatientById(patientId));
    }

    @Test
    void getPatientsByOrganizationId_Success() {
        when(patientRepository.findByOrganizationId(orgId)).thenReturn(List.of(patient));

        List<PatientResponse> responses = patientService.getPatientsByOrganizationId(orgId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(orgId, responses.get(0).getOrganizationId());
    }
}
