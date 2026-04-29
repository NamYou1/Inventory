package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import yoyo.inventory.dto.request.SellerRequest;
import yoyo.inventory.dto.response.SellerResponse;
import yoyo.inventory.entities.Seller;

@Mapper(componentModel = "spring")
public interface SellerMapper {
    SellerResponse toResponse(Seller seller);
    @Mapping(target = "id", ignore = true)
    Seller toEntity(SellerRequest request);
    @Mapping(target = "id", ignore = true)
    void updateFromRequest(SellerRequest request , @MappingTarget Seller seller);
}
