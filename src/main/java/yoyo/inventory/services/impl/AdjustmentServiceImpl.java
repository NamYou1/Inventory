package yoyo.inventory.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.PageUtil;
import yoyo.inventory.dto.request.AdjustmentRequest;
import yoyo.inventory.dto.response.AdjustmentResponse;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.StockAdjustment;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.enums.AdjustmentStatus;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.AdjustmentMapper;
import yoyo.inventory.repository.AdjustmentRepository;
import yoyo.inventory.services.AdjustmentService;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.StockService;
import yoyo.inventory.services.StoreService;
import yoyo.inventory.specification.adjustment.AdjustmentFilter;
import yoyo.inventory.specification.adjustment.AdjustmentSpec;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AdjustmentServiceImpl implements AdjustmentService {

    private final AdjustmentRepository adjustmentRepository;
    private final AdjustmentMapper adjustmentMapper;
    private final StockService stockService;
    private final ProductService productService;
    private final StoreService storeService;
    private final ObjectMapper objectMapper;

    @Override
//    @CacheEvict(cacheNames = {"adjustment-page", "adjustment-entity", "adjustment-response"}, allEntries = true)
    public AdjustmentResponse create(AdjustmentRequest request, String createdBy)
    {
     Product product = productService.findById(request.getProductId());
     Stores stores = storeService.findById(request.getStoreId());
        // =====================================
        // UPDATE STOCK
        // =====================================
        stockService.adjustStock(request.getProductId(), request.getStoreId(), request.getQuantity(), request.getAdjustmentType());
        // =====================================
        // SAVE ADJUSTMENT
        // =====================================
//        StockAdjustment adjustment = adjustmentMapper.toEntity(request);
//        adjustment.setReferenceNo("ADJ-" + System.currentTimeMillis());
//        adjustment.setStatus(AdjustmentStatus.COMPLETED);
//        adjustment.setAdjustmentDate(LocalDateTime.now());
//        adjustment.setCreatedBy(createdBy);
        StockAdjustment adjustment = StockAdjustment.builder()
                        .referenceNo("ADJ-" + System.currentTimeMillis())
                        .product(product)
                        .store(stores)
                        .quantity(request.getQuantity())
                        .adjustmentType(request.getAdjustmentType())
                        .reason(request.getReason())
                        .status(AdjustmentStatus.COMPLETED)
                        .adjustmentDate(LocalDateTime.now())
                        .createdBy(createdBy)
                        .build();
        adjustmentRepository.save(adjustment);
        return adjustmentMapper.toResponse(adjustment);
    }

    @Override
    @Cacheable(cacheNames = "adjustment-entity", key = "#id")
    public StockAdjustment findById(Long id) {
        return adjustmentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Adjustment", id));
    }

    @Override
//    @Cacheable(cacheNames = "adjustment-response", key = "#id")
    public AdjustmentResponse getById(Long id) {
        return adjustmentMapper.toResponse(findById(id));
    }

    @Override
//    @Cacheable(cacheNames = "adjustment-page", key = "#params.toString()")
    public Page<AdjustmentResponse> getAll(Map<String, String> params) {
        AdjustmentFilter filter = objectMapper.convertValue(params, AdjustmentFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<StockAdjustment> spec = AdjustmentSpec.filterBy(filter);
        return  adjustmentRepository.findAll(spec , pageable).map(adjustmentMapper::toResponse);
    }
    @Override
//    @CacheEvict(cacheNames = {"adjustment-page", "adjustment-entity", "adjustment-response"}, allEntries = true)
    public AdjustmentResponse updateStatus(Long id, AdjustmentStatus status, String updatedBy)
    {
        StockAdjustment adjustment = findById(id);
        adjustment.setStatus(status);
        adjustment.setUpdatedBy(updatedBy);
        adjustmentRepository.save(adjustment);
        return adjustmentMapper.toResponse(adjustment);
    }

    @Override
//    @CacheEvict(cacheNames = {"adjustment-page", "adjustment-entity", "adjustment-response"}, allEntries = true)
    public void delete(Long id) {
        StockAdjustment adjustment = findById(id);
        adjustment.setStatus(AdjustmentStatus.CANCELLED);
        adjustmentRepository.delete(adjustment);
    }
}
