package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yoyo.inventory.entities.PurchaseItem;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {
}
