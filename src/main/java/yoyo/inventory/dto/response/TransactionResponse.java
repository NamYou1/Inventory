package yoyo.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private LocalDateTime tranDate;
    private String referenceNo;
    private TransactionType transactionType;
    private Long referenceId;
    private BigDecimal quantity;
    private BigDecimal unitQuantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalAmount;
    private SaleStatus status;
    private String createdBy;

    // Flat properties from relations
    private Long storeId;
    private String storeName;
    private Long productId;
    private String productName;
    private String productCode;
    private Long unitId;
    private String unitName;
}
