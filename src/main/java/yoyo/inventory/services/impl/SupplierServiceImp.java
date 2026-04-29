package yoyo.inventory.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.Pagination;
import yoyo.inventory.common.UniqueChecker;
import yoyo.inventory.dto.request.SupplierRequest;
import yoyo.inventory.dto.response.SupplierResponse;
import yoyo.inventory.entities.Suppliers;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundExecption;
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
    public Page<SupplierResponse> getAllSuppliers(Map<String, String> params) {
        SupplierFilter filter = objectMapper.convertValue(params, SupplierFilter.class);
        int pageNumber  = params.containsKey(Pagination.PAGE_NUMBER) ? Integer.parseInt(params.get(Pagination.PAGE_NUMBER)) : Pagination.DEFAULT_PAGE_NUMBER;
        int pageSize  = params.containsKey(Pagination.DEFAULT_PAGE_LIMIT) ? Integer.parseInt(params.get(Pagination.DEFAULT_PAGE_LIMIT)) : Pagination.DEFAULT_PAGE_LIMIT;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Suppliers> spec = SupplierSpec.filterBy(filter);
        return  supplierRepository.findAll(spec, pageable).map(supplierMapper::toResponse);
    }

    @Override
    public Suppliers findById(Long id) {
        return  supplierRepository.findById(id).orElseThrow(()->new ResourceNotFoundExecption("Suppliers", id));
    }

    @Override
    public SupplierResponse getById(Long id) {
        return supplierMapper.toResponse(findById(id));
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        Suppliers suppliers = supplierMapper.toEntity(request);
        uniqueChecker.verify(supplierRepository , suppliers , "name" , suppliers.getName());
        uniqueChecker.verify(supplierRepository , suppliers , "email" , suppliers.getEmail());
        supplierRepository.save(suppliers);
        return supplierMapper.toResponse(suppliers);
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Suppliers existingSupplier = findById(id);
        supplierMapper.updateFromRequest(request, existingSupplier);
        uniqueChecker.verify(supplierRepository , existingSupplier , "name" , existingSupplier.getName());
        uniqueChecker.verify(supplierRepository , existingSupplier , "email" , existingSupplier.getEmail());
        supplierRepository.save(existingSupplier);
        return supplierMapper.toResponse(existingSupplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        Suppliers suppliers = findById(id);
        suppliers.setStatus(Status.INACTIVE);
        supplierRepository.save(suppliers);
    }
}
