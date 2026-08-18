package com.physioos.employee.dto;

import com.physioos.common.entity.Role;
import com.physioos.employee.entity.EmployeeStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EmployeeResponse {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private String specialization;
    private UUID clinicId;
    private String workingHours;
    private EmployeeStatus status;
    private LocalDateTime joinedAt;
}
