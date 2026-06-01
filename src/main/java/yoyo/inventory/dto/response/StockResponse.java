package yoyo.inventory.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class StockResponse {

    private Long id;
    private BigDecimal quantity;
    private BigDecimal costPrice;
    private Integer reorderLevel;
    private Integer alertQuantity;
    private LocalDateTime lastRestockDate;
    private String productName;
    private String storeName;
}