package com.physioos.employee.service;

import com.physioos.employee.dto.EmployeeCreateRequest;
import com.physioos.employee.dto.EmployeeResponse;
import com.physioos.employee.dto.EmployeeUpdateRequest;
import com.physioos.employee.entity.Employee;
import com.physioos.employee.entity.EmployeeStatus;
import com.physioos.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponse onboardEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Employee with this email already exists");
        }

        Employee employee = Employee.builder()
                .organizationId(request.getOrganizationId())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .specialization(request.getSpecialization())
                .clinicId(request.getClinicId())
                .workingHours(request.getWorkingHours())
                .status(EmployeeStatus.INVITED) // PRD default
                .build();

        return mapToResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse getEmployeeById(UUID id) {
        return mapToResponse(employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found")));
    }

    public List<EmployeeResponse> getEmployeesByOrganization(UUID organizationId) {
        return employeeRepository.findByOrganizationId(organizationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmployeeResponse updateEmployee(UUID id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (request.getName() != null) employee.setName(request.getName());
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getRole() != null) employee.setRole(request.getRole());
        if (request.getSpecialization() != null) employee.setSpecialization(request.getSpecialization());
        if (request.getClinicId() != null) employee.setClinicId(request.getClinicId());
        if (request.getWorkingHours() != null) employee.setWorkingHours(request.getWorkingHours());
        
        if (request.getStatus() != null) {
            // Implement state machine logic here if needed (e.g. INVITED -> ACTIVE)
            employee.setStatus(request.getStatus());
        }

        return mapToResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void deactivateEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setOrganizationId(employee.getOrganizationId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setPhone(employee.getPhone());
        response.setRole(employee.getRole());
        response.setSpecialization(employee.getSpecialization());
        response.setClinicId(employee.getClinicId());
        response.setWorkingHours(employee.getWorkingHours());
        response.setStatus(employee.getStatus());
        response.setJoinedAt(employee.getJoinedAt());
        return response;
    }
}
