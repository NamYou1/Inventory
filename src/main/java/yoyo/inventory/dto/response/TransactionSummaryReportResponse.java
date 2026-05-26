package yoyo.inventory.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionSummaryReportResponse {
    private String period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private long saleCount;
    private BigDecimal saleAmount;

    private long purchaseCount;
    private BigDecimal purchaseAmount;

    private long transferOutCount;
    private BigDecimal transferOutAmount;

    private long transferInCount;
    private BigDecimal transferInAmount;
}
