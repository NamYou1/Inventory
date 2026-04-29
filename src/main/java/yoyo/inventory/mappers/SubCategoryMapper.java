package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import yoyo.inventory.dto.request.SubCategoryRequest;
import yoyo.inventory.dto.response.SubCategoryResponse;
import yoyo.inventory.entities.SubCategory;
import yoyo.inventory.services.CategoryService;

@Mapper(componentModel = "spring" , uses = {CategoryService.class})
public interface SubCategoryMapper {
    @Mapping(target = "categoryId" , source = "tblCategory.id")
    @Mapping(target = "categoryName" , source = "tblCategory.name")
    SubCategoryResponse toResponse(SubCategory response);

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "tblCategory" , source = "categoryId")
    SubCategory toEnitty (SubCategoryRequest request);
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "tblCategory" , source = "categoryId")
    void updateFromRequest (SubCategoryRequest request , @MappingTarget  SubCategory subCategory);

}
