package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import yoyo.inventory.entities.Seller;

public interface SellerRepository extends JpaRepository<Seller , Long>  , JpaSpecificationExecutor<Seller> {
}
