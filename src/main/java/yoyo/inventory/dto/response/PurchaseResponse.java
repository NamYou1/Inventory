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
    private  Long supplierId ;
    private String supplierName;
    private Long storeId ;
    private String storeName;
    private Long userId ;
    private String userName;
    private BigDecimal total;
    private BigDecimal totalDiscount;
    private BigDecimal grandTotal;
    private String purchasesStatus;
    private List<PurchaseItemResponse> items;
}