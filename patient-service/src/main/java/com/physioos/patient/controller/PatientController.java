package com.physioos.patient.controller;

import com.physioos.patient.dto.PatientCreateRequest;
import com.physioos.patient.dto.PatientResponse;
import com.physioos.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientCreateRequest request) {
        PatientResponse response = patientService.createPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable UUID id) {
        PatientResponse response = patientService.getPatientById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getPatientsByOrganizationId(@RequestParam UUID organizationId) {
        List<PatientResponse> response = patientService.getPatientsByOrganizationId(organizationId);
        return ResponseEntity.ok(response);
    }
}
