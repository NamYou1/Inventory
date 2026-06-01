package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import yoyo.inventory.entities.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category , Long> , JpaSpecificationExecutor<Category> {
    Optional<Category> findByName(String name);
}
