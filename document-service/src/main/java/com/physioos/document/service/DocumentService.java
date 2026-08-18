package com.physioos.document.service;

import com.physioos.document.dto.DocumentMetadataRequest;
import com.physioos.document.entity.Document;
import com.physioos.document.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document saveDocumentMetadata(DocumentMetadataRequest request) {
        Document document = new Document(
                request.getPatientId(),
                request.getFileName(),
                request.getFileType(),
                request.getS3Url(),
                LocalDateTime.now()
        );
        return documentRepository.save(document);
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public List<Document> getDocumentsByPatientId(Long patientId) {
        return documentRepository.findByPatientId(patientId);
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
