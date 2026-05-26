package yoyo.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yoyo.inventory.entities.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock , Long> {
//    Optional<Stock> findByTblProductId(Long productId);
    @Query("SELECT s FROM Stock s WHERE s.quantity <= s.reorderLevel")
    List<Stock> findLowStockProducts();

    Optional<Stock> findByTblProductIdAndTblStoreId(Long productId, Long storeId);
    Page<Stock> findByTblStoreId(Long storeId, Pageable pageable);
}
