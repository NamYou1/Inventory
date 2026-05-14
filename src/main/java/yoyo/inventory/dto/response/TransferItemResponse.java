package yoyo.inventory.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferItemResponse {

    private Long id;
    private Integer productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal cost;
    private BigDecimal subtotal;
    private Integer productUnitId;
    private BigDecimal unitQuantity;
    private Integer transferUnitId;
}
