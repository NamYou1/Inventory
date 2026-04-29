package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import yoyo.inventory.entities.Stores;

public interface StoreRepository extends JpaRepository<Stores , Long> , JpaSpecificationExecutor<Stores> {
}
