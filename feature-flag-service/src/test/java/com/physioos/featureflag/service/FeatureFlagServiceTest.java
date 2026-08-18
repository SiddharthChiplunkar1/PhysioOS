package com.physioos.featureflag.service;

import com.physioos.featureflag.dto.FeatureFlagRequest;
import com.physioos.featureflag.dto.FeatureFlagResponse;
import com.physioos.featureflag.entity.FeatureFlag;
import com.physioos.featureflag.repository.FeatureFlagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureFlagServiceTest {

    @Mock
    private FeatureFlagRepository repository;

    @InjectMocks
    private FeatureFlagService service;

    private FeatureFlag featureFlag;
    private FeatureFlagRequest request;

    @BeforeEach
    void setUp() {
        featureFlag = new FeatureFlag();
        featureFlag.setId(1L);
        featureFlag.setKey("test.flag");
        featureFlag.setDescription("Test Flag");
        featureFlag.setEnabled(true);

        request = new FeatureFlagRequest();
        request.setKey("test.flag");
        request.setDescription("Test Flag");
        request.setEnabled(true);
    }

    @Test
    void create_WhenKeyDoesNotExist_ShouldSaveAndReturnResponse() {
        when(repository.findByKey("test.flag")).thenReturn(Optional.empty());
        when(repository.save(any(FeatureFlag.class))).thenReturn(featureFlag);

        FeatureFlagResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("test.flag", response.getKey());
        assertTrue(response.isEnabled());
        verify(repository, times(1)).save(any(FeatureFlag.class));
    }

    @Test
    void create_WhenKeyExists_ShouldThrowException() {
        when(repository.findByKey("test.flag")).thenReturn(Optional.of(featureFlag));

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
        verify(repository, never()).save(any(FeatureFlag.class));
    }

    @Test
    void getByKey_WhenFound_ShouldReturnResponse() {
        when(repository.findByKey("test.flag")).thenReturn(Optional.of(featureFlag));

        FeatureFlagResponse response = service.getByKey("test.flag");

        assertNotNull(response);
        assertEquals("test.flag", response.getKey());
    }

    @Test
    void getByKey_WhenNotFound_ShouldThrowException() {
        when(repository.findByKey("unknown.flag")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getByKey("unknown.flag"));
    }

    @Test
    void getAll_ShouldReturnListOfResponses() {
        when(repository.findAll()).thenReturn(java.util.List.of(featureFlag));
        
        var responses = service.getAll();
        
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("test.flag", responses.get(0).getKey());
    }

    @Test
    void update_WhenFound_ShouldUpdateAndReturnResponse() {
        when(repository.findByKey("test.flag")).thenReturn(Optional.of(featureFlag));
        when(repository.save(any(FeatureFlag.class))).thenReturn(featureFlag);

        FeatureFlagRequest updateRequest = new FeatureFlagRequest();
        updateRequest.setDescription("Updated Description");
        updateRequest.setEnabled(false);

        FeatureFlagResponse response = service.update("test.flag", updateRequest);

        assertNotNull(response);
        verify(repository, times(1)).save(any(FeatureFlag.class));
    }

    @Test
    void update_WhenNotFound_ShouldThrowException() {
        when(repository.findByKey("unknown.flag")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update("unknown.flag", request));
    }

    @Test
    void delete_WhenFound_ShouldDelete() {
        when(repository.findByKey("test.flag")).thenReturn(Optional.of(featureFlag));

        service.delete("test.flag");

        verify(repository, times(1)).delete(featureFlag);
    }

    @Test
    void delete_WhenNotFound_ShouldThrowException() {
        when(repository.findByKey("unknown.flag")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.delete("unknown.flag"));
    }
}
