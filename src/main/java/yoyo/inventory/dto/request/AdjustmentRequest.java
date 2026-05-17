package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import yoyo.inventory.enums.AdjustmentType;

import java.math.BigDecimal;

@Data
public class AdjustmentRequest {

    @NotNull
    private Long productId;
    @NotNull
    private Long storeId;
    @NotNull
    private BigDecimal quantity;
    @NotNull
    private AdjustmentType adjustmentType;
    private String reason;
}