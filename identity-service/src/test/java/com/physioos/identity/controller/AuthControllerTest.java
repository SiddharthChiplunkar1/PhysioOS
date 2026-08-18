package com.physioos.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.physioos.identity.dto.LoginRequest;
import com.physioos.identity.dto.RegisterRequest;
import com.physioos.common.entity.Role;
import com.physioos.identity.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService; // Just mock the service, we are testing the web layer and validation

    @Test
    void testLogin_Validation_InvalidEmail() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("not-an-email"); // Invalid
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Validation_MissingPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        // Missing password

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_Validation_WeakPassword() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setPassword("weak"); // Too short, missing complexity
        request.setRole(Role.PATIENT);
        request.setOrganizationId(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_Validation_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setPassword("StrongPass1!"); // Meets OWASP criteria
        request.setRole(Role.PATIENT);
        request.setOrganizationId(UUID.randomUUID());

        // Note: It returns 200 OK or 401/403 depending on security config mocking.
        // For a pure WebMvcTest without Security config fully loaded, it might fail auth.
        // We will just expect it not to be 400 Bad Request to prove validation passed.
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
