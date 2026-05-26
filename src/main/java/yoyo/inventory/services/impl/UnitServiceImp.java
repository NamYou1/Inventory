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
import yoyo.inventory.dto.request.UnitRequest;
import yoyo.inventory.dto.response.UnitResponse;
import yoyo.inventory.entities.Unit;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.UnitMapper;
import yoyo.inventory.repository.UnitRepository;
import yoyo.inventory.services.UnitService;
import yoyo.inventory.specification.Unit.UnitFilter;
import yoyo.inventory.specification.Unit.UnitSpec;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UnitServiceImp implements UnitService {
    private  final UnitRepository unitRepository ;
    private  final ObjectMapper objectMapper ;
    private  final UniqueChecker uniqueChecker ;
    private  final UnitMapper unitMapper ;


    @Override
//    @Cacheable(cacheNames = "unit-page", key = "#params.toString()")
    public Page<UnitResponse> getAll(Map<String, String> params) {
        UnitFilter filter = objectMapper.convertValue(params, UnitFilter.class);
        Pageable pageable = PageUtil.fromParams(params);

        Specification<Unit> spec = UnitSpec.filterBy(filter);
        return unitRepository.findAll(spec, pageable).map(unitMapper::toResponse);
    }

    @Override
//    @Cacheable(cacheNames = "unit-entity", key = "#id")
    public Unit findById(Long id) {

        return  unitRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Unit" , id));
    }

    @Override
//    @Cacheable(cacheNames = "unit-response", key = "#id")
    public UnitResponse getById(Long id) {
        return  unitMapper.toResponse(findById(id));
    }

    @Override
//    @CacheEvict(cacheNames = {"unit-page", "unit-entity", "unit-response"}, allEntries = true)
    public UnitResponse create(UnitRequest request) {
        Unit unit = unitMapper.toEntity(request);
        uniqueChecker.verify(unitRepository , unit , "code" , unit.getCode());
        uniqueChecker.verify(unitRepository , unit , "name" , unit.getName());
        unitRepository.save(unit);
        return unitMapper.toResponse(unit);
    }

    @Override
//    @CacheEvict(cacheNames = {"unit-page", "unit-entity", "unit-response"}, allEntries = true)
    public UnitResponse update(Long id, UnitRequest request) {
        Unit unit = findById(id);
        unitMapper.updateFromRequest(request , unit);
        uniqueChecker.verify(unitRepository , unit , "code" , unit.getCode());
        uniqueChecker.verify(unitRepository , unit , "name" , unit.getName());
        unitRepository.save(unit);
        return unitMapper.toResponse(unit);
    }

    @Override
//    @CacheEvict(cacheNames = {"unit-page", "unit-entity", "unit-response"}, allEntries = true)
    public void delete(Long id) {
        Unit unit = findById(id);
        unit.setStatus(Status.INACTIVE);
        unitRepository.save(unit);
    }
}
