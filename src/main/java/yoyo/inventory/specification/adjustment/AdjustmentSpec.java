package yoyo.inventory.specification.adjustment;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.StockAdjustment;
import yoyo.inventory.entities.status.Status;


import java.util.ArrayList;
import java.util.List;

public class AdjustmentSpec {
    public static Specification<StockAdjustment> filterBy(AdjustmentFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Always filter by ACTIVE status (soft delete)
            predicates.add(cb.equal(root.get("status"), Status.ACTIVE));

            if (filter.getReferenceNo() != null) {
                predicates.add(cb.equal(root.get("name"), filter.getReferenceNo()));
            }
            if (filter.getProductId() != null) {
                predicates.add(cb.equal(root.get("productId"), filter.getProductId()));
            }
            if (filter.getStoreId() != null) {
                predicates.add(cb.equal(root.get("storeId"), filter.getStoreId()));
            }
            if (filter.getAdjustmentDate() != null) {
                predicates.add(cb.equal(root.get("adjustmentDate"), filter.getAdjustmentDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
