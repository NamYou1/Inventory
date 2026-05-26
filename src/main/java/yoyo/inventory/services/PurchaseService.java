package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.PurchaseRequest;
import yoyo.inventory.dto.response.PurchaseResponse;

import java.util.Map;

public interface PurchaseService {
    PurchaseResponse create(PurchaseRequest request);
    Page<PurchaseResponse> getAll(Map<String, String> params);
    PurchaseResponse getById(Long id);
    PurchaseResponse update(Long id, PurchaseRequest request, String updatedBy);
    PurchaseResponse approve(Long id, String updatedBy);
    PurchaseResponse complete(Long id, String updatedBy);
    PurchaseResponse cancel(Long id, String updatedBy);
    void delete(Long id, String deletedBy);
}
