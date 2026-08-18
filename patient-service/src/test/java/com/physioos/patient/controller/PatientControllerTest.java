package com.physioos.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.physioos.patient.dto.PatientCreateRequest;
import com.physioos.patient.dto.PatientResponse;
import com.physioos.patient.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    private PatientResponse response;
    private PatientCreateRequest request;
    private UUID patientId;
    private UUID orgId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        orgId = UUID.randomUUID();

        response = new PatientResponse();
        response.setId(patientId);
        response.setOrganizationId(orgId);
        response.setName("John Doe");
        response.setEmail("john@example.com");

        request = new PatientCreateRequest();
        request.setOrganizationId(orgId);
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createPatient_Success() throws Exception {
        when(patientService.createPatient(any(PatientCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(patientId.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void createPatient_ValidationFailed() throws Exception {
        request.setEmail("invalid-email"); // Invalid email format

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPatientById_Success() throws Exception {
        when(patientService.getPatientById(patientId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getPatientsByOrganizationId_Success() throws Exception {
        when(patientService.getPatientsByOrganizationId(orgId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/patients")
                .param("organizationId", orgId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(patientId.toString()));
    }
}
