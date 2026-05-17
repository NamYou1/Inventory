package yoyo.inventory.dto.response;

import lombok.Builder;
import lombok.Data;
import yoyo.inventory.enums.AdjustmentStatus;
import yoyo.inventory.enums.AdjustmentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdjustmentResponse {

    private Long id;

    private String referenceNo;

    private String productName;

    private String storeName;

    private BigDecimal quantity;

    private AdjustmentType adjustmentType;

    private AdjustmentStatus status;

    private String reason;

    private LocalDateTime adjustmentDate;

    private String createdBy;
}