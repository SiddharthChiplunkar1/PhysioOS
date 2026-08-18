package com.physioos.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.physioos.common.entity.Role;
import com.physioos.employee.dto.EmployeeCreateRequest;
import com.physioos.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void testOnboard_Validation_InvalidEmail() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setOrganizationId(UUID.randomUUID());
        request.setName("Test");
        request.setRole(Role.DOCTOR);
        request.setEmail("invalid-email"); // Should fail

        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testOnboard_Validation_MissingRole() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setOrganizationId(UUID.randomUUID());
        request.setName("Test");
        request.setEmail("valid@example.com");
        // Missing role

        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
