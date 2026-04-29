package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import yoyo.inventory.dto.request.StoreRequest;
import yoyo.inventory.dto.response.StoreResponse;
import yoyo.inventory.entities.Stores;

@Mapper(componentModel = "spring")
public interface StoreMapper {
    StoreResponse toResponse(Stores stores);
    Stores toEntity(StoreRequest request);

    void updateFromRequest(StoreRequest request , @MappingTarget Stores stores);
}
