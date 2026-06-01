package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import yoyo.inventory.dto.request.AdjustmentRequest;
import yoyo.inventory.dto.response.AdjustmentResponse;
import yoyo.inventory.entities.StockAdjustment;
import yoyo.inventory.enums.AdjustmentStatus;

import java.util.Map;

public interface AdjustmentService {
    Page<AdjustmentResponse> getAll(Map<String, String> params);
    AdjustmentResponse create(AdjustmentRequest request, String createdBy);
    StockAdjustment findById(Long id);
    AdjustmentResponse getById(Long id);
    AdjustmentResponse updateStatus(Long id, AdjustmentStatus status, String updatedBy);
    void delete(Long id);
}