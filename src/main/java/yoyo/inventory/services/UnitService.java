package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.UnitRequest;
import yoyo.inventory.dto.response.UnitResponse;
import yoyo.inventory.entities.Unit;

import java.util.Map;

public interface UnitService {
    Page<UnitResponse> getAll(Map<String , String> params);
    Unit findById(Long id );
    UnitResponse getById(Long id);
    UnitResponse create(UnitRequest request);
    UnitResponse update(Long id , UnitRequest request);
    void delete(Long id);
}
