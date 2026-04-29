package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import yoyo.inventory.dto.request.ProductRequest;
import yoyo.inventory.dto.response.ProductResponse;
import yoyo.inventory.entities.Product;
import yoyo.inventory.services.CategoryService;
import yoyo.inventory.services.SubCategoryService;
import yoyo.inventory.services.UnitService;

@Mapper(uses = {CategoryService.class , SubCategoryService.class , UnitService.class} , componentModel = "spring" )
public interface ProductMapper {

    @Mapping(target = "categoryId" , source = "tblCategory.id")
    @Mapping(target = "categoryName" , source = "tblCategory.name")
    @Mapping(target = "subCategoryId" , source = "tblSubCategory.id")
    @Mapping(target = "subCategoryName" , source = "tblSubCategory.name")
    @Mapping(target = "unitId" , source = "tblUnit.id")
    @Mapping(target = "unitName" , source = "tblUnit.name")
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tblCategory", source = "categoryId")
    @Mapping(target = "tblSubCategory", source = "subCategoryId")
    @Mapping(target = "tblUnit", source = "unitId")
    Product toEntity(ProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tblCategory", source = "categoryId")
    @Mapping(target = "tblSubCategory", source = "subCategoryId")
    @Mapping(target = "tblUnit", source = "unitId")

    void updateFromRequest (ProductRequest request , @MappingTarget Product product);
}
