package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import yoyo.inventory.dto.request.CategoryRequest;
import yoyo.inventory.dto.response.CategoryResponse;
import yoyo.inventory.entities.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse (Category category);
    @Mapping(target = "id" , ignore = true)
    Category toEntity (CategoryRequest request);
    @Mapping(target = "id" , ignore = true)
    void updateFromRequestt (CategoryRequest request , @MappingTarget Category category);
}
