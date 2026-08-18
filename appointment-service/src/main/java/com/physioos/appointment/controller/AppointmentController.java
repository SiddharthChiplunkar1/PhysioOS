package com.physioos.appointment.controller;

import com.physioos.appointment.domain.Appointment;
import com.physioos.appointment.dto.AppointmentCreateRequest;
import com.physioos.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentCreateRequest request) {
        try {
            Appointment appointment = appointmentService.createAppointment(request);
            return new ResponseEntity<>(appointment, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}
