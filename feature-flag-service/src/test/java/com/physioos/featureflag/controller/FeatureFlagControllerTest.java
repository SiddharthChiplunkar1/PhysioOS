package com.physioos.featureflag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.physioos.featureflag.dto.FeatureFlagRequest;
import com.physioos.featureflag.service.FeatureFlagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeatureFlagController.class)
class FeatureFlagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeatureFlagService service;

    @Test
    void createFeatureFlag_WithValidRequest_ReturnsCreated() throws Exception {
        FeatureFlagRequest request = new FeatureFlagRequest();
        request.setKey("valid.key");
        request.setDescription("Valid description");
        request.setEnabled(true);

        mockMvc.perform(post("/api/v1/feature-flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createFeatureFlag_WithBlankKey_ReturnsBadRequest() throws Exception {
        FeatureFlagRequest request = new FeatureFlagRequest();
        request.setKey(""); // Invalid
        request.setDescription("Valid description");
        request.setEnabled(true);

        mockMvc.perform(post("/api/v1/feature-flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFeatureFlag_WithNullDescription_ReturnsBadRequest() throws Exception {
        FeatureFlagRequest request = new FeatureFlagRequest();
        request.setKey("valid.key");
        request.setDescription(null); // Invalid
        request.setEnabled(true);

        mockMvc.perform(post("/api/v1/feature-flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void createFeatureFlag_WithNullEnabled_ReturnsBadRequest() throws Exception {
        FeatureFlagRequest request = new FeatureFlagRequest();
        request.setKey("valid.key");
        request.setDescription("Valid description");
        request.setEnabled(null); // Invalid

        mockMvc.perform(post("/api/v1/feature-flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_ReturnsOk() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/feature-flags"))
                .andExpect(status().isOk());
    }

    @Test
    void getByKey_ReturnsOk() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/feature-flags/some.key"))
                .andExpect(status().isOk());
    }

    @Test
    void updateFeatureFlag_WithValidRequest_ReturnsOk() throws Exception {
        FeatureFlagRequest request = new FeatureFlagRequest();
        request.setKey("valid.key");
        request.setDescription("Updated description");
        request.setEnabled(false);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/feature-flags/valid.key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFeatureFlag_ReturnsNoContent() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/feature-flags/valid.key"))
                .andExpect(status().isNoContent());
    }
}
