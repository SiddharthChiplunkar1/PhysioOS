package com.physioos.organization.dto;

import java.time.LocalDateTime;

public class OrganizationResponse {

    private Long id;
    private String name;
    private String contactEmail;
    private String contactPhone;
    private String subscriptionTier;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getSubscriptionTier() { return subscriptionTier; }
    public void setSubscriptionTier(String subscriptionTier) { this.subscriptionTier = subscriptionTier; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
