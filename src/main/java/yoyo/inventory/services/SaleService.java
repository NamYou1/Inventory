package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import yoyo.inventory.dto.request.SaleRequest;
import yoyo.inventory.dto.response.SaleResponse;
import yoyo.inventory.entities.Sale;
import yoyo.inventory.enums.SaleStatus;

public interface SaleService {

    SaleResponse create(SaleRequest request, String createdBy);

    SaleResponse getById(Long id);
    Page<SaleResponse> getAll(Pageable pageable);
    Sale findById(Long id );
    SaleResponse updateStatus(Long id, SaleStatus status, String updatedBy);
    void delete(Long id);
}