package yoyo.inventory.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yoyo.inventory.dto.request.AdjustmentRequest;
import yoyo.inventory.dto.response.AdjustmentResponse;
import yoyo.inventory.entities.StockAdjustment;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.StoreService;

@Mapper(componentModel = "spring" , uses = {ProductService.class , StoreService.class})
public interface AdjustmentMapper {

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "store.name", target = "storeName")
    AdjustmentResponse toResponse(StockAdjustment adjustment);

    @Mapping(target = "id" , ignore = true)
    @Mapping(source = "productId", target = "product")
    @Mapping(source = "storeId", target = "store")
    StockAdjustment toEntity(AdjustmentRequest request);


}