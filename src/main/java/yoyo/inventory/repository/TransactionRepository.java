package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yoyo.inventory.entities.Transaction;
import yoyo.inventory.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT SUM(t.quantity) FROM Transaction t " +
            "WHERE t.tblStore.id = :storeId AND t.tblProduct.id = :productId")
    BigDecimal getStockBalance(@Param("storeId") Long storeId, @Param("productId") Long productId);

    // ស្វែងរកការដើរទិន្នន័យស្តុកតាមចន្លោះថ្ងៃខែ (Index: idx_tran_date នឹងធ្វើការនៅទីនេះ)
    List<Transaction> findByTranDateBetween(LocalDateTime start, LocalDateTime end);

    // ស្វែងរកតាមប្រភេទ Transaction (Index: idx_tran_type_ref_id នឹងធ្វើការនៅទីនេះ)
    List<Transaction> findByTransactionType(TransactionType transactionType);
}
