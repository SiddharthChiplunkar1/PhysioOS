package com.physioos.featureflag.service;

import com.physioos.featureflag.dto.FeatureFlagRequest;
import com.physioos.featureflag.dto.FeatureFlagResponse;
import com.physioos.featureflag.entity.FeatureFlag;
import com.physioos.featureflag.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeatureFlagService {

    private final FeatureFlagRepository repository;

    public FeatureFlagService(FeatureFlagRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<FeatureFlagResponse> getAll() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeatureFlagResponse getByKey(String key) {
        return repository.findByKey(key)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Feature flag not found"));
    }

    @Transactional
    public FeatureFlagResponse create(FeatureFlagRequest request) {
        if (repository.findByKey(request.getKey()).isPresent()) {
            throw new IllegalArgumentException("Feature flag with this key already exists");
        }
        FeatureFlag entity = new FeatureFlag();
        entity.setKey(request.getKey());
        entity.setDescription(request.getDescription());
        entity.setEnabled(request.getEnabled());
        
        return mapToResponse(repository.save(entity));
    }

    @Transactional
    public FeatureFlagResponse update(String key, FeatureFlagRequest request) {
        FeatureFlag entity = repository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("Feature flag not found"));
        
        entity.setDescription(request.getDescription());
        entity.setEnabled(request.getEnabled());
        
        return mapToResponse(repository.save(entity));
    }

    @Transactional
    public void delete(String key) {
        FeatureFlag entity = repository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("Feature flag not found"));
        repository.delete(entity);
    }

    private FeatureFlagResponse mapToResponse(FeatureFlag entity) {
        FeatureFlagResponse response = new FeatureFlagResponse();
        response.setId(entity.getId());
        response.setKey(entity.getKey());
        response.setDescription(entity.getDescription());
        response.setEnabled(entity.isEnabled());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
