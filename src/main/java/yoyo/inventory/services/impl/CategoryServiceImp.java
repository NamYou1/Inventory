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
import yoyo.inventory.dto.request.CategoryRequest;
import yoyo.inventory.dto.response.CategoryResponse;
import yoyo.inventory.entities.Category;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.execption.ResourceNotFoundException;
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
//    @Cacheable(cacheNames = "category-page", key = "#params.toString()")
    public Page<CategoryResponse> getAll(Map<String, String> params) {
        CategoryFilter filter = objectMapper.convertValue(params, CategoryFilter.class);
        Pageable pageable = PageUtil.fromParams(params);

        Specification<Category> spec = CategorySpec.filterBy(filter);
        return  categoryRepository.findAll(spec , pageable).map(categoryMapper::toResponse);
    }

    @Override
//    @Cacheable(cacheNames = "category-entity", key = "#id")
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Category" , id));
    }

    @Override
//    @Cacheable(cacheNames = "category-response", key = "#id")
    public CategoryResponse getById(Long id) {
        return categoryMapper.toResponse(findById(id));
    }

    @Override
    @CacheEvict(cacheNames = {"category-page", "category-entity", "category-response"}, allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
         Category category =categoryMapper.toEntity(request);
         uniqueChecker.verify(categoryRepository , category , "name" , category.getName());
         Category saveCategory = categoryRepository.save(category);
         return  categoryMapper.toResponse(saveCategory);
    }

    @Override
//    @CacheEvict(cacheNames = {"category-page", "category-entity", "category-response"}, allEntries = true)
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category  = findById(id);
        categoryMapper.updateFromRequestt(request , category);
        uniqueChecker.verify(categoryRepository , category , "name" , category.getName());
        Category updateCategory = categoryRepository.save(category);
        return  categoryMapper.toResponse(updateCategory);
    }

    @Override
//    @CacheEvict(cacheNames = {"category-page", "category-entity", "category-response"}, allEntries = true)
    public void deleteCategory(long id) {
        Category category = findById(id);
        category.setStatus(Status.INACTIVE);
        categoryRepository.save(category);
    }
}
