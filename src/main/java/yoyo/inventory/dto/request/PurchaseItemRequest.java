package yoyo.inventory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItemRequest {
    private Long productId;
    private Double quantity;
    private Double costPrice;
    private Double totalDiscount;
}
