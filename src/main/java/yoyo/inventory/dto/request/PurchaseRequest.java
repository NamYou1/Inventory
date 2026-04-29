package yoyo.inventory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.PurchaseItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PurchaseRequest {
    private LocalDateTime date = LocalDateTime.now();
    private  String no ;
    private  String reference ;
    private  String note ;
    private  String attachment ;
    private  BigDecimal productDiscount;
    private  BigDecimal orderDiscount;
    private  Short received ;
    private  String status ;
    private  String purchasesStatus ;
    private  String paymentStatus ;
    private  Long supplierId ;
    private  Long sellerId  ;
//    private  Long storeId ;
    private List<PurchaseItem> tblPurchaseItem;
}
