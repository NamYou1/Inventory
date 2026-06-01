package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleRequest {

    @NotNull
    private Long storeId;

    private Long customerId;

    private String note;

    private BigDecimal discountAmount;

    private BigDecimal taxAmount;

    @NotEmpty
    private List<SaleItemRequest> items;
}