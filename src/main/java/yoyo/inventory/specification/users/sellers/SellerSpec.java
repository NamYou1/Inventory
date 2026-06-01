package yoyo.inventory.specification.users.sellers;

import jakarta.persistence.criteria.Predicate;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.Seller;
import yoyo.inventory.entities.status.Status;

import java.util.ArrayList;
import java.util.List;
@Data
public class SellerSpec {
    public static Specification<Seller> filterBy(SellerFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Always filter by ACTIVE status (soft delete)
            predicates.add(cb.equal(root.get("status"), Status.ACTIVE));

            if (filter.getName() != null) {
                predicates.add(cb.equal(root.get("name"), filter.getName()));
            }
            if (filter.getPhone() != null && !filter.getPhone().isEmpty()) {
                predicates.add(cb.like(cb.upper(root.get("phone")), "%" + filter.getPhone().toUpperCase() + "%"));
            }
            if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                predicates.add(cb.like(root.get("email"), "%" + filter.getEmail() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
