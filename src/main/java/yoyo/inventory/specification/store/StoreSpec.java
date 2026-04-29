package yoyo.inventory.specification.store;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.entities.Suppliers;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.specification.suppliers.SupplierFilter;

import java.util.ArrayList;
import java.util.List;

public class StoreSpec {
    public static Specification<Stores> filterBy(StoreFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Always filter by ACTIVE status (soft delete)
            predicates.add(cb.equal(root.get("status"), Status.ACTIVE));

            if (filter.getName() != null) {
                predicates.add(cb.equal(root.get("name"), filter.getName()));
            }
            if (filter.getCode() != null) {
                predicates.add(cb.equal(root.get("code"), filter.getCode()));
            }
            if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                predicates.add(cb.like(root.get("email"), "%" + filter.getEmail() + "%"));
            }
            if (filter.getPhone() != null && !filter.getPhone().isEmpty()) {
                predicates.add(cb.like(root.get("phone"), "%" + filter.getPhone() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
