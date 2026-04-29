package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import yoyo.inventory.dto.request.SupplierRequest;
import yoyo.inventory.dto.response.SupplierResponse;
import yoyo.inventory.entities.Suppliers;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponse toResponse(Suppliers supplier);
    @Mapping(target = "id", ignore = true)
    Suppliers toEntity (SupplierRequest request);
    @Mapping(target = "id", ignore = true)
    void updateFromRequest(SupplierRequest request ,@MappingTarget Suppliers supplier);
}
