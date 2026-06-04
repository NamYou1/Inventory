package yoyo.inventory.specification.sale;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.Sale;

import java.util.ArrayList;
import java.util.List;

public class SaleSpec {
    public static Specification<Sale> filterBy(SaleFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (filter.getInvoiceNo() != null && !filter.getInvoiceNo().isEmpty()) {
                predicates.add(cb.like(root.get("invoiceNo"), "%" + filter.getInvoiceNo() + "%"));
            }
            if (filter.getStoreId() != null) {
                predicates.add(cb.equal(root.get("store").get("id"), filter.getStoreId()));
            }
            if (filter.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), filter.getCustomerId()));
            }
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getNote() != null && !filter.getNote().isEmpty()) {
                predicates.add(cb.like(root.get("note"), "%" + filter.getNote() + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
