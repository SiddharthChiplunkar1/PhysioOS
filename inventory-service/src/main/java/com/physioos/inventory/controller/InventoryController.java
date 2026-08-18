package com.physioos.inventory.controller;

import com.physioos.inventory.dto.InventoryResponse;
import com.physioos.inventory.dto.ItemCreateRequest;
import com.physioos.inventory.dto.StockAdjustmentRequest;
import com.physioos.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/items")
    public ResponseEntity<InventoryResponse> createItem(@Valid @RequestBody ItemCreateRequest request) {
        return new ResponseEntity<>(inventoryService.createItem(request), HttpStatus.CREATED);
    }

    @GetMapping("/clinics/{clinicId}/items")
    public ResponseEntity<List<InventoryResponse>> getInventoryByClinic(@PathVariable UUID clinicId) {
        return ResponseEntity.ok(inventoryService.getInventoryByClinic(clinicId));
    }

    @PostMapping("/items/{itemId}/adjust")
    public ResponseEntity<InventoryResponse> adjustStock(
            @PathVariable UUID itemId, 
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(inventoryService.adjustStock(itemId, request));
    }
}
