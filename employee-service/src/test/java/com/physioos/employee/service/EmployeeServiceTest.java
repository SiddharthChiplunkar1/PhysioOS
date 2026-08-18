package com.physioos.employee.service;

import com.physioos.common.entity.Role;
import com.physioos.employee.dto.EmployeeCreateRequest;
import com.physioos.employee.dto.EmployeeResponse;
import com.physioos.employee.entity.Employee;
import com.physioos.employee.entity.EmployeeStatus;
import com.physioos.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        mockEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .organizationId(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .role(Role.DOCTOR)
                .status(EmployeeStatus.INVITED)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testOnboardEmployee_Success() {
        // Arrange
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setOrganizationId(mockEmployee.getOrganizationId());
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setRole(Role.DOCTOR);

        when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> {
            Employee e = i.getArgument(0);
            e.setId(UUID.randomUUID());
            e.setJoinedAt(LocalDateTime.now());
            return e;
        });

        // Act
        EmployeeResponse response = employeeService.onboardEmployee(request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(EmployeeStatus.INVITED, response.getStatus()); // Verify PRD default
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testOnboardEmployee_EmailExists() {
        // Arrange
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setEmail("john@example.com");

        when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockEmployee));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> employeeService.onboardEmployee(request));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testDeactivateEmployee_Success() {
        // Arrange
        when(employeeRepository.findById(mockEmployee.getId())).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        employeeService.deactivateEmployee(mockEmployee.getId());

        // Assert
        assertEquals(EmployeeStatus.INACTIVE, mockEmployee.getStatus());
        verify(employeeRepository).save(mockEmployee);
    }
}
