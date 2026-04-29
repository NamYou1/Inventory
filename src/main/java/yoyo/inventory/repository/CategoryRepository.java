package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import yoyo.inventory.entities.Category;

public interface CategoryRepository extends JpaRepository<Category , Long> , JpaSpecificationExecutor<Category> {
}
