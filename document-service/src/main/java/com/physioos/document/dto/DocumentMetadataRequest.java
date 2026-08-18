package com.physioos.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class DocumentMetadataRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotBlank(message = "File name is required")
    @Pattern(regexp = "^[\\w,\\s-]+\\.[A-Za-z]{3,4}$", message = "Invalid file name format")
    private String fileName;

    @NotBlank(message = "File type is required")
    @Pattern(regexp = "^(application/pdf|image/jpeg|image/png)$", message = "Invalid file type. Only PDF, JPEG, and PNG are allowed")
    private String fileType;

    @NotBlank(message = "S3 URL is required")
    private String s3Url;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }
}
