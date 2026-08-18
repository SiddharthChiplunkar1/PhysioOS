package com.physioos.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.physioos.billing.dto.InvoiceCreateRequest;
import com.physioos.billing.dto.InvoiceLineItemRequest;
import com.physioos.billing.service.BillingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillingController.class)
public class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BillingService billingService;

    @Test
    void testCreateInvoice_Validation_NegativeTax() throws Exception {
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setOrganizationId(UUID.randomUUID());
        request.setClinicId(UUID.randomUUID());
        request.setPatientId(UUID.randomUUID());
        request.setTaxAmount(new BigDecimal("-5.00")); // Invalid
        request.setDiscountAmount(BigDecimal.ZERO);

        InvoiceLineItemRequest item = new InvoiceLineItemRequest();
        item.setDescription("Test");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("10.00"));
        request.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/billing/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateInvoice_Validation_EmptyItems() throws Exception {
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setOrganizationId(UUID.randomUUID());
        request.setClinicId(UUID.randomUUID());
        request.setPatientId(UUID.randomUUID());
        request.setTaxAmount(BigDecimal.ZERO);
        request.setDiscountAmount(BigDecimal.ZERO);
        request.setItems(List.of()); // Invalid: must have at least one item

        mockMvc.perform(post("/api/v1/billing/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
