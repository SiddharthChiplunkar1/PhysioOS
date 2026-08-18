package com.physioos.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.physioos.inventory.dto.StockAdjustmentRequest;
import com.physioos.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void testAdjustStock_Validation_MissingReason() throws Exception {
        StockAdjustmentRequest request = new StockAdjustmentRequest();
        request.setQuantityChange(-5);
        request.setMovementType("USAGE");
        // Missing reason

        mockMvc.perform(post("/api/v1/inventory/items/" + UUID.randomUUID() + "/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAdjustStock_Validation_NullQuantity() throws Exception {
        StockAdjustmentRequest request = new StockAdjustmentRequest();
        request.setMovementType("USAGE");
        request.setReason("Test");
        // Missing quantityChange

        mockMvc.perform(post("/api/v1/inventory/items/" + UUID.randomUUID() + "/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateItem_Validation_NegativeUnitPrice() throws Exception {
        com.physioos.inventory.dto.ItemCreateRequest request = new com.physioos.inventory.dto.ItemCreateRequest();
        request.setOrganizationId(UUID.randomUUID());
        request.setClinicId(UUID.randomUUID());
        request.setName("Test Item");
        request.setCategory(com.physioos.inventory.entity.ItemCategory.CONSUMABLE);
        request.setUnitPrice(new java.math.BigDecimal("-10.00"));

        mockMvc.perform(post("/api/v1/inventory/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
