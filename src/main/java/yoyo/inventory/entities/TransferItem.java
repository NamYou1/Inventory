package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tbl_transfer_items",
        indexes = {
                @Index(name = "idx_transfer_item_transfer", columnList = "transfer_id"),
                @Index(name = "idx_transfer_item_product", columnList = "product_id"),
                @Index(name = "idx_transfer_item_unit", columnList = "unit_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantity;
    @Column(name = "unit_price", nullable = false, precision = 14, scale = 4)
    private BigDecimal unitPrice = BigDecimal.ZERO;
    @Column(name = "cost_price", nullable = false, precision = 14, scale = 4)
    private BigDecimal costPrice = BigDecimal.ZERO;
    @Column(name = "unit_quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal unitQuantity = BigDecimal.ZERO;
    @Column(name = "subtotal", nullable = false, precision = 14, scale = 4)
    private BigDecimal subtotal = BigDecimal.ZERO;

   // relationship
    @JoinColumn(name = "unit_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Unit unit  ;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", nullable = false)
    private Transfer transfer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    // Auto-calculate subtotal before persist/update
    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        if (quantity != null && unitPrice != null) {
            this.subtotal = quantity.multiply(unitPrice);
        }
    }
}
