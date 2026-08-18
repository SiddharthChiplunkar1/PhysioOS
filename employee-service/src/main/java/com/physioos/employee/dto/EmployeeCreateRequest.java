package com.physioos.employee.dto;

import com.physioos.common.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class EmployeeCreateRequest {
    @NotNull(message = "Organization ID is required")
    private UUID organizationId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    @NotNull(message = "Role is required")
    private Role role;

    private String specialization;
    private UUID clinicId;
    private String workingHours;
}
