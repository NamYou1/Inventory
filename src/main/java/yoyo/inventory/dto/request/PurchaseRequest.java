package yoyo.inventory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.PurchaseItem;
import yoyo.inventory.entities.status.PaymentStatus;
import yoyo.inventory.entities.status.PurchaseStatus;
import yoyo.inventory.entities.status.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PurchaseRequest {
    private String reference;
    private String note;
    private Long supplierId;
    private Long userId;
    private Long storeId;
    private  PurchaseStatus purchaseStatus  = PurchaseStatus.ORDERED;
    private  PaymentStatus paymentStatus= PaymentStatus.PENDING;
    private LocalDateTime date = LocalDateTime.now();
    private Double orderDiscount;
    private List<PurchaseItemRequest> items;
}
