package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.CategoryRequest;
import yoyo.inventory.dto.response.CategoryResponse;
import yoyo.inventory.entities.Category;

import java.util.Map;

public interface CategoryService {
    Page<CategoryResponse> getAll(Map<String , String>  params);
    Category findById(Long id);
    CategoryResponse getById(Long id);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id , CategoryRequest request);
    void deleteCategory (long id );
}
