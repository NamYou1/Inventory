package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yoyo.inventory.dto.response.StockResponse;
import yoyo.inventory.entities.Stock;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(
            source = "tblProduct.name",
            target = "productName"
    )
    @Mapping(
            source = "tblStore.name",
            target = "storeName"
    )
    StockResponse toResponse(Stock stock);
}