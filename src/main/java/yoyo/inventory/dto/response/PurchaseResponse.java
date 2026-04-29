package yoyo.inventory.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseResponse {

    private Long id;
    private String reference;
    private LocalDateTime date;
    private String note;

    private BigDecimal total;
    private BigDecimal productDiscount;
    private BigDecimal orderDiscount;
    private BigDecimal totalDiscount;

    private BigDecimal grandTotal;
    private BigDecimal paid;
    private BigDecimal due;

    private Short received;
    private String attachment;
    private Integer no;

    // status
    private String status;
    private String purchaseStatus;
    private String paymentStatus;

    // relationships (flatten for performance)
    private long storeId ;
    private String storeName;
    private Long sellerId;
    private String sellerName;
    private Long supplierId;
    private String supplierName;
    private List<PurchaseItemResponse> items;
}