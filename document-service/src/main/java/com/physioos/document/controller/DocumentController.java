package com.physioos.document.controller;

import com.physioos.document.dto.DocumentMetadataRequest;
import com.physioos.document.entity.Document;
import com.physioos.document.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<Document> uploadDocumentMetadata(@Valid @RequestBody DocumentMetadataRequest request) {
        Document savedDocument = documentService.saveDocumentMetadata(request);
        return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Document>> getPatientDocuments(@PathVariable Long patientId) {
        return ResponseEntity.ok(documentService.getDocumentsByPatientId(patientId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
