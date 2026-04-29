package yoyo.inventory.specification.suppliers;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.Seller;
import yoyo.inventory.entities.Suppliers;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.specification.sellers.SellerFilter;

import java.util.ArrayList;
import java.util.List;

public class SupplierSpec {
    public static Specification<Suppliers> filterBy(SupplierFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Always filter by ACTIVE status (soft delete)
            predicates.add(cb.equal(root.get("status"), Status.ACTIVE));

            if (filter.getName() != null) {
                predicates.add(cb.equal(root.get("name"), filter.getName()));
            }
            if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                predicates.add(cb.like(root.get("email"), "%" + filter.getEmail() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
