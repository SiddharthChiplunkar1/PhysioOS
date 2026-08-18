package com.physioos.patient.service;

import com.physioos.patient.dto.PatientCreateRequest;
import com.physioos.patient.dto.PatientResponse;
import com.physioos.patient.entity.Patient;
import com.physioos.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResponse createPatient(PatientCreateRequest request) {
        Patient patient = new Patient();
        patient.setOrganizationId(request.getOrganizationId());
        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setPhone(request.getPhone());
        patient.setGender(request.getGender());
        patient.setAddress(request.getAddress());

        Patient savedPatient = patientRepository.save(patient);
        return mapToResponse(savedPatient);
    }

    public PatientResponse getPatientById(UUID id) {
        return patientRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Patient not found")); // Could use a custom exception
    }
    
    public List<PatientResponse> getPatientsByOrganizationId(UUID organizationId) {
        return patientRepository.findByOrganizationId(organizationId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PatientResponse mapToResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setOrganizationId(patient.getOrganizationId());
        response.setName(patient.getName());
        response.setEmail(patient.getEmail());
        response.setDateOfBirth(patient.getDateOfBirth());
        response.setPhone(patient.getPhone());
        response.setGender(patient.getGender());
        response.setAddress(patient.getAddress());
        response.setCreatedAt(patient.getCreatedAt());
        return response;
    }
}
