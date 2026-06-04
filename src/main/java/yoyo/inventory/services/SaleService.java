package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.SaleRequest;
import yoyo.inventory.dto.response.SaleResponse;
import yoyo.inventory.entities.Sale;
import yoyo.inventory.enums.SaleStatus;

import java.util.Map;

public interface SaleService {

    SaleResponse create(SaleRequest request, String createdBy);

    SaleResponse getById(Long id);
    Page<SaleResponse> getAll(Map<String, String> params);
    SaleResponse update(Long id, SaleRequest request, String updatedBy);
    SaleResponse approve(Long id, String updatedBy);
    SaleResponse complete(Long id, String updatedBy);
    SaleResponse cancel(Long id, String updatedBy);
    SaleResponse returnSale(Long id, String updatedBy);
    void delete(Long id, String deletedBy);

    // Backward compatibility
    Sale findById(Long id);
    SaleResponse updateStatus(Long id, SaleStatus status, String updatedBy);
}
