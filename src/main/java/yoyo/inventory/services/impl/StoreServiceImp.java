package yoyo.inventory.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.Pagination;
import yoyo.inventory.common.UniqueChecker;
import yoyo.inventory.dto.request.StoreRequest;
import yoyo.inventory.dto.response.StoreResponse;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundExecption;
import yoyo.inventory.mappers.StoreMapper;
import yoyo.inventory.repository.StoreRepository;
import yoyo.inventory.services.StoreService;
import yoyo.inventory.specification.store.StoreFilter;
import yoyo.inventory.specification.store.StoreSpec;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class StoreServiceImp implements StoreService {
    private  final StoreRepository storeRepository ;
    private  final  UniqueChecker uniqueChecker ;
    private  final  StoreMapper storeMapper ;
    private  final ObjectMapper objectMapper ;
    @Override
    public Page<StoreResponse> getAll(Map<String, String> params) {
        StoreFilter filter = objectMapper.convertValue(params, StoreFilter.class);
        int pageNumber  = params.containsKey(Pagination.PAGE_NUMBER) ? Integer.parseInt(params.get(Pagination.PAGE_NUMBER)) : Pagination.DEFAULT_PAGE_NUMBER;
        int pageSize  = params.containsKey(Pagination.DEFAULT_PAGE_LIMIT) ? Integer.parseInt(params.get(Pagination.DEFAULT_PAGE_LIMIT)) : Pagination.DEFAULT_PAGE_LIMIT;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Stores> spec = StoreSpec.filterBy(filter);
        return  storeRepository.findAll(spec , pageable).map(storeMapper::toResponse);
    }

    @Override
    public Stores findById(Long id) {
        return storeRepository.findById(id).orElseThrow(()->new ResourceNotFoundExecption("Store",id));
    }

    @Override
    public StoreResponse getById(Long id) {
        return storeMapper.toResponse(findById(id));
    }

    @Override
    public StoreResponse create(StoreRequest request) {
        Stores stores = storeMapper.toEntity(request);
        uniqueChecker.verify(storeRepository, stores , "name" , stores.getName());
        uniqueChecker.verify(storeRepository, stores , "code" , stores.getCode());
        uniqueChecker.verify(storeRepository, stores , "phone" , stores.getPhone());
        uniqueChecker.verify(storeRepository, stores , "email" , stores.getEmail());
        storeRepository.save(stores);
        return storeMapper.toResponse(stores);
    }

    @Override
    public StoreResponse update(Long id, StoreRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {
        Stores stores  = findById(id);
        stores.setStatus(Status.ACTIVE);
        storeRepository.save(stores);
    }
}
