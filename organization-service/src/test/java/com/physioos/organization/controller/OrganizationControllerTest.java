package com.physioos.organization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.physioos.organization.dto.OrganizationCreateRequest;
import com.physioos.organization.service.OrganizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OrganizationController.class)
public class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationService organizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createOrganization_ValidRequest_ReturnsCreated() throws Exception {
        OrganizationCreateRequest request = new OrganizationCreateRequest();
        request.setName("Test Org");
        request.setContactEmail("test@example.com");
        request.setContactPhone("+1234567890");
        request.setSubscriptionTier("Premium");

        mockMvc.perform(post("/api/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void createOrganization_InvalidEmail_ReturnsBadRequest() throws Exception {
        OrganizationCreateRequest request = new OrganizationCreateRequest();
        request.setName("Test Org");
        request.setContactEmail("invalid-email");
        request.setContactPhone("+1234567890");
        request.setSubscriptionTier("Premium");

        mockMvc.perform(post("/api/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createOrganization_BlankName_ReturnsBadRequest() throws Exception {
        OrganizationCreateRequest request = new OrganizationCreateRequest();
        request.setName("");
        request.setContactEmail("test@example.com");
        request.setContactPhone("+1234567890");
        request.setSubscriptionTier("Premium");

        mockMvc.perform(post("/api/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void createOrganization_InvalidPhone_ReturnsBadRequest() throws Exception {
        OrganizationCreateRequest request = new OrganizationCreateRequest();
        request.setName("Test Org");
        request.setContactEmail("test@example.com");
        request.setContactPhone("123"); // Too short
        request.setSubscriptionTier("Premium");

        mockMvc.perform(post("/api/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
