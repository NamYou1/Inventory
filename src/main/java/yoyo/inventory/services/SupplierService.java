package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.SupplierRequest;
import yoyo.inventory.dto.response.SupplierResponse;
import yoyo.inventory.entities.Suppliers;

import java.util.Map;

public interface SupplierService {
    Page<SupplierResponse> getAllSuppliers(Map<String , String> params);
    Suppliers findById(Long id);
    SupplierResponse getById(Long id );
    SupplierResponse createSupplier(SupplierRequest request);
    SupplierResponse updateSupplier(Long id , SupplierRequest request);
    void deleteSupplier (Long id );
}
