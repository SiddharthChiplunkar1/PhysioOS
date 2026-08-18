package com.physioos.organization.service;

import com.physioos.organization.dto.OrganizationCreateRequest;
import com.physioos.organization.dto.OrganizationResponse;
import com.physioos.organization.entity.Organization;
import com.physioos.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public OrganizationResponse createOrganization(OrganizationCreateRequest request) {
        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setContactEmail(request.getContactEmail());
        organization.setContactPhone(request.getContactPhone());
        organization.setSubscriptionTier(request.getSubscriptionTier());

        Organization savedOrganization = organizationRepository.save(organization);
        return mapToResponse(savedOrganization);
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        return mapToResponse(organization);
    }

    @Transactional
    public OrganizationResponse updateOrganization(Long id, OrganizationCreateRequest request) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        organization.setName(request.getName());
        organization.setContactEmail(request.getContactEmail());
        organization.setContactPhone(request.getContactPhone());
        organization.setSubscriptionTier(request.getSubscriptionTier());

        Organization updatedOrganization = organizationRepository.save(organization);
        return mapToResponse(updatedOrganization);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new RuntimeException("Organization not found");
        }
        organizationRepository.deleteById(id);
    }

    private OrganizationResponse mapToResponse(Organization organization) {
        OrganizationResponse response = new OrganizationResponse();
        response.setId(organization.getId());
        response.setName(organization.getName());
        response.setContactEmail(organization.getContactEmail());
        response.setContactPhone(organization.getContactPhone());
        response.setSubscriptionTier(organization.getSubscriptionTier());
        response.setCreatedAt(organization.getCreatedAt());
        return response;
    }
}
