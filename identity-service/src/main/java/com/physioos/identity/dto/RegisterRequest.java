package com.physioos.identity.dto;

import com.physioos.common.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", 
             message = "Password must be at least 8 characters long and contain at least one letter, one number, and one special character")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
}
