package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yoyo.inventory.dto.response.TransactionResponse;
import yoyo.inventory.entities.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "tblStore.id", target = "storeId")
    @Mapping(source = "tblStore.name", target = "storeName")
    @Mapping(source = "tblProduct.id", target = "productId")
    @Mapping(source = "tblProduct.name", target = "productName")
    @Mapping(source = "tblProduct.code", target = "productCode")
    @Mapping(source = "tblUnit.id", target = "unitId")
    @Mapping(source = "tblUnit.name", target = "unitName")
    TransactionResponse toResponse(Transaction transaction);
}
