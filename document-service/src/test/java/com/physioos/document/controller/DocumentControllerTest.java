package com.physioos.document.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.physioos.document.dto.DocumentMetadataRequest;
import com.physioos.document.entity.Document;
import com.physioos.document.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenValidInput_thenReturns201() throws Exception {
        DocumentMetadataRequest request = new DocumentMetadataRequest();
        request.setPatientId(1L);
        request.setFileName("scan.png");
        request.setFileType("image/png");
        request.setS3Url("s3://bucket/scan.png");

        Document mockDocument = new Document(1L, "scan.png", "image/png", "s3://bucket/scan.png", LocalDateTime.now());
        mockDocument.setId(100L);

        Mockito.when(documentService.saveDocumentMetadata(any(DocumentMetadataRequest.class))).thenReturn(mockDocument);

        mockMvc.perform(post("/api/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    void whenInvalidMimeType_thenReturns400() throws Exception {
        DocumentMetadataRequest request = new DocumentMetadataRequest();
        request.setPatientId(1L);
        request.setFileName("script.sh");
        request.setFileType("application/x-sh");
        request.setS3Url("s3://bucket/script.sh");

        mockMvc.perform(post("/api/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void whenMissingPatientId_thenReturns400() throws Exception {
        DocumentMetadataRequest request = new DocumentMetadataRequest();
        request.setFileName("report.pdf");
        request.setFileType("application/pdf");
        request.setS3Url("s3://bucket/report.pdf");

        mockMvc.perform(post("/api/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
