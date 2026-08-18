package com.physioos.organization.service;

import com.physioos.organization.dto.OrganizationCreateRequest;
import com.physioos.organization.dto.OrganizationResponse;
import com.physioos.organization.entity.Organization;
import com.physioos.organization.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @Test
    public void createOrganization_Success() {
        OrganizationCreateRequest request = new OrganizationCreateRequest();
        request.setName("Test Org");
        request.setContactEmail("test@example.com");
        request.setContactPhone("+1234567890");
        request.setSubscriptionTier("Premium");

        Organization savedEntity = new Organization();
        savedEntity.setId(1L);
        savedEntity.setName("Test Org");
        savedEntity.setContactEmail("test@example.com");
        savedEntity.setContactPhone("+1234567890");
        savedEntity.setSubscriptionTier("Premium");

        when(organizationRepository.save(any(Organization.class))).thenReturn(savedEntity);

        OrganizationResponse response = organizationService.createOrganization(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Org", response.getName());
    }
}
