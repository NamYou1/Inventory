package yoyo.inventory.dto.response;

import java.math.BigDecimal;

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
