package yoyo.inventory.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yoyo.inventory.dto.response.InvoiceResponse;
import yoyo.inventory.dto.response.SaleItemResponse;
import yoyo.inventory.dto.response.SaleResponse;
import yoyo.inventory.entities.Invoice;
import yoyo.inventory.entities.Sale;
import yoyo.inventory.entities.SaleItem;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    @Mapping(source = "store.name", target = "storeName")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "invoice", target = "invoice")
    SaleResponse toResponse(Sale sale);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    SaleItemResponse toItemResponse(SaleItem saleItem);

    @Mapping(source = "sale.invoiceNo", target = "saleInvoiceNo")
    @Mapping(source = "customer.fullName", target = "customerName")
    InvoiceResponse toInvoiceResponse(Invoice invoice);
}