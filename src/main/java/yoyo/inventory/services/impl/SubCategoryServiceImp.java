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
import yoyo.inventory.dto.request.SubCategoryRequest;
import yoyo.inventory.dto.response.SubCategoryResponse;
import yoyo.inventory.entities.SubCategory;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundExecption;
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
    public Page<SubCategoryResponse> getAll(Map<String, String> params) {
        SubCategoryFilter filter = objectMapper.convertValue(params, SubCategoryFilter.class);
        int pageNumber  = params.containsKey(Pagination.PAGE_NUMBER) ? Integer.parseInt(params.get(Pagination.PAGE_NUMBER)) : Pagination.DEFAULT_PAGE_NUMBER;
        int pageSize  = params.containsKey(Pagination.DEFAULT_PAGE_LIMIT) ? Integer.parseInt(params.get(Pagination.DEFAULT_PAGE_LIMIT)) : Pagination.DEFAULT_PAGE_LIMIT;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<SubCategory> spec = SubCategorySpec.filterBy(filter);
        return  subCategoryRepository.findAll(spec , pageable).map(subCategoryMapper::toResponse);


    }

    @Override
    public SubCategory findById(Long id) {
        return  subCategoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundExecption("SubCategory" , id));
    }

    @Override
    public SubCategoryResponse getById(Long id) {
        return subCategoryMapper.toResponse(findById(id));
    }

    @Override
    public SubCategoryResponse create(SubCategoryRequest request) {
        SubCategory subCategory = subCategoryMapper.toEnitty(request);
        uniqueChecker.verify(subCategoryRepository , subCategory , "name" , subCategory.getName());
        uniqueChecker.verify(subCategoryRepository , subCategory , "code" , subCategory.getCode());
        subCategory = subCategoryRepository.save(subCategory);
         return subCategoryMapper.toResponse(subCategory);
    }

    @Override
    public SubCategoryResponse update(Long id, SubCategoryRequest request) {
        SubCategory exitsId = findById(id);
        subCategoryMapper.updateFromRequest(request , exitsId);
        uniqueChecker.verify(subCategoryRepository , exitsId , "name" , exitsId.getName());
        uniqueChecker.verify(subCategoryRepository , exitsId , "code" , exitsId.getCode());
        exitsId = subCategoryRepository.save(exitsId);
        return subCategoryMapper.toResponse(exitsId);
    }

    @Override
    public void delete(Long id) {
        SubCategory subCategory = findById(id);
        subCategory.setStatus(Status.INACTIVE);
        subCategoryRepository.save(subCategory);
    }
}
