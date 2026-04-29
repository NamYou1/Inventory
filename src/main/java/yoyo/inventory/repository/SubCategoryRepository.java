package yoyo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import yoyo.inventory.entities.SubCategory;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long>  , JpaSpecificationExecutor<SubCategory> {
}
