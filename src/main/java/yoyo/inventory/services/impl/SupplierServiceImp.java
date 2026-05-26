package yoyo.inventory.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.PageUtil;
import yoyo.inventory.common.UniqueChecker;
import yoyo.inventory.dto.request.SupplierRequest;
import yoyo.inventory.dto.response.SupplierResponse;
import yoyo.inventory.entities.Suppliers;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.SupplierMapper;
import yoyo.inventory.repository.SupplierRepository;
import yoyo.inventory.services.SupplierService;
import yoyo.inventory.specification.suppliers.SupplierFilter;
import yoyo.inventory.specification.suppliers.SupplierSpec;

import java.util.Map;
@Service
@RequiredArgsConstructor
public class SupplierServiceImp implements SupplierService {
    private  final SupplierRepository supplierRepository;
    private  final UniqueChecker uniqueChecker ;
    private  final ObjectMapper objectMapper ;
    private  final SupplierMapper supplierMapper ;
    @Override
//    @Cacheable(cacheNames = "supplier-page", key = "#params.toString()")
    public Page<SupplierResponse> getAllSuppliers(Map<String, String> params) {
        SupplierFilter filter = objectMapper.convertValue(params, SupplierFilter.class);
        Pageable pageable = PageUtil.fromParams(params);

        Specification<Suppliers> spec = SupplierSpec.filterBy(filter);
        return  supplierRepository.findAll(spec, pageable).map(supplierMapper::toResponse);
    }

    @Override
//    @Cacheable(cacheNames = "supplier-entity", key = "#id")
    public Suppliers findById(Long id) {
        return  supplierRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Suppliers", id));
    }

    @Override
//    @Cacheable(cacheNames = "supplier-response", key = "#id")
    public SupplierResponse getById(Long id) {
        return supplierMapper.toResponse(findById(id));
    }

    @Override
//    @CacheEvict(cacheNames = {"supplier-page", "supplier-entity", "supplier-response"}, allEntries = true)
    public SupplierResponse createSupplier(SupplierRequest request) {
        Suppliers suppliers = supplierMapper.toEntity(request);
        uniqueChecker.verify(supplierRepository , suppliers , "name" , suppliers.getName());
        uniqueChecker.verify(supplierRepository , suppliers , "email" , suppliers.getEmail());
        supplierRepository.save(suppliers);
        return supplierMapper.toResponse(suppliers);
    }

    @Override
//    @CacheEvict(cacheNames = {"supplier-page", "supplier-entity", "supplier-response"}, allEntries = true)
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Suppliers existingSupplier = findById(id);
        supplierMapper.updateFromRequest(request, existingSupplier);
        uniqueChecker.verify(supplierRepository , existingSupplier , "name" , existingSupplier.getName());
        uniqueChecker.verify(supplierRepository , existingSupplier , "email" , existingSupplier.getEmail());
        supplierRepository.save(existingSupplier);
        return supplierMapper.toResponse(existingSupplier);
    }

    @Override
//    @CacheEvict(cacheNames = {"supplier-page", "supplier-entity", "supplier-response"}, allEntries = true)
    public void deleteSupplier(Long id) {
        Suppliers suppliers = findById(id);
        suppliers.setStatus(Status.INACTIVE);
        supplierRepository.save(suppliers);
    }
}
