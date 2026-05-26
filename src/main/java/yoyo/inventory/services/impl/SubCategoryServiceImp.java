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
import yoyo.inventory.dto.request.SubCategoryRequest;
import yoyo.inventory.dto.response.SubCategoryResponse;
import yoyo.inventory.entities.SubCategory;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.SubCategoryMapper;
import yoyo.inventory.repository.SubCategoryRepository;
import yoyo.inventory.services.SubCategoryService;
import yoyo.inventory.specification.categories.SubCategoryFilter;
import yoyo.inventory.specification.categories.SubCategorySpec;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImp implements SubCategoryService {
    private  final SubCategoryRepository subCategoryRepository ;
    private  final ObjectMapper objectMapper ;
    private  final UniqueChecker uniqueChecker ;
    private  final SubCategoryMapper subCategoryMapper ;
    @Override
//    @Cacheable(cacheNames = "subcategory-page", key = "#params.toString()")
    public Page<SubCategoryResponse> getAll(Map<String, String> params) {
        SubCategoryFilter filter = objectMapper.convertValue(params, SubCategoryFilter.class);
        Pageable pageable = PageUtil.fromParams(params);

        Specification<SubCategory> spec = SubCategorySpec.filterBy(filter);
        return  subCategoryRepository.findAll(spec , pageable).map(subCategoryMapper::toResponse);


    }

    @Override
//    @Cacheable(cacheNames = "subcategory-entity", key = "#id")
    public SubCategory findById(Long id) {
        return  subCategoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("SubCategory" , id));
    }

    @Override
    @Cacheable(cacheNames = "subcategory-response", key = "#id")
    public SubCategoryResponse getById(Long id) {
        return subCategoryMapper.toResponse(findById(id));
    }

    @Override
//    @CacheEvict(cacheNames = {"subcategory-page", "subcategory-entity", "subcategory-response"}, allEntries = true)
    public SubCategoryResponse create(SubCategoryRequest request) {
        SubCategory subCategory = subCategoryMapper.toEnitty(request);
        uniqueChecker.verify(subCategoryRepository , subCategory , "name" , subCategory.getName());
        uniqueChecker.verify(subCategoryRepository , subCategory , "code" , subCategory.getCode());
        subCategory = subCategoryRepository.save(subCategory);
         return subCategoryMapper.toResponse(subCategory);
    }

    @Override
    @CacheEvict(cacheNames = {"subcategory-page", "subcategory-entity", "subcategory-response"}, allEntries = true)
    public SubCategoryResponse update(Long id, SubCategoryRequest request) {
        SubCategory exitsId = findById(id);
        subCategoryMapper.updateFromRequest(request , exitsId);
        uniqueChecker.verify(subCategoryRepository , exitsId , "name" , exitsId.getName());
        uniqueChecker.verify(subCategoryRepository , exitsId , "code" , exitsId.getCode());
        exitsId = subCategoryRepository.save(exitsId);
        return subCategoryMapper.toResponse(exitsId);
    }

    @Override
    @CacheEvict(cacheNames = {"subcategory-page", "subcategory-entity", "subcategory-response"}, allEntries = true)
    public void delete(Long id) {
        SubCategory subCategory = findById(id);
        subCategory.setStatus(Status.INACTIVE);
        subCategoryRepository.save(subCategory);
    }
}
