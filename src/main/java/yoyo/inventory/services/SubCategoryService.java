package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.SubCategoryRequest;
import yoyo.inventory.dto.response.SubCategoryResponse;
import yoyo.inventory.entities.SubCategory;

import java.util.Map;

public interface SubCategoryService {
    Page<SubCategoryResponse> getAll(Map<String , String> params);
    SubCategory findById(Long id );
    SubCategoryResponse getById(Long id );
    SubCategoryResponse create(SubCategoryRequest request);
    SubCategoryResponse update(Long id , SubCategoryRequest request);
    void delete(Long id);
}
