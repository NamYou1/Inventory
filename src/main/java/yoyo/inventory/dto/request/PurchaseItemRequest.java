package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItemRequest {
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    private Double quantity;
    private Double costPrice;
    private Double totalDiscount;
}
