package com.physioos.document.service;

import com.physioos.document.dto.DocumentMetadataRequest;
import com.physioos.document.entity.Document;
import com.physioos.document.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void saveDocumentMetadata_ReturnsSavedDocument() {
        DocumentMetadataRequest request = new DocumentMetadataRequest();
        request.setPatientId(1L);
        request.setFileName("test.pdf");
        request.setFileType("application/pdf");
        request.setS3Url("s3://bucket/test.pdf");

        Document mockDocument = new Document(1L, "test.pdf", "application/pdf", "s3://bucket/test.pdf", LocalDateTime.now());
        mockDocument.setId(10L);

        when(documentRepository.save(any(Document.class))).thenReturn(mockDocument);

        Document savedDoc = documentService.saveDocumentMetadata(request);

        assertNotNull(savedDoc);
        assertEquals(10L, savedDoc.getId());
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void getDocumentById_ReturnsDocument_WhenExists() {
        Document mockDocument = new Document(1L, "test.pdf", "application/pdf", "s3://bucket/test.pdf", LocalDateTime.now());
        mockDocument.setId(10L);

        when(documentRepository.findById(10L)).thenReturn(Optional.of(mockDocument));

        Optional<Document> result = documentService.getDocumentById(10L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
    }

    @Test
    void getDocumentsByPatientId_ReturnsDocumentList() {
        Document mockDocument = new Document(1L, "test.pdf", "application/pdf", "s3://bucket/test.pdf", LocalDateTime.now());
        when(documentRepository.findByPatientId(1L)).thenReturn(List.of(mockDocument));

        List<Document> result = documentService.getDocumentsByPatientId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void deleteDocument_DeletesSuccessfully() {
        doNothing().when(documentRepository).deleteById(10L);

        documentService.deleteDocument(10L);

        verify(documentRepository, times(1)).deleteById(10L);
    }
}
