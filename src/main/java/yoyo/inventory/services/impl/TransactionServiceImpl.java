package yoyo.inventory.services.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.entities.Transaction;
import yoyo.inventory.entities.Unit;
import yoyo.inventory.dto.response.TransactionSummaryReportResponse;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;
import yoyo.inventory.execption.ApiException;
import yoyo.inventory.repository.TransactionRepository;
import yoyo.inventory.services.TransactionService;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j 
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final EntityManager entityManager; 

    @Override
    @Transactional 
    public void logStockMovement(
            TransactionType type, Long referenceId, String referenceNo,
            Long productId, Long storeId, Long unitId,
            BigDecimal qty, BigDecimal unitQty, BigDecimal price,
            SaleStatus status, String createdBy) {

        try {
            Transaction tx = new Transaction();
            tx.setTranDate(LocalDateTime.now());
            tx.setTransactionType(type);
            tx.setReferenceId(referenceId);
            tx.setReferenceNo(referenceNo);
            tx.setUnitQuantity(unitQty);
            tx.setPricePerUnit(price);
            tx.setStatus(status);
            tx.setCreatedBy(createdBy);

            if (type == TransactionType.SALE || type == TransactionType.TRANSFER_OUT || type == TransactionType.ADJUSTMENT_OUT) {
                tx.setQuantity(qty.negate());
            } else {
                tx.setQuantity(qty);
            }

            tx.setTotalAmount(qty.abs().multiply(price));
            tx.setTblProduct(entityManager.getReference(Product.class, productId));
            tx.setTblStore(entityManager.getReference(Stores.class, storeId));
            tx.setTblUnit(entityManager.getReference(Unit.class, unitId));

            transactionRepository.save(tx);
            log.info("Stock transaction logged successfully for Ref: {}, Type: {}", referenceNo, type);

        } catch (Exception e) {
            log.error("Failed to log stock transaction for Ref: {}. Error: {}", referenceNo, e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "មិនអាចកត់ត្រាប្រតិបត្តិការស្តុកបានទេ៖ " + e.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true) 
    public BigDecimal getStockBalance(Long storeId, Long productId) {
        BigDecimal balance = transactionRepository.getStockBalance(storeId, productId);
        return balance != null ? balance : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTranDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getReportsByType(TransactionType type) {
        return transactionRepository.findByTransactionType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionSummaryReportResponse getSummaryReport(String period) {
        String normalized = period == null ? "today" : period.trim().toLowerCase();
        DateRange range = resolveRange(normalized);
        List<Transaction> transactions = transactionRepository.findByTranDateBetween(range.start(), range.end());

        long saleCount = 0L;
        BigDecimal saleAmount = BigDecimal.ZERO;
        long purchaseCount = 0L;
        BigDecimal purchaseAmount = BigDecimal.ZERO;
        long transferOutCount = 0L;
        BigDecimal transferOutAmount = BigDecimal.ZERO;
        long transferInCount = 0L;
        BigDecimal transferInAmount = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            BigDecimal amount = tx.getTotalAmount() == null ? BigDecimal.ZERO : tx.getTotalAmount();
            if (tx.getTransactionType() == TransactionType.SALE) {
                saleCount++;
                saleAmount = saleAmount.add(amount);
            } else if (tx.getTransactionType() == TransactionType.PURCHASE) {
                purchaseCount++;
                purchaseAmount = purchaseAmount.add(amount);
            } else if (tx.getTransactionType() == TransactionType.TRANSFER_OUT) {
                transferOutCount++;
                transferOutAmount = transferOutAmount.add(amount);
            } else if (tx.getTransactionType() == TransactionType.TRANSFER_IN) {
                transferInCount++;
                transferInAmount = transferInAmount.add(amount);
            }
        }

        return TransactionSummaryReportResponse.builder()
                .period(normalized)
                .startDate(range.start())
                .endDate(range.end())
                .saleCount(saleCount)
                .saleAmount(saleAmount)
                .purchaseCount(purchaseCount)
                .purchaseAmount(purchaseAmount)
                .transferOutCount(transferOutCount)
                .transferOutAmount(transferOutAmount)
                .transferInCount(transferInCount)
                .transferInAmount(transferInAmount)
                .build();
    }

    private DateRange resolveRange(String period) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        if ("today".equals(period)) {
            startDate = today;
        } else if ("week".equals(period)) {
            startDate = today.with(DayOfWeek.MONDAY);
        } else if ("month".equals(period)) {
            startDate = today.withDayOfMonth(1);
        } else if ("year".equals(period)) {
            startDate = today.withDayOfYear(1);
        } else {
            throw new IllegalArgumentException("Invalid period. Use: today, week, month, year");
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);
        return new DateRange(start, end);
    }

    private record DateRange(LocalDateTime start, LocalDateTime end) {
    }
}
