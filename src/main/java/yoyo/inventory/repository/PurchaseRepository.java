package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yoyo.inventory.entities.Purchases;

public interface PurchaseRepository extends JpaRepository<Purchases , Long> {
}
