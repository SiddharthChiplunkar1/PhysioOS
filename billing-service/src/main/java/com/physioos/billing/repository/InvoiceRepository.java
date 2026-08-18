package com.physioos.billing.repository;

import com.physioos.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByPatientId(UUID patientId);
    List<Invoice> findByClinicId(UUID clinicId);
}
