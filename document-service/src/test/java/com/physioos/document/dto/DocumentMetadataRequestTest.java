package com.physioos.document.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentMetadataRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDocumentMetadataRequest() {
        DocumentMetadataRequest request = new DocumentMetadataRequest();
        request.setPatientId(1L);
        request.setFileName("report.pdf");
        request.setFileType("application/pdf");
        request.setS3Url("https://s3.aws.com/report.pdf");

        Set<ConstraintViolation<DocumentMetadataRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidFileName() {
        DocumentMetadataRequest request = new DocumentMetadataRequest();
        request.setPatientId(1L);
        request.setFileName("report"); // No extension
        request.setFileType("application/pdf");
        request.setS3Url("https://s3.aws.com/report.pdf");

        Set<ConstraintViolation<DocumentMetadataRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Invalid file name format", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidFileType() {
        DocumentMetadataRequest request = new DocumentMetadataRequest();
        request.setPatientId(1L);
        request.setFileName("report.exe");
        request.setFileType("application/x-msdownload"); // Invalid mime type
        request.setS3Url("https://s3.aws.com/report.exe");

        Set<ConstraintViolation<DocumentMetadataRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size()); // Fails file type validation
        
        boolean fileTypeViolationFound = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Invalid file type. Only PDF, JPEG, and PNG are allowed"));
        assertTrue(fileTypeViolationFound);
    }
}
