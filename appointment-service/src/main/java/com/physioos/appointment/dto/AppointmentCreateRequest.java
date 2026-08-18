package com.physioos.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentCreateRequest {

    @NotNull(message = "Patient ID cannot be null")
    private UUID patientId;

    @NotNull(message = "Therapist ID cannot be null")
    private UUID therapistId;

    @NotNull(message = "Clinic ID cannot be null")
    private UUID clinicId;

    @NotNull(message = "Start time cannot be null")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    public AppointmentCreateRequest() {
    }

    public AppointmentCreateRequest(UUID patientId, UUID therapistId, UUID clinicId, LocalDateTime startTime, LocalDateTime endTime) {
        this.patientId = patientId;
        this.therapistId = therapistId;
        this.clinicId = clinicId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(UUID therapistId) {
        this.therapistId = therapistId;
    }

    public UUID getClinicId() {
        return clinicId;
    }

    public void setClinicId(UUID clinicId) {
        this.clinicId = clinicId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
