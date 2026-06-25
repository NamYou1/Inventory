package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yoyo.inventory.dto.request.PurchaseItemRequest;
import yoyo.inventory.dto.request.PurchaseRequest;
import yoyo.inventory.dto.response.PurchaseItemResponse;
import yoyo.inventory.dto.response.PurchaseResponse;
import yoyo.inventory.entities.PurchaseItem;
import yoyo.inventory.entities.Purchases;
import yoyo.inventory.services.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SupplierService.class, UserService.class, ProductService.class, StoreService.class, UnitService.class})
public interface PurchaseMapper {

//    PurchaseResponse toResponsePurchase (Purchases purchases );


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tblSuppliers" , source = "supplierId")
    @Mapping(target = "tblUser" , source = "userId")
    @Mapping(target = "tblStore" , source = "storeId")
    @Mapping(target = "tblPurchaseItem", source = "items")
    Purchases toEntityPurchase(PurchaseRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tblProduct", source = "productId")
    @Mapping(target = "tblUnit", source = "unitId")
//    @Mapping(target = "")
    PurchaseItem toItemEntity(PurchaseItemRequest request);

    @Mapping(target = "supplierId", source = "tblSuppliers.id")
    @Mapping(target = "supplierName", source = "tblSuppliers.name")
    @Mapping(target = "storeId", source = "tblStore.id")
    @Mapping(target = "storeName", source = "tblStore.name")
    @Mapping(target = "userId", source = "tblUser.id")
    @Mapping(target = "userName", source = "tblUser.username")

    @Mapping(target = "purchasesStatus", expression = "java(entity.getPurchaseStatus() != null ? entity.getPurchaseStatus().name() : null)")
    @Mapping(target = "items", source = "tblPurchaseItem")
    PurchaseResponse toResponsePurchase(Purchases entity);

    List<PurchaseItem> toListEntityItem(List<PurchaseItemRequest> items);
    @Mapping(target = "productId", source = "tblProduct.id")
    @Mapping(target = "productName", source = "tblProduct.name")
    @Mapping(target = "productCode", source = "tblProduct.code")
    @Mapping(target = "unitName", source = "tblUnit.name")
    PurchaseItemResponse toItemResponse(PurchaseItem entity);

    List<PurchaseItemResponse> toItemResponseList(List<PurchaseItem> items);

    // stock


}
