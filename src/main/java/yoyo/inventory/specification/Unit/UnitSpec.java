package yoyo.inventory.specification.Unit;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.Unit;
import yoyo.inventory.entities.status.Status;

import java.util.ArrayList;
import java.util.List;

public class UnitSpec {
    public static Specification<Unit> filterBy(UnitFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Always filter by ACTIVE status (soft delete)
            predicates.add(cb.equal(root.get("status"), Status.ACTIVE));

            if (filter.getName() != null) {
                predicates.add(cb.equal(root.get("name"), filter.getName()));
            }
            if (filter.getCode() != null && !filter.getCode().isEmpty()) {
                predicates.add(cb.like(cb.upper(root.get("code")), "%" + filter.getCode().toUpperCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
