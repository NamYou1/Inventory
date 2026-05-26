package yoyo.inventory.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.PageUtil;

import yoyo.inventory.common.UniqueChecker;
import yoyo.inventory.dto.request.StoreRequest;
import yoyo.inventory.dto.response.StoreResponse;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.entities.User;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.StoreMapper;
import yoyo.inventory.repository.StoreRepository;
import yoyo.inventory.repository.UserRepository;
import yoyo.inventory.services.StoreService;
import yoyo.inventory.specification.store.StoreFilter;
import yoyo.inventory.specification.store.StoreSpec;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class StoreServiceImp implements StoreService {
    private  final StoreRepository storeRepository ;
    private  final UserRepository userRepository ;
    private  final  UniqueChecker uniqueChecker ;
    private  final  StoreMapper storeMapper ;
    private  final ObjectMapper objectMapper ;
    @Override
//    @Cacheable(cacheNames = "store-page", key = "#params.toString()")
    public Page<StoreResponse> getAll(Map<String, String> params) {
        StoreFilter filter = objectMapper.convertValue(params, StoreFilter.class);
        Pageable pageable = PageUtil.fromParams(params);

        String usernameOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);

        Specification<Stores> spec = StoreSpec.filterBy(filter);

        if (currentUser != null && currentUser.getStore() != null) {
            boolean isSuperAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> "ROLE_SUPER_ADMIN".equals(role.getCode()));
            if (!isSuperAdmin) {
                Long userStoreId = currentUser.getStore().getId();
                spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), userStoreId));
            }
        }

        return  storeRepository.findAll(spec , pageable).map(storeMapper::toResponse);
    }

    @Override
//    @Cacheable(cacheNames = "store-entity", key = "#id")
    public Stores findById(Long id) {
        String usernameOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);

        if (currentUser != null && currentUser.getStore() != null) {
            boolean isSuperAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> "ROLE_SUPER_ADMIN".equals(role.getCode()));
            if (!isSuperAdmin && !currentUser.getStore().getId().equals(id)) {
                throw new org.springframework.security.access.AccessDeniedException("You do not have permission to access this store.");
            }
        }
        return storeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Store",id));
    }

    @Override
//    @Cacheable(cacheNames = "store-response", key = "#id")
    public StoreResponse getById(Long id) {
        return storeMapper.toResponse(findById(id));
    }

    @Override
//    @CacheEvict(cacheNames = {"store-page", "store-entity", "store-response"}, allEntries = true)
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
//    @CacheEvict(cacheNames = {"store-page", "store-entity", "store-response"}, allEntries = true)
    public StoreResponse update(Long id, StoreRequest request) {
        Stores stores = findById(id);
        storeMapper.updateFromRequest(request, stores);
        uniqueChecker.verify(storeRepository, stores, "name", stores.getName());
        uniqueChecker.verify(storeRepository, stores, "code", stores.getCode());
        uniqueChecker.verify(storeRepository, stores, "phone", stores.getPhone());
        uniqueChecker.verify(storeRepository, stores, "email", stores.getEmail());
        storeRepository.save(stores);
        return storeMapper.toResponse(stores);
    }

    @Override
//    @CacheEvict(cacheNames = {"store-page", "store-entity", "store-response"}, allEntries = true)
    public void delete(Long id) {
        Stores stores  = findById(id);
        stores.setStatus(Status.ACTIVE);
        storeRepository.save(stores);
    }
}
