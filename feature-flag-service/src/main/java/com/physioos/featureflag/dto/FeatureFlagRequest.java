package com.physioos.featureflag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FeatureFlagRequest {
    @NotBlank(message = "Key cannot be blank")
    private String key;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Enabled status must be provided")
    private Boolean enabled;

    // Getters and Setters
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
