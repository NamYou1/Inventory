package yoyo.inventory.services.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.entities.Transaction;
import yoyo.inventory.entities.Unit;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;
import yoyo.inventory.repository.TransactionRepository;
import yoyo.inventory.services.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // សម្រាប់កត់ត្រា Log ក្នុង Console ពេលមាន Error ងាយស្រួល Debug
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final EntityManager entityManager; // សម្រាប់ reference proxies របស់ Product, Store, Unit កុំឱ្យហៅទិន្នន័យពី DB នាំយឺត

    @Override
    @Transactional // ធានាថាការលក់/ទិញ និងការកត់ត្រា Log ស្តុក ត្រូវតែជោគជ័យជាមួយគ្នា ឬបរាជ័យជាមួយគ្នា (Atomicity)
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

            // 💡 គន្លឹះសំខាន់៖ បើលក់ ឬផ្ទេរចេញ ត្រូវកត់ត្រាតម្លៃដក (-) ដើម្បីងាយស្រួល SUM ក្នុង Report
            if (type == TransactionType.SALE || type == TransactionType.TRANSFER_OUT || type == TransactionType.ADJUSTMENT_OUT) {
                tx.setQuantity(qty.negate()); // ប្តូរទៅជាតម្លៃអវិជ្ជមាន (ឧ. -10.00)
            } else {
                tx.setQuantity(qty); // ទិញចូល, ផ្ទេរចូល, ឬកើនឡើង រក្សាតម្លៃវិជ្ជមាន (+) ធម្មតា
            }

            // គណនាទឹកប្រាក់សរុប (Quantity * Price)
            // ប្រើ abs() ដើម្បីកុំឱ្យតម្លៃទឹកប្រាក់ទៅជាដក ក្នុង Report
            tx.setTotalAmount(qty.abs().multiply(price));

            // ការប្រើgetReference ជួយឱ្យ JPA បង្កើត Proxy Object ដោយមិនបាច់ទៅ Query Table ផ្សេងនាំខាតពេល (ព្រោះយើងដឹង ID ហើយ)
            tx.setTblProduct(entityManager.getReference(Product.class, productId));
            tx.setTblStore(entityManager.getReference(Stores.class, storeId));
            tx.setTblUnit(entityManager.getReference(Unit.class, unitId));

            transactionRepository.save(tx);
            log.info("Stock transaction logged successfully for Ref: {}, Type: {}", referenceNo, type);

        } catch (Exception e) {
            log.error("Failed to log stock transaction for Ref: {}. Error: {}", referenceNo, e.getMessage());
            throw new RuntimeException("មិនអាចកត់ត្រាប្រតិបត្តិការស្តុកបានទេ៖ " + e.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true) // readOnly = true ជួយឱ្យ Query ដើរលឿនជាងមុន
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
}