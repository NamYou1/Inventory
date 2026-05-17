package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItemRequest {
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    private  long unitId ;
    private BigDecimal quantity;
//    private BigDecimal costPrice;
    private Double totalDiscount;
}
