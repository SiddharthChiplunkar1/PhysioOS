package com.physioos.appointment.service;

import com.physioos.appointment.domain.Appointment;
import com.physioos.appointment.domain.AppointmentStatus;
import com.physioos.appointment.dto.AppointmentCreateRequest;
import com.physioos.appointment.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public Appointment createAppointment(AppointmentCreateRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointmentsForTherapist(
                request.getTherapistId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!overlappingAppointments.isEmpty()) {
            throw new IllegalStateException("Therapist is already booked for the given time slot");
        }

        Appointment appointment = new Appointment(
                request.getPatientId(),
                request.getTherapistId(),
                request.getClinicId(),
                request.getStartTime(),
                request.getEndTime(),
                AppointmentStatus.SCHEDULED
        );

        return appointmentRepository.save(appointment);
    }
}
