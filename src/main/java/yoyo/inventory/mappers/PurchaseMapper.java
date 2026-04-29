package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yoyo.inventory.dto.request.PurchaseItemRequest;
import yoyo.inventory.dto.request.PurchaseRequest;
import yoyo.inventory.dto.response.PurchaseItemResponse;
import yoyo.inventory.dto.response.PurchaseResponse;
import yoyo.inventory.entities.PurchaseItem;
import yoyo.inventory.entities.Purchases;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.SellerService;
import yoyo.inventory.services.SupplierService;

import java.util.List;

@Mapper(componentModel = "spring" , uses = {SupplierService.class , SellerService.class , ProductService.class})
public interface PurchaseMapper {

    @Mapping(target = "supplierId", source = "tblSuppliers.id")
    @Mapping(target = "sellerId", source = "tblSeller.id")
    PurchaseResponse toResponsePurchase(Purchases purchases);

    @Mapping(target = "productId", source = "tblProduct.id")
    @Mapping(target = "productName", source = "tblProduct.name")
    @Mapping(target = "storeId", source = "tblStore.id")
    @Mapping(target = "storeName", source = "tblStore.name")
    PurchaseItemResponse toPurchaseItemResponse(PurchaseItem item);
//    PurchaseResponse toResponsePurchase(Purchases purchases);
//
//    @Mapping(target = "id", ignore = true)
////    @Mapping(target = "tblStore" , source = "storeId")
//    @Mapping(target = "tblSuppliers" , source = "supplierId")
//    @Mapping(target = "tblSeller" , source = "sellerId")
//    Purchases toEntityPurchase(PurchaseRequest request);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "tblProduct" , source = "productId")
//    PurchaseItem toEntityPurchaseItem(PurchaseItemRequest request);
////    @Mapping(target = "id", ignore = true)
////    @Mapping(target = "tblProduct" , source = "productId")
//    List<PurchaseItem> toItemEntities(List<PurchaseItemRequest> requests);

    // stock


}
