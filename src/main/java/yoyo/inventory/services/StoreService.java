package yoyo.inventory.services;

import org.apache.catalina.Store;
import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.StoreRequest;
import yoyo.inventory.dto.response.StoreResponse;
import yoyo.inventory.entities.Stores;

import java.util.Map;

public interface StoreService {
    Page<StoreResponse> getAll(Map<String , String> params);
    Stores findById(Long id);
    StoreResponse getById(Long id);
    StoreResponse create(StoreRequest request);
    StoreResponse update(Long id , StoreRequest request);
    void delete(Long id);
}
