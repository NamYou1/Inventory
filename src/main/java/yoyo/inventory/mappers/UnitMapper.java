package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import yoyo.inventory.dto.request.UnitRequest;
import yoyo.inventory.dto.response.UnitResponse;
import yoyo.inventory.entities.Unit;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    UnitResponse toResponse(Unit unit);
    @Mapping(target = "id", ignore = true)
    Unit toEntity(UnitRequest request);
    @Mapping(target = "id", ignore = true)
    void updateFromRequest(UnitRequest request , @MappingTarget Unit unit);
}
