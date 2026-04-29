package yoyo.inventory.mappers;

import org.mapstruct.*;
import yoyo.inventory.dto.request.TransferItemRequest;
import yoyo.inventory.dto.request.TransferRequest;
import yoyo.inventory.dto.response.TransferItemResponse;
import yoyo.inventory.dto.response.TransferResponse;
import yoyo.inventory.entities.Transfer;
import yoyo.inventory.entities.TransferItem;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.StoreService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StoreService.class , ProductService.class})
public interface TransferMapper {

    // CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fromStoreId" , source = "fromStoreId")
    @Mapping(target = "toStoreId" , source = "toStoreId")

    Transfer toEntity(TransferRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fromStoreId", source = "fromStoreId.id")
    @Mapping(target = "toStoreId", source = "fromStoreId.id")
    // RESPONSE
   TransferResponse toResponse(Transfer transfer);

//    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId" , source = "productId")
    TransferItem toItemRequest(TransferItemRequest entity);




    List<TransferResponse> toResponseList(List<Transfer> transfers);

    // UPDATE (only allowed fields)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fromStoreId" , source = "fromStoreId")
    @Mapping(target = "toStoreId" , source = "toStoreId")
    void updateEntity(TransferRequest request, @MappingTarget Transfer transfer);
}