package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import yoyo.inventory.entities.Unit;

import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit , Long> , JpaSpecificationExecutor<Unit> {
    Optional<Unit> findByName(String name);
}
