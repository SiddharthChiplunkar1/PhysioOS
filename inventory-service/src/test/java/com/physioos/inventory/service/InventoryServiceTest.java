package com.physioos.inventory.service;

import com.physioos.inventory.dto.InventoryResponse;
import com.physioos.inventory.dto.ItemCreateRequest;
import com.physioos.inventory.dto.StockAdjustmentRequest;
import com.physioos.inventory.entity.InventoryItem;
import com.physioos.inventory.entity.ItemCategory;
import com.physioos.inventory.entity.StockMovement;
import com.physioos.inventory.repository.InventoryItemRepository;
import com.physioos.inventory.repository.StockMovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryItemRepository itemRepository;

    @Mock
    private StockMovementRepository movementRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void testCreateItem_SetsDefaultStock() {
        // Arrange
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("Ultrasound Gel");
        request.setCategory(ItemCategory.CONSUMABLE);
        request.setInitialStock(5);
        request.setMinimumThreshold(10); // Will trigger LOW STOCK immediately

        when(itemRepository.save(any(InventoryItem.class))).thenAnswer(i -> {
            InventoryItem item = i.getArgument(0);
            item.setId(UUID.randomUUID());
            return item;
        });

        // Act
        InventoryResponse response = inventoryService.createItem(request);

        // Assert
        assertEquals(5, response.getCurrentStock());
        assertEquals("LOW STOCK", response.getAlertStatus());
        verify(movementRepository).save(any(StockMovement.class)); // Verifies initial stock was recorded
    }

    @Test
    void testAdjustStock_NegativeResult_ThrowsException() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        InventoryItem item = InventoryItem.builder()
                .id(itemId)
                .name("Bandages")
                .currentStock(5)
                .minimumThreshold(10)
                .build();

        StockAdjustmentRequest request = new StockAdjustmentRequest();
        request.setQuantityChange(-10); // 5 - 10 = -5 (Invalid)
        request.setMovementType("USAGE");
        request.setReason("Patient treatment");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> inventoryService.adjustStock(itemId, request));
        assertTrue(exception.getMessage().contains("negative inventory"));
        verify(movementRepository, never()).save(any());
    }

    @Test
    void testAdjustStock_Out_Of_Stock_Alert() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        InventoryItem item = InventoryItem.builder()
                .id(itemId)
                .name("Bandages")
                .currentStock(5)
                .minimumThreshold(10)
                .build();

        StockAdjustmentRequest request = new StockAdjustmentRequest();
        request.setQuantityChange(-5); // Drops stock to 0
        request.setMovementType("USAGE");
        request.setReason("Patient treatment");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        InventoryResponse response = inventoryService.adjustStock(itemId, request);

        // Assert
        assertEquals(0, response.getCurrentStock());
        assertEquals("OUT OF STOCK", response.getAlertStatus());
    }
}
