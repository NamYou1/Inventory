package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import yoyo.inventory.entities.Sale;

@Repository
public interface SaleRepository
        extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale> {
}