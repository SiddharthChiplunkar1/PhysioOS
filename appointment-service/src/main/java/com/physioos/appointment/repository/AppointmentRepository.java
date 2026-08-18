package com.physioos.appointment.repository;

import com.physioos.appointment.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("SELECT a FROM Appointment a WHERE a.therapistId = :therapistId " +
           "AND a.status != com.physioos.appointment.domain.AppointmentStatus.CANCELLED " +
           "AND ((a.startTime < :endTime AND a.endTime > :startTime))")
    List<Appointment> findOverlappingAppointmentsForTherapist(
            @Param("therapistId") UUID therapistId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
