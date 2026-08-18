package com.physioos.inventory.service;

import com.physioos.inventory.dto.InventoryResponse;
import com.physioos.inventory.dto.ItemCreateRequest;
import com.physioos.inventory.dto.StockAdjustmentRequest;
import com.physioos.inventory.entity.InventoryItem;
import com.physioos.inventory.entity.StockMovement;
import com.physioos.inventory.repository.InventoryItemRepository;
import com.physioos.inventory.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public InventoryResponse createItem(ItemCreateRequest request) {
        InventoryItem item = InventoryItem.builder()
                .organizationId(request.getOrganizationId())
                .clinicId(request.getClinicId())
                .name(request.getName())
                .sku(request.getSku())
                .category(request.getCategory())
                .currentStock(request.getInitialStock() != null ? request.getInitialStock() : 0)
                .minimumThreshold(request.getMinimumThreshold() != null ? request.getMinimumThreshold() : 0)
                .unitPrice(request.getUnitPrice())
                .build();

        item = inventoryItemRepository.save(item);

        if (item.getCurrentStock() > 0) {
            recordMovement(item, item.getCurrentStock(), "INITIAL_STOCK", "Initial stock setup", null);
        }

        return mapToResponse(item);
    }

    public List<InventoryResponse> getInventoryByClinic(UUID clinicId) {
        return inventoryItemRepository.findByClinicId(clinicId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryResponse adjustStock(UUID itemId, StockAdjustmentRequest request) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (request.getQuantityChange() == 0) {
            throw new IllegalArgumentException("Quantity change cannot be zero");
        }

        int newStock = item.getCurrentStock() + request.getQuantityChange();

        if (newStock < 0) {
            throw new IllegalArgumentException("Stock adjustment would result in negative inventory");
        }

        item.setCurrentStock(newStock);
        inventoryItemRepository.save(item);

        recordMovement(item, request.getQuantityChange(), request.getMovementType(), request.getReason(), request.getPerformedBy());

        // Check alerts
        String alertStatus = calculateAlertStatus(item);
        if (!"NORMAL".equals(alertStatus)) {
            log.warn("INVENTORY ALERT: Item {} ({}) is now {}", item.getName(), item.getId(), alertStatus);
            // In the future, publish to Kafka for Notification Service
        }

        return mapToResponse(item);
    }

    private void recordMovement(InventoryItem item, Integer quantity, String type, String reason, UUID performedBy) {
        StockMovement movement = StockMovement.builder()
                .item(item)
                .quantity(quantity)
                .movementType(type)
                .reason(reason)
                .performedBy(performedBy)
                .build();
        stockMovementRepository.save(movement);
    }

    private InventoryResponse mapToResponse(InventoryItem item) {
        InventoryResponse response = new InventoryResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setSku(item.getSku());
        response.setCategory(item.getCategory());
        response.setCurrentStock(item.getCurrentStock());
        response.setMinimumThreshold(item.getMinimumThreshold());
        response.setUnitPrice(item.getUnitPrice());
        response.setAlertStatus(calculateAlertStatus(item));
        return response;
    }

    private String calculateAlertStatus(InventoryItem item) {
        if (item.getCurrentStock() == 0) return "OUT OF STOCK";
        if (item.getCurrentStock() <= item.getMinimumThreshold()) return "LOW STOCK";
        return "NORMAL";
    }
}
