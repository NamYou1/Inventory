package yoyo.inventory.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferItemRequest {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price must be >= 0")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", message = "Cost must be >= 0")
    @Builder.Default
    private BigDecimal cost = BigDecimal.ZERO;

    private Integer productUnitId;

    @Builder.Default
    private BigDecimal unitQuantity = BigDecimal.ZERO;

    private Integer transferUnitId;
}
