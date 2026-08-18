package com.physioos.employee.repository;

import com.physioos.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByOrganizationId(UUID organizationId);
    List<Employee> findByClinicId(UUID clinicId);
}
