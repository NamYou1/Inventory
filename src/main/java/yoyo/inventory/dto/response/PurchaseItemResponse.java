package yoyo.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productCode;
    private BigDecimal quantity;
    private BigDecimal costPrice;
    private BigDecimal totalDiscount;
    private BigDecimal subtotal;
    private String unitName;
}
