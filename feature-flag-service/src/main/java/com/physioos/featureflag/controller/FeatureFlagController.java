package com.physioos.featureflag.controller;

import com.physioos.featureflag.dto.FeatureFlagRequest;
import com.physioos.featureflag.dto.FeatureFlagResponse;
import com.physioos.featureflag.service.FeatureFlagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feature-flags")
public class FeatureFlagController {

    private final FeatureFlagService service;

    public FeatureFlagController(FeatureFlagService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<FeatureFlagResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<FeatureFlagResponse> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(service.getByKey(key));
    }

    @PostMapping
    public ResponseEntity<FeatureFlagResponse> create(@Valid @RequestBody FeatureFlagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{key}")
    public ResponseEntity<FeatureFlagResponse> update(@PathVariable String key, @Valid @RequestBody FeatureFlagRequest request) {
        return ResponseEntity.ok(service.update(key, request));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        service.delete(key);
        return ResponseEntity.noContent().build();
    }
}
