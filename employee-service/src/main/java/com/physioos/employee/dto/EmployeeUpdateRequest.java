package com.physioos.employee.dto;

import com.physioos.common.entity.Role;
import com.physioos.employee.entity.EmployeeStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class EmployeeUpdateRequest {
    private String name;
    private String phone;
    private Role role;
    private String specialization;
    private UUID clinicId;
    private String workingHours;
    private EmployeeStatus status;
}
