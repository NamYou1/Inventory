package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import yoyo.inventory.entities.Purchases;

public interface PurchaseRepository extends JpaRepository<Purchases , Long>, JpaSpecificationExecutor<Purchases> {
}
