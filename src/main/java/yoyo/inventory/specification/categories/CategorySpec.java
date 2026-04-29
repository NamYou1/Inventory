package yoyo.inventory.specification.categories;

import jakarta.persistence.criteria.Predicate;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.Category;
import yoyo.inventory.entities.status.Status;

import java.util.ArrayList;
import java.util.List;
@Data
public class CategorySpec {
    public static Specification<Category> filterBy(CategoryFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Always filter by ACTIVE status (soft delete)
            predicates.add(cb.equal(root.get("status"), Status.ACTIVE));

            if (filter.getName() != null) {
                predicates.add(cb.equal(root.get("name"), filter.getName()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
