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
import yoyo.inventory.dto.request.CategoryRequest;
import yoyo.inventory.dto.response.CategoryResponse;
import yoyo.inventory.entities.Category;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundExecption;
import yoyo.inventory.mappers.CategoryMapper;
import yoyo.inventory.repository.CategoryRepository;
import yoyo.inventory.services.CategoryService;
import yoyo.inventory.specification.categories.CategoryFilter;
import yoyo.inventory.specification.categories.CategorySpec;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private  final CategoryRepository categoryRepository ;
    private  final UniqueChecker uniqueChecker ;
    private  final ObjectMapper objectMapper ;
    private  final CategoryMapper categoryMapper ;
    @Override
    public Page<CategoryResponse> getAll(Map<String, String> params) {
        CategoryFilter filter = objectMapper.convertValue(params, CategoryFilter.class);
        int pageNumber  = params.containsKey(Pagination.PAGE_NUMBER) ? Integer.parseInt(params.get(Pagination.PAGE_NUMBER)) : Pagination.DEFAULT_PAGE_NUMBER;
        int pageSize  = params.containsKey(Pagination.DEFAULT_PAGE_LIMIT) ? Integer.parseInt(params.get(Pagination.DEFAULT_PAGE_LIMIT)) :0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Category> spec = CategorySpec.filterBy(filter);
        return  categoryRepository.findAll(spec , pageable).map(categoryMapper::toResponse);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundExecption("Category" , id));
    }

    @Override
    public CategoryResponse getById(Long id) {
        return categoryMapper.toResponse(findById(id));
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
         Category category =categoryMapper.toEntity(request);
         uniqueChecker.verify(categoryRepository , category , "name" , category.getName());
         Category saveCategory = categoryRepository.save(category);
         return  categoryMapper.toResponse(saveCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category  = findById(id);
        categoryMapper.updateFromRequestt(request , category);
        uniqueChecker.verify(categoryRepository , category , "name" , category.getName());
        Category updateCategory = categoryRepository.save(category);
        return  categoryMapper.toResponse(updateCategory);
    }

    @Override
    public void deleteCategory(long id) {
        Category category = findById(id);
        category.setStatus(Status.INACTIVE);
        categoryRepository.save(category);
    }
}
