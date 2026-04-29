package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import yoyo.inventory.entities.Suppliers;

public interface SupplierRepository extends JpaRepository<Suppliers , Long>  , JpaSpecificationExecutor<Suppliers> {
}
