package yoyo.inventory.specification.purchase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseFilter {
    private String reference;
    private String no;
    private Long storeId;
    private Long supplierId;
    private Long sellerId;
    private String purchaseStatus;
    private String paymentStatus;
}
