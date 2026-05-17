package yoyo.inventory.specification.purchase;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.Purchases;
import yoyo.inventory.entities.status.Status;

import java.util.ArrayList;
import java.util.List;

public class PurchaseSpec {
    public static Specification<Purchases> filterBy(PurchaseFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Always filter by ACTIVE status (soft delete)
            predicates.add(cb.equal(root.get("status"), Status.ACTIVE));

            if (filter.getReference() != null && !filter.getReference().isEmpty()) {
                predicates.add(cb.like(root.get("reference"), "%" + filter.getReference() + "%"));
            }
            if (filter.getNo() != null && !filter.getNo().isEmpty()) {
                predicates.add(cb.like(root.get("no"), "%" + filter.getNo() + "%"));
            }
            if (filter.getStoreId() != null) {
                predicates.add(cb.equal(root.get("tblStore").get("id"), filter.getStoreId()));
            }
            if (filter.getSupplierId() != null) {
                predicates.add(cb.equal(root.get("tblSuppliers").get("id"), filter.getSupplierId()));
            }
            if (filter.getSellerId() != null) {
                predicates.add(cb.equal(root.get("tblSeller").get("id"), filter.getSellerId()));
            }
            if (filter.getPurchaseStatus() != null && !filter.getPurchaseStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("purchaseStatus"), filter.getPurchaseStatus()));
            }
            if (filter.getPaymentStatus() != null && !filter.getPaymentStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("paymentStatus"), filter.getPaymentStatus()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
