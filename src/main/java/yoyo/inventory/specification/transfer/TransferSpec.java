package yoyo.inventory.specification.transfer;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.Transfer;

import java.util.ArrayList;
import java.util.List;

public class TransferSpec {
    public static Specification<Transfer> filterBy(TransferFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter out soft deleted records
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (filter.getTransferNo() != null && !filter.getTransferNo().isEmpty()) {
                predicates.add(cb.like(root.get("transferNo"), "%" + filter.getTransferNo() + "%"));
            }
            if (filter.getFromStoreId() != null) {
                predicates.add(cb.equal(root.get("fromStoreId").get("id"), filter.getFromStoreId()));
            }
            if (filter.getToStoreId() != null) {
                predicates.add(cb.equal(root.get("toStoreId").get("id"), filter.getToStoreId()));
            }
            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filter.getFromDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
