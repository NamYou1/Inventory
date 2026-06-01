package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import yoyo.inventory.entities.SubCategory;

import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long>  , JpaSpecificationExecutor<SubCategory> {
    Optional<SubCategory> findByName(String name);
}
