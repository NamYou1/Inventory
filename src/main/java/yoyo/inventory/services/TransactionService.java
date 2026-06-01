package yoyo.inventory.services;

import yoyo.inventory.dto.response.TransactionSummaryReportResponse;
import yoyo.inventory.entities.Transaction;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    void logStockMovement(
            TransactionType type,
            Long referenceId,
            String referenceNo,
            Long productId,
            Long storeId,
            Long unitId,
            BigDecimal qty,
            BigDecimal unitQty,
            BigDecimal price,
            SaleStatus status,
            String createdBy
    );

    BigDecimal getStockBalance(Long storeId, Long productId);

    // សម្រាប់ទាញយក Transaction History តាមចន្លោះថ្ងៃខែ (សម្រាប់ធ្វើ Report ផ្សេងៗ)
    List<Transaction> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // សម្រាប់ទាញយក Report តាមប្រភេទជាក់លាក់ (ឧ. ចង់បានតែ Report លក់ ឬ ទិញ)
    List<Transaction> getReportsByType(TransactionType type);

    TransactionSummaryReportResponse getSummaryReport(String period);
}
